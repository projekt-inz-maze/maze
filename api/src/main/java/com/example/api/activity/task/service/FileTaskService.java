package com.example.api.activity.task.service;

import com.example.api.activity.task.dto.request.create.CreateFileTaskChapterForm;
import com.example.api.activity.task.dto.request.create.CreateFileTaskForm;
import com.example.api.activity.task.dto.request.edit.EditFileTaskForm;
import com.example.api.activity.task.dto.response.FileTaskInfoResponse;
import com.example.api.activity.task.dto.response.util.FileResponse;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.activity.feedback.model.ProfessorFeedback;
import com.example.api.activity.result.model.FileTaskResult;
import com.example.api.activity.task.model.FileTask;
import com.example.api.map.model.Chapter;
import com.example.api.user.model.User;
import com.example.api.activity.feedback.repository.ProfessorFeedbackRepository;
import com.example.api.activity.result.repository.FileTaskResultRepository;
import com.example.api.activity.task.repository.FileTaskRepository;
import com.example.api.map.repository.ChapterRepository;
import com.example.api.user.repository.UserRepository;
import com.example.api.security.AuthenticationService;
import com.example.api.map.service.RequirementService;
import com.example.api.validator.ChapterValidator;
import com.example.api.validator.UserValidator;
import com.example.api.activity.validator.ActivityValidator;
import com.example.api.util.calculator.TimeParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FileTaskService {
    private final FileTaskRepository fileTaskRepository;
    private final FileTaskResultRepository fileTaskResultRepository;
    private final ProfessorFeedbackRepository professorFeedbackRepository;
    private final UserRepository userRepository;
    private final ChapterRepository chapterRepository;
    private final UserValidator userValidator;
    private final AuthenticationService authService;
    private final ActivityValidator activityValidator;
    private final TimeParser timeParser;
    private final RequirementService requirementService;
    private final ChapterValidator chapterValidator;

    public FileTask saveFileTask(FileTask fileTask) {
        return fileTaskRepository.save(fileTask);
    }

    public FileTaskInfoResponse getFileTaskInfo(Long id) throws EntityNotFoundException, WrongUserTypeException {
        String email = authService.getAuthentication().getName();
        FileTaskInfoResponse result = new FileTaskInfoResponse();
        FileTask fileTask = fileTaskRepository.findFileTaskById(id);
        activityValidator.validateActivityIsNotNull(fileTask, id);
        result.setFileTaskId(fileTask.getId());
        result.setName(fileTask.getTitle());
        result.setDescription(fileTask.getDescription());

        User student = userRepository.findUserByEmail(email);
        userValidator.validateStudentAccount(student, email);
        FileTaskResult fileTaskResult = fileTaskResultRepository.findFileTaskResultByFileTaskAndUser(fileTask, student);
        if(fileTaskResult == null){
            log.debug("File task result for {} and file task with id {} does not exist", email, fileTask.getId());
            return result;
        }
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
        activityValidator.validateFileTaskTitle(form.getTitle(), fileTasks);

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        String email = authService.getAuthentication().getName();
        User professor = userRepository.findUserByEmail(email);
        userValidator.validateProfessorAccount(professor, email);

        FileTask fileTask = new FileTask(form, professor, null);
        fileTask.setRequirements(requirementService.getDefaultRequirements(true));
        fileTaskRepository.save(fileTask);
        chapter.getActivityMap().getFileTasks().add(fileTask);
    }

    public List<FileTask> getStudentFileTasks() {
        return fileTaskRepository.findAll()
                .stream()
                .filter(fileTask -> !fileTask.getIsBlocked())
                .toList();
    }

    public void editFileTask(FileTask fileTask, EditFileTaskForm form) {
        CreateFileTaskForm fileTaskForm = (CreateFileTaskForm) form.getActivityBody();
        fileTask.setRequiredKnowledge(fileTaskForm.getRequiredKnowledge());
        editMaxPoints(fileTask, fileTaskForm.getMaxPoints());
    }

    public void editMaxPoints(FileTask fileTask, Double newMaxPoints) {
        fileTaskResultRepository.findAllByFileTask(fileTask)
                .stream()
                .filter(FileTaskResult::isEvaluated)
                .forEach(fileTaskResult -> {
                    Double prevPoints = fileTaskResult.getPointsReceived();
                    Double newPoints = prevPoints * (newMaxPoints / fileTask.getMaxPoints());
                    ProfessorFeedback feedback = professorFeedbackRepository.findProfessorFeedbackByFileTaskResult(fileTaskResult);
                    if (feedback != null) {
                        feedback.setPoints(newPoints);
                    }
                    fileTaskResult.setPointsReceived(newPoints);
                });
        fileTask.setMaxPoints(newMaxPoints);
    }
}
