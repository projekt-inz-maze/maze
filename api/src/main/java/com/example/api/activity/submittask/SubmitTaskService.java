package com.example.api.activity.submittask;

import com.example.api.activity.CreateActivityChapterForm;
import com.example.api.activity.submittask.result.SubmitTaskResult;
import com.example.api.activity.submittask.result.SubmitTaskResultDTO;
import com.example.api.activity.submittask.result.SubmitTaskResultRepository;
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
import com.example.api.validator.ChapterValidator;
import com.example.api.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

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

    public Long createSubmitTask(CreateActivityChapterForm chapterForm) throws RequestValidationException {
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

    public void createResultForSubmitTask(SubmitTaskResultDTO dto) throws WrongUserTypeException, EntityNotFoundException {
        User student = userService.getCurrentUserAndValidateStudentAccount();
        SubmitTask task = submitTaskRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Activity not found"));

        CourseMember member = student.getCourseMember(task.getCourse(), true);

        SubmitTaskResult result = new SubmitTaskResult(dto, member);
        submitTaskResultRepository.save(result);
    }
}
