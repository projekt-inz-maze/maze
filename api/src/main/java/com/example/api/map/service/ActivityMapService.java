package com.example.api.map.service;

import com.example.api.activity.task.service.TaskService;
import com.example.api.map.dto.response.ActivityMapResponse;
import com.example.api.map.dto.response.task.MapTaskDTO;
import com.example.api.map.dto.response.task.MapTaskProfessorDTO;
import com.example.api.map.dto.response.task.MapTaskStudentDTO;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.activity.result.model.FileTaskResult;
import com.example.api.activity.result.model.GraphTaskResult;
import com.example.api.activity.result.model.SurveyResult;
import com.example.api.activity.task.model.Activity;
import com.example.api.activity.task.model.FileTask;
import com.example.api.activity.task.model.GraphTask;
import com.example.api.activity.task.model.Survey;
import com.example.api.map.model.ActivityMap;
import com.example.api.user.model.AccountType;
import com.example.api.user.model.User;
import com.example.api.activity.result.repository.FileTaskResultRepository;
import com.example.api.activity.result.repository.GraphTaskResultRepository;
import com.example.api.activity.result.repository.SurveyResultRepository;
import com.example.api.map.repository.MapRepository;
import com.example.api.security.LoggedInUserService;
import com.example.api.validator.MapValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ActivityMapService {
    private final MapRepository mapRepository;
    private final RequirementService requirementService;
    private final MapValidator mapValidator;
    private final LoggedInUserService authService;
    private final GraphTaskResultRepository graphTaskResultRepository;
    private final FileTaskResultRepository fileTaskResultRepository;
    private final SurveyResultRepository surveyResultRepository;
    private final TaskService taskService;

    public ActivityMap saveActivityMap(ActivityMap activityMap){
        return mapRepository.save(activityMap);
    }

    public ActivityMapResponse getActivityMap(Long id) throws EntityNotFoundException, WrongUserTypeException {
        log.info("Fetching activity map with id {} as ActivityMapResponse", id);
        User user = authService.getCurrentUser();

        ActivityMap activityMap = mapRepository.findActivityMapById(id);
        mapValidator.validateActivityMapIsNotNull(activityMap, id);
        List<? extends MapTaskDTO> allTasks;
        if (user.getAccountType() == AccountType.STUDENT) {
            allTasks = getMapTasksForStudent(activityMap, user);
        } else {
            allTasks = getMapTasksForProfessor(activityMap, user);
        }
        return new ActivityMapResponse(activityMap.getId(), allTasks, activityMap.getMapSizeX(), activityMap.getMapSizeY(), activityMap.getImage());
    }

    public List<MapTaskProfessorDTO> getMapTasksForProfessor(ActivityMap activityMap, User professor) {
        return Stream.of(activityMap.getGraphTasks(), activityMap.getFileTasks(), activityMap.getSurveys(), activityMap.getInfos())
                .flatMap(List::stream)
                .map(activity -> new MapTaskProfessorDTO(activity, activity.getIsBlocked()))
                .sorted(Comparator.comparingLong(MapTaskDTO::getId))
                .toList();
    }

    public List<MapTaskStudentDTO> getMapTasksForStudent(ActivityMap activityMap, User student) {
        List<MapTaskStudentDTO> graphTasks = activityMap.getGraphTasks()
                .stream()
                .filter(graphTask -> !graphTask.getIsBlocked())
                .map(graphTask -> new MapTaskStudentDTO(
                        graphTask,
                        areRequirementsFulfilled(graphTask),
                        isGraphTaskCompleted(graphTask, student),
                        taskService.getRequirementsForActivity(graphTask),
                        graphTask.getTimeToSolveMillis()))
                .toList();
        List<MapTaskStudentDTO> fileTasks = activityMap.getFileTasks()
                .stream()
                .filter(fileTask -> !fileTask.getIsBlocked())
                .map(fileTask -> new MapTaskStudentDTO(
                        fileTask,
                        areRequirementsFulfilled(fileTask),
                        isFileTaskCompleted(fileTask, student),
                        taskService.getRequirementsForActivity(fileTask)))
                .toList();
        List<MapTaskStudentDTO> infos = activityMap.getInfos()
                .stream()
                .filter(info -> !info.getIsBlocked())
                .map(info -> new MapTaskStudentDTO(
                        info,
                        areRequirementsFulfilled(info),
                        true,
                        taskService.getRequirementsForActivity(info)))
                .toList();
        List<MapTaskStudentDTO> surveys = activityMap.getSurveys()
                .stream()
                .filter(survey -> !survey.getIsBlocked())
                .map(survey -> new MapTaskStudentDTO(
                        survey,
                        areRequirementsFulfilled(survey),
                        isSurveyCompleted(survey, student),
                        taskService.getRequirementsForActivity(survey)))
                .toList();
        return Stream.of(graphTasks, fileTasks, infos, surveys)
                .flatMap(List::stream)
                .sorted(Comparator.comparingLong(MapTaskDTO::getId))
                .toList();
    }

    private Boolean areRequirementsFulfilled(Activity activity) {
        return requirementService.areRequirementsFulfilled(activity.getRequirements(), activity.getCourse());
    }

    private Boolean isGraphTaskCompleted(GraphTask graphTask, User user) {
        GraphTaskResult result = graphTaskResultRepository.findGraphTaskResultByGraphTaskAndUser(graphTask, user);
        return result != null && result.getSendDateMillis() != null;
    }

    private Boolean isFileTaskCompleted(FileTask fileTask, User user) {
        FileTaskResult result = fileTaskResultRepository.findFileTaskResultByFileTaskAndUser(fileTask, user);
        return result != null;
    }

    private Boolean isSurveyCompleted(Survey survey, User student) {
        SurveyResult result = surveyResultRepository.findSurveyResultBySurveyAndUser(survey, student);
        return result != null;
    }
}
