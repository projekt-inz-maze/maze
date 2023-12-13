package com.example.api.activity.submittask;

import com.example.api.activity.CreateSubmitTaskChapterForm;
import com.example.api.activity.submittask.result.SubmitTaskResult;
import com.example.api.activity.submittask.result.SubmitTaskResultDTO;
import com.example.api.activity.submittask.result.SubmitTaskResultRepository;
import com.example.api.activity.submittask.result.SubmitTaskResultWithFileDTO;
import com.example.api.activity.task.filetask.CreateFileTaskForm;
import com.example.api.activity.validator.ActivityValidator;
import com.example.api.chapter.Chapter;
import com.example.api.chapter.ChapterRepository;
import com.example.api.chapter.requirement.RequirementService;
import com.example.api.course.coursemember.CourseMember;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.security.LoggedInUserService;
import com.example.api.user.model.User;
import com.example.api.user.service.UserService;
import com.example.api.file.File;
import com.example.api.file.FileRepository;
import com.example.api.validator.ChapterValidator;
import com.example.api.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SubmitTaskService {
    private final ChapterRepository chapterRepository;
    private final ChapterValidator chapterValidator;
    private final ActivityValidator activityValidator;
    private final SubmitTaskRepository submitTaskRepository;
    private final LoggedInUserService authService;
    private final UserValidator userValidator;
    private final RequirementService requirementService;
    private final UserService userService;
    private final SubmitTaskResultRepository submitTaskResultRepository;
    private final FileRepository fileRepository;

    public Long createSubmitTask(CreateSubmitTaskChapterForm chapterForm) throws RequestValidationException {
        log.info("Starting the creation of submit task");
        CreateSubmitTaskForm form = chapterForm.getForm();
        Chapter chapter = chapterRepository.findChapterById(chapterForm.getChapterId());
        chapterValidator.validateChapterIsNotNull(chapter, chapterForm.getChapterId());
        activityValidator.validateActivityPosition(form, chapter);
        activityValidator.validateActivityTitle(form.getTitle(), submitTaskRepository.existsByTitle(form.getTitle()));

        User professor = authService.getCurrentUser();
        userValidator.validateProfessorAccount(professor);

        SubmitTask submitTask = new SubmitTask(form, professor, chapter.getCourse());
        submitTask.setRequirements(requirementService.getDefaultRequirements(true));
        submitTaskRepository.save(submitTask);
        chapter.getActivityMap().add(submitTask);

        return submitTask.getId();
    }

    public Long createResultForSubmitTask(SubmitTaskResultDTO dto) throws WrongUserTypeException, EntityNotFoundException {
        User student = userService.getCurrentUserAndValidateStudentAccount();
        SubmitTask task = submitTaskRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Activity not found"));

        CourseMember member = student.getCourseMember(task.getCourse(), true);

        SubmitTaskResult result = new SubmitTaskResult(dto, member, task);
        submitTaskResultRepository.save(result);
        return result.getId();
    }

    public Long createResultForSubmitTask(SubmitTaskResultWithFileDTO dto) throws WrongUserTypeException, EntityNotFoundException, IOException {
        User student = userService.getCurrentUserAndValidateStudentAccount();
        SubmitTask task = submitTaskRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Activity not found"));

        CourseMember member = student.getCourseMember(task.getCourse(), true);

        SubmitTaskResult result = new SubmitTaskResult(dto, member, task);

        if (dto.getFile() != null) {
            File file = new File(null, dto.getFileName(), dto.getFile().getBytes());
            fileRepository.save(file);
            result.getFiles().add(file);
        }

        submitTaskResultRepository.save(result);

        return result.getId();
    }


    public void rejectResult(Long id) {
        SubmitTaskResult result = submitTaskResultRepository.findById(id).orElseThrow(() -> new javax.persistence.EntityNotFoundException("Result not found"));
        result.setEvaluated(true);
        submitTaskResultRepository.save(result);
    }

    public CreateFileTaskForm acceptResult(Long id) {
        SubmitTaskResult result = submitTaskResultRepository.findById(id).orElseThrow(() -> new javax.persistence.EntityNotFoundException("Result not found"));
        result.setEvaluated(true);
        submitTaskResultRepository.save(result);

        CreateFileTaskForm fileTask = CreateFileTaskForm.example();
        fileTask.setTitle(result.getSubmittedTitle());
        fileTask.setTaskContent(result.getSubmittedContent());
        fileTask.setBasedOn(Optional.of(id));

        return fileTask;
    }
}
