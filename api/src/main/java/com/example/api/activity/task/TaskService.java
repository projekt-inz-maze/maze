package com.example.api.activity.task;

import com.example.api.activity.auction.AuctionRepository;
import com.example.api.activity.task.dto.response.ActivitiesResponse;
import com.example.api.activity.task.dto.response.ActivityToEvaluateResponse;
import com.example.api.activity.task.dto.response.util.FileResponse;
import com.example.api.course.model.Course;
import com.example.api.course.service.CourseService;
import com.example.api.course.validator.CourseValidator;
import com.example.api.chapter.requirement.RequirementDTO;
import com.example.api.chapter.requirement.RequirementResponse;
import com.example.api.activity.ActivityType;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.activity.result.model.FileTaskResult;
import com.example.api.activity.Activity;
import com.example.api.activity.task.filetask.FileTask;
import com.example.api.map.ActivityMap;
import com.example.api.chapter.Chapter;
import com.example.api.chapter.requirement.model.Requirement;
import com.example.api.security.LoggedInUserService;
import com.example.api.user.model.User;
import com.example.api.activity.result.repository.FileTaskResultRepository;
import com.example.api.activity.task.filetask.FileTaskRepository;
import com.example.api.activity.task.graphtask.GraphTaskRepository;
import com.example.api.activity.info.InfoRepository;
import com.example.api.activity.survey.SurveyRepository;
import com.example.api.chapter.ChapterRepository;
import com.example.api.chapter.requirement.RequirementService;
import com.example.api.activity.validator.ActivityValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TaskService {
    private final FileTaskRepository fileTaskRepository;
    private final GraphTaskRepository graphTaskRepository;
    private final SurveyRepository surveyRepository;
    private final InfoRepository infoRepository;
    private final FileTaskResultRepository fileTaskResultRepository;
    private final ChapterRepository chapterRepository;
    private final ActivityValidator activityValidator;
    private final RequirementService requirementService;
    private final CourseValidator courseValidator;
    private final CourseService courseService;
    private final LoggedInUserService authService;
    private  final AuctionRepository auctionRepository;
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
        List<List<ActivitiesResponse>> activitiesResponses = new LinkedList<>();
        chapters.forEach(chapter -> {
            ActivityMap activityMap = chapter.getActivityMap();
            List<ActivitiesResponse> graphTasks = activityMap.getGraphTasks()
                    .stream()
                    .map(graphTask -> new ActivitiesResponse(graphTask.getId(), graphTask.getTitle(), chapter.getName(), ActivityType.EXPEDITION))
                    .toList();
            List<ActivitiesResponse> fileTasks = activityMap.getFileTasks()
                    .stream()
                    .map(fileTask -> new ActivitiesResponse(fileTask.getId(), fileTask.getTitle(), chapter.getName(), ActivityType.TASK))
                    .toList();
            List<ActivitiesResponse> surveys = activityMap.getSurveys()
                    .stream()
                    .map(survey -> new ActivitiesResponse(survey.getId(), survey.getTitle(), chapter.getName(), ActivityType.SURVEY))
                    .toList();
            List<ActivitiesResponse> responses = Stream.of(graphTasks, fileTasks, surveys)
                    .flatMap(Collection::stream)
                    .toList();
            activitiesResponses.add(responses);
        });
        return activitiesResponses.stream()
                .flatMap(Collection::stream)
                .toList();
    }

    public RequirementResponse getRequirementsForActivity(Long id) throws EntityNotFoundException {
        Activity activity = getActivity(id);

        List<? extends RequirementDTO<?>> requirements = activity.getRequirements()
                .stream()
                .map(Requirement::getResponse)
                .sorted(Comparator.comparingLong(RequirementDTO::getId))
                .toList();
        return new RequirementResponse(activity.getIsBlocked(), requirements);
    }

    public void updateRequirementForActivity(ActivityRequirementForm form) throws RequestValidationException, CannotEditRequirementsForAuctionedTaskException {
        Activity activity = getActivity(form.getActivityId());

        Boolean isBlocked = form.getIsBlocked();
        if (isBlocked != null) {
            activity.setIsBlocked(isBlocked);
        }
        List<RequirementForm> requirementForms = form.getRequirements();
        requirementService.updateRequirements(requirementForms);
    }

    public Activity getActivity(Long id) throws EntityNotFoundException {
        if (graphTaskRepository.existsById(id)) {
            return graphTaskRepository.findGraphTaskById(id);
        } else if (fileTaskRepository.existsById(id)) {
            return fileTaskRepository.findFileTaskById(id);
        } else if (surveyRepository.existsById(id)) {
            return surveyRepository.findSurveyById(id);
        } else if (infoRepository.existsById(id)) {
            return infoRepository.findInfoById(id);
        } else if (auctionRepository.existsById(id)) {
            return auctionRepository.findById(id).get();
        } else {
            log.error("Activity with id {} not found in database", id);
            throw new EntityNotFoundException("Activity with id " + id + " not found in database");
        }
    }
}
