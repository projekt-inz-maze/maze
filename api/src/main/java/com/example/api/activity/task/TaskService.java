package com.example.api.activity.task;

import com.example.api.activity.Activity;
import com.example.api.activity.ActivityRepository;
import com.example.api.activity.result.model.FileTaskResult;
import com.example.api.activity.result.repository.FileTaskResultRepository;
import com.example.api.activity.task.dto.response.ActivitiesResponse;
import com.example.api.activity.task.dto.response.ActivityToEvaluateResponse;
import com.example.api.activity.task.dto.response.util.FileResponse;
import com.example.api.activity.task.filetask.FileTask;
import com.example.api.activity.task.filetask.FileTaskRepository;
import com.example.api.activity.validator.ActivityValidator;
import com.example.api.chapter.Chapter;
import com.example.api.chapter.ChapterRepository;
import com.example.api.chapter.requirement.RequirementDTO;
import com.example.api.chapter.requirement.RequirementResponse;
import com.example.api.chapter.requirement.RequirementService;
import com.example.api.chapter.requirement.model.Requirement;
import com.example.api.course.Course;
import com.example.api.course.CourseService;
import com.example.api.course.CourseValidator;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.security.LoggedInUserService;
import com.example.api.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TaskService {
    private final FileTaskRepository fileTaskRepository;
    private final FileTaskResultRepository fileTaskResultRepository;
    private final ChapterRepository chapterRepository;
    private final ActivityValidator activityValidator;
    private final RequirementService requirementService;
    private final CourseValidator courseValidator;
    private final CourseService courseService;
    private final LoggedInUserService authService;
    private final ActivityRepository activityRepository;

    public List<ActivityToEvaluateResponse> getAllActivitiesToEvaluate(Long courseId)
            throws RequestValidationException, UsernameNotFoundException {
        User professor = authService.getCurrentUser();
        Course course = courseService.getCourse(courseId);
        courseValidator.validateCourseOwner(course, professor);
        log.info("Fetching all activities that are needed to be evaluated for professor {}", professor.getEmail());

        List<ActivityToEvaluateResponse> response = new LinkedList<>();
        List<FileTask> fileTasks = fileTaskRepository.findFileTasksByCourse(course)
                .stream()
                .filter(fileTask -> fileTask.getProfessor().equals(professor))
                .toList();
        List<FileTaskResult> fileTaskResults = fileTaskResultRepository.findAll();
        for (FileTask task : fileTasks) {
            long num = fileTaskResults.stream()
                    .filter(result -> Objects.equals(result.getFileTask().getId(), task.getId()))
                    .filter(result -> !result.isEvaluated())
                    .count();
            response.add(new ActivityToEvaluateResponse(task.getId(), num));
        }
        return response;
    }

    public TaskToEvaluateResponse getFirstAnswerToEvaluate(Long id) throws EntityNotFoundException {
        log.info("Fetching first activity that is needed to be evaluated for file task with id {}", id);
        FileTask task = fileTaskRepository.findFileTaskById(id);
        activityValidator.validateActivityIsNotNull(task, id);

        List<FileTaskResult> fileTaskResults = fileTaskResultRepository.findAll()
                .stream()
                .filter(result -> Objects.equals(result.getFileTask().getId(), task.getId()))
                .filter(result -> !result.isEvaluated())
                .toList();

        if (fileTaskResults.isEmpty()) return null;

        FileTaskResult result = fileTaskResults.get(0);
        long num = fileTaskResults.size();
        boolean isLate = false;
        Long sendDateMillis = result.getSendDateMillis();

        if (sendDateMillis != null){
            isLate = task.getRequirements()
                    .stream()
                    .map(Requirement::getDateToMillis)
                    .filter(Objects::nonNull)
                    .anyMatch(dateTo -> sendDateMillis > dateTo);
        }

        List<FileResponse> filesResponse = result.getFiles().stream().map(FileResponse::new).toList();

        return new TaskToEvaluateResponse(result.getMember().getUser(),
                result.getId(),
                task.getTitle(),
                isLate,
                task.getDescription(),
                result.getAnswer(),
                filesResponse,
                task.getMaxPoints(),
                id,
                num-1
        );
    }

    public List<ActivitiesResponse> getAllActivities(Long courseId) throws EntityNotFoundException {
        log.info("Fetching all activities for course {}", courseId);
        Course course = courseService.getCourse(courseId);
        courseValidator.validateCurrentUserCanAccess(courseId);

        List<Chapter> chapters = chapterRepository.findAllByCourse(course);

        return  chapters.stream().flatMap(chapter ->
            chapter.getActivityMap()
                    .getAllActivities()
                    .stream()
                    .map(activity -> new ActivitiesResponse(activity, chapter.getName())))
                .toList();
    }

    public RequirementResponse getRequirementsForActivity(Long id) throws EntityNotFoundException {
        Activity activity = getActivity(id);
        return getRequirementsForActivity(activity);
    }

    public RequirementResponse getRequirementsForActivity(Activity activity) {
        List<? extends RequirementDTO<?>> requirements = activity.getRequirements()
                .stream()
                .map(Requirement::getResponse)
                .sorted(Comparator.comparingLong(RequirementDTO::getId))
                .toList();
        return new RequirementResponse(activity.getIsBlocked(), requirements);
    }

    public void updateRequirementForActivity(ActivityRequirementForm form) throws RequestValidationException {
        Activity activity = getActivity(form.getActivityId());

        Boolean isBlocked = form.getIsBlocked();
        if (isBlocked != null) {
            activity.setIsBlocked(isBlocked);
        }
        List<RequirementForm> requirementForms = form.getRequirements();
        requirementService.updateRequirements(requirementForms);
    }

    public Activity getActivity(Long id) throws EntityNotFoundException {
        return activityRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Activity was not found"));
    }
}
