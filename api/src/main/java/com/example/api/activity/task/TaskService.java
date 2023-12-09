package com.example.api.activity.task;

import com.example.api.activity.Activity;
import com.example.api.activity.ActivityRepository;
import com.example.api.activity.result.model.ActivityResult;
import com.example.api.activity.result.model.FileTaskResult;
import com.example.api.activity.result.repository.FileTaskResultRepository;
import com.example.api.activity.submittask.result.SubmitTaskResult;
import com.example.api.activity.submittask.result.SubmitTaskResultRepository;
import com.example.api.activity.task.dto.response.ActivitiesResponse;
import com.example.api.activity.task.dto.response.ActivityToEvaluateResponse;
import com.example.api.activity.task.dto.response.util.FileResponse;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TaskService {
    private final FileTaskResultRepository fileTaskResultRepository;
    private final ChapterRepository chapterRepository;
    private final RequirementService requirementService;
    private final CourseValidator courseValidator;
    private final CourseService courseService;
    private final LoggedInUserService authService;
    private final ActivityRepository activityRepository;
    private final SubmitTaskResultRepository submitTaskResultRepository;

    public List<ActivityToEvaluateResponse> getAllActivitiesToEvaluate(Long courseId)
            throws RequestValidationException, UsernameNotFoundException {
        User professor = authService.getCurrentUser();
        Course course = courseService.getCourse(courseId);
        log.info("Fetching all activities that are needed to be evaluated for professor {}", professor.getEmail());
        return
        Stream.concat(fileTaskResultRepository.findAllByMember_CourseIsAndActivity_ProfessorIs(course, professor).stream(),
                        submitTaskResultRepository.findAllByMember_CourseIsAndActivity_ProfessorIs(course, professor).stream())
                .filter(result -> !result.isEvaluated())
                .collect(Collectors.groupingBy(ActivityResult::getActivity))
                .entrySet()
                .stream()
                .map(entry -> new ActivityToEvaluateResponse(entry.getKey().getId(),
                        entry.getKey().getActivityType(),
                        (long) entry.getValue().size()))
                .toList();
    }

    public TaskToEvaluateResponse getFirstAnswerToEvaluate(Long id) throws EntityNotFoundException {
        log.info("Fetching first activity that is needed to be evaluated for file task with id {}", id);
        Activity task = activityRepository.findById(id).orElseThrow(() -> new javax.persistence.EntityNotFoundException("activty not found"));
        switch (task.getActivityType()) {
            case TASK -> {
                return getFirstAnswerToEvaluateForFileTask(task);
            }
            case SUBMIT -> {
                return getFirstAnswerToEvaluateForSubmitTask(task);
            }
            default -> throw new EntityNotFoundException("Invalid activity type");
        }
    }

    private TaskToEvaluateResponse getFirstAnswerToEvaluateForFileTask(Activity task) {
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

        return TaskToEvaluateResponse.builder()
                .from(result)
                .withUser(result.getMember().getUser())
                .withIsLate(isLate)
                .withUserAnswer(result.getAnswer())
                .withFile(filesResponse)
                .withRemaining(num - 1)
                .build();
    }

    private TaskToEvaluateResponse getFirstAnswerToEvaluateForSubmitTask(Activity task) {
        List<SubmitTaskResult> results = submitTaskResultRepository.findAllByActivityAndEvaluatedIs(task, false);
        long num = results.size();

        return results.stream().findAny().map(result -> {
            boolean isLate = false;
            Long sendDateMillis = result.getSendDateMillis();

            if (sendDateMillis != null){
                isLate = task.getRequirements()
                        .stream()
                        .map(Requirement::getDateToMillis)
                        .filter(Objects::nonNull)
                        .anyMatch(dateTo -> sendDateMillis > dateTo);
            }


            return TaskToEvaluateResponse.builder()
                    .from(result)
                    .withUser(result.getMember().getUser())
                    .withIsLate(isLate)
                    .withUserTitle(result.getSubmittedTitle())
                    .withUserContent(result.getSubmittedContent())
                    .withRemaining(num - 1)
                    .withFile(result.getFiles().stream().map(FileResponse::new).toList())
                    .build();
        }).orElse(null);
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
