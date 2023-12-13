package com.example.api.activity.task.filetask;

import com.example.api.activity.auction.AuctionService;
import com.example.api.activity.submittask.result.SubmitTaskResultRepository;
import com.example.api.file.FileResponse;
import com.example.api.course.Course;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.activity.feedback.ProfessorFeedback;
import com.example.api.activity.result.model.FileTaskResult;
import com.example.api.chapter.Chapter;
import com.example.api.user.model.User;
import com.example.api.activity.feedback.ProfessorFeedbackRepository;
import com.example.api.activity.result.repository.FileTaskResultRepository;
import com.example.api.chapter.ChapterRepository;
import com.example.api.security.LoggedInUserService;
import com.example.api.chapter.requirement.RequirementService;
import com.example.api.validator.ChapterValidator;
import com.example.api.validator.UserValidator;
import com.example.api.activity.validator.ActivityValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FileTaskService {
    private final FileTaskRepository fileTaskRepository;
    private final FileTaskResultRepository fileTaskResultRepository;
    private final ProfessorFeedbackRepository professorFeedbackRepository;
    private final ChapterRepository chapterRepository;
    private final UserValidator userValidator;
    private final LoggedInUserService authService;
    private final ActivityValidator activityValidator;
    private final RequirementService requirementService;
    private final ChapterValidator chapterValidator;
    private final AuctionService auctionService;
    private final SubmitTaskResultRepository submitTaskResultRepository;

    public FileTask saveFileTask(FileTask fileTask) {
        return fileTaskRepository.save(fileTask);
    }

    public FileTaskDetailsResponse getFileTaskDetails(Long id) throws EntityNotFoundException, WrongUserTypeException {
        FileTaskDetailsResponse result = new FileTaskDetailsResponse();
        FileTask fileTask = fileTaskRepository.findFileTaskById(id);
        activityValidator.validateActivityIsNotNull(fileTask, id);
        result.setFileTaskId(fileTask.getId());
        result.setName(fileTask.getTitle());
        result.setDescription(fileTask.getRequiredKnowledge());

        User student = authService.getCurrentUser();
        userValidator.validateStudentAccount(student);

        List<FileResponse> filesList = fileTask.getFiles()
                .stream()
                .map(file -> new FileResponse(file.getId(), file.getName()))
                .toList();
        result.setFiles(filesList);

        Optional<FileTaskResult> maybeFileTaskResult =
                Optional.ofNullable(fileTaskResultRepository.findFileTaskResultByFileTaskAndUser(fileTask, student));

        if (maybeFileTaskResult.isPresent()) {
            FileTaskResult fileTaskResult = maybeFileTaskResult.get();
            result.setAnswer(fileTaskResult.getAnswer());
            List<FileResponse> fileResponseList = fileTaskResult.getFiles()
                    .stream()
                    .map(file -> new FileResponse(file.getId(), file.getName()))
                    .toList();
            result.setTaskFiles(fileResponseList);

            ProfessorFeedback feedback = professorFeedbackRepository.findProfessorFeedbackByFileTaskResult(fileTaskResult);
            if (feedback == null) {
                log.debug("Feedback for file task result with id {} does not exist", fileTaskResult.getId());
                return result;
            }
            result.setPoints(feedback.getPoints());
            result.setRemarks(feedback.getContent());
            if (feedback.getFeedbackFile() != null) {
                result.setFeedbackFile(new FileResponse(feedback.getFeedbackFile()));
            }
        } else {
            log.debug("File task result for {} and file task with id {} does not exist", student.getEmail(), fileTask.getId());
        }

        return result;
    }

    public void createFileTask(CreateFileTaskChapterForm chapterForm) throws RequestValidationException {
        log.info("Starting the creation of file task");
        CreateFileTaskForm form = chapterForm.getForm();
        Chapter chapter = chapterRepository.findChapterById(chapterForm.getChapterId());

        chapterValidator.validateChapterIsNotNull(chapter, chapterForm.getChapterId());
        activityValidator.validateCreateFileTaskFormFields(form);
        activityValidator.validateActivityPosition(form, chapter);

        List<FileTask> fileTasks = fileTaskRepository.findAll();
        activityValidator.validateActivityTitle(form.getTitle(), fileTasks);

        User professor = authService.getCurrentUser();
        userValidator.validateProfessorAccount(professor);

        FileTask fileTask = new FileTask(form, professor, chapter.getCourse());
        fileTask.setRequirements(requirementService.getDefaultRequirements(true));
        fileTaskRepository.save(fileTask);
        chapter.getActivityMap().getFileTasks().add(fileTask);

        if (form.getAuction() != null) {
            auctionService.createAuction(fileTask, form.getAuction(), chapter.getActivityMap());
        }

        form.getBasedOn()
                .flatMap(submitTaskResultRepository::findById)
                .ifPresent(submitResult -> {
                    fileTask.setAuthoredByStudent(submitResult);
                    submitResult.setPoints(0D);
                });
    }

    public List<FileTask> getStudentFileTasks(Course course) {
        return fileTaskRepository.findAllByCourseAndIsBlockedFalse(course);
    }

    public void editFileTask(FileTask fileTask, EditFileTaskForm form) {
        CreateFileTaskForm fileTaskForm = (CreateFileTaskForm) form.getActivityBody();
        fileTask.setRequiredKnowledge(fileTaskForm.getRequiredKnowledge());
        editMaxPoints(fileTask, fileTaskForm.getMaxPoints());
    }

    public void editMaxPoints(FileTask fileTask, Double newMaxPoints) {
        fileTaskResultRepository.findAllByActivity(fileTask)
                .stream()
                .filter(FileTaskResult::isEvaluated)
                .forEach(fileTaskResult -> {
                    Double prevPoints = fileTaskResult.getPoints();
                    Double newPoints = prevPoints * (newMaxPoints / fileTask.getMaxPoints());
                    ProfessorFeedback feedback = professorFeedbackRepository.findProfessorFeedbackByFileTaskResult(fileTaskResult);
                    if (feedback != null) {
                        feedback.setPoints(newPoints);
                    }
                    fileTaskResult.setPoints(newPoints);
                });
        fileTask.setMaxPoints(newMaxPoints);
    }
}
