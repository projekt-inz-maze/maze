package com.example.api.service.activity.task;

import com.example.api.dto.request.activity.task.requirement.ActivityRequirementForm;
import com.example.api.dto.request.activity.task.requirement.RequirementForm;
import com.example.api.dto.response.activity.task.ActivitiesResponse;
import com.example.api.dto.response.activity.task.ActivityToEvaluateResponse;
import com.example.api.dto.response.activity.task.TaskToEvaluateResponse;
import com.example.api.dto.response.activity.task.util.FileResponse;
import com.example.api.dto.response.map.RequirementDTO;
import com.example.api.dto.response.map.RequirementResponse;
import com.example.api.dto.response.map.task.ActivityType;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.MissingAttributeException;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.model.activity.result.FileTaskResult;
import com.example.api.model.activity.task.Activity;
import com.example.api.model.activity.task.FileTask;
import com.example.api.model.map.ActivityMap;
import com.example.api.model.map.Chapter;
import com.example.api.model.map.requirement.Requirement;
import com.example.api.model.user.User;
import com.example.api.repository.activity.result.ProfessorFeedbackRepository;
import com.example.api.repository.activity.result.FileTaskResultRepo;
import com.example.api.repository.activity.task.FileTaskRepository;
import com.example.api.repository.activity.task.GraphTaskRepository;
import com.example.api.repository.activity.task.InfoRepository;
import com.example.api.repository.activity.task.SurveyRepository;
import com.example.api.repository.map.ChapterRepository;
import com.example.api.repository.user.UserRepository;
import com.example.api.security.AuthenticationService;
import com.example.api.service.map.RequirementService;
import com.example.api.service.validator.UserValidator;
import com.example.api.service.validator.activity.ActivityValidator;
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
    private final FileTaskResultRepo fileTaskResultRepo;
    private final UserRepository userRepository;
    private final ChapterRepository chapterRepository;
    private final ProfessorFeedbackRepository professorFeedbackRepository;
    private final AuthenticationService authService;
    private final UserValidator userValidator;
    private final ActivityValidator activityValidator;
    private final RequirementService requirementService;

    public List<ActivityToEvaluateResponse> getAllActivitiesToEvaluate()
            throws WrongUserTypeException, UsernameNotFoundException {
        String email = authService.getAuthentication().getName();
        log.info("Fetching all activities that are needed to be evaluated for professor {}", email);
        User professor = userRepository.findUserByEmail(email);
        userValidator.validateProfessorAccount(professor, email);
        List<ActivityToEvaluateResponse> response = new LinkedList<>();
        List<FileTask> fileTasks = fileTaskRepository.findAll()
                .stream()
                .filter(fileTask -> fileTask.getProfessor().getEmail().equals(email))
                .toList();
        List<FileTaskResult> fileTaskResults = fileTaskResultRepo.findAll();
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
        List<FileTaskResult> fileTaskResults = fileTaskResultRepo.findAll()
                .stream()
                .filter(result -> Objects.equals(result.getFileTask().getId(), task.getId()))
                .filter(result -> !result.isEvaluated())
                .toList();
        if (fileTaskResults.size() == 0) return null;
        FileTaskResult result = fileTaskResults.get(0);
        long num = fileTaskResults.size();
        boolean isLate = false;
        Long sendDateMillis = result.getSendDateMillis();
        if(sendDateMillis != null){
            isLate = task.getRequirements()
                    .stream()
                    .map(Requirement::getDateToMillis)
                    .filter(Objects::nonNull)
                    .anyMatch(dateTo -> sendDateMillis > dateTo);
        }

        List<FileResponse> filesResponse = result.getFiles().stream().map(FileResponse::new).toList();

        return new TaskToEvaluateResponse(result.getUser().getEmail(), result.getId(), result.getUser().getFirstName(),
                result.getUser().getLastName(), task.getTitle(), isLate, task.getDescription(),
                result.getAnswer(), filesResponse, task.getMaxPoints(), id, num-1);
    }

    public List<ActivitiesResponse> getAllActivities() {
        log.info("Fetching all activities");
        List<Chapter> chapters = chapterRepository.findAll();
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

    public RequirementResponse getRequirementsForActivity(Long id) throws EntityNotFoundException, MissingAttributeException {
        Activity activity = getActivity(id);
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
        if(isBlocked != null) {
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
        } else {
            log.error("Activity with id {} not found in database", id);
            throw new EntityNotFoundException("Activity with id " + id + " not found in database");
        }
    }
}
