package com.example.api.map;

import com.example.api.activity.Activity;
import com.example.api.activity.task.TaskService;
import com.example.api.map.mapactivity.MapActivityConverter;
import com.example.api.map.mapactivity.MapActivity;
import com.example.api.map.mapactivity.MapActivityProfessor;
import com.example.api.map.mapactivity.MapActivityStudent;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.user.model.AccountType;
import com.example.api.user.model.User;
import com.example.api.security.LoggedInUserService;
import com.example.api.validator.MapValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ActivityMapService {
    private final MapRepository mapRepository;
    private final MapValidator mapValidator;
    private final LoggedInUserService authService;
    private final MapActivityConverter mapActivityConverter;
    private final TaskService taskService;

    public ActivityMap saveActivityMap(ActivityMap activityMap){
        return mapRepository.save(activityMap);
    }

    public ActivityMapResponse getActivityMap(Long id) throws EntityNotFoundException, WrongUserTypeException {
        log.info("Fetching activity map with id {} as ActivityMapResponse", id);
        User user = authService.getCurrentUser();

        ActivityMap activityMap = mapRepository.findActivityMapById(id);
        mapValidator.validateActivityMapIsNotNull(activityMap, id);
        List<? extends MapActivity> allTasks;
        if (user.getAccountType() == AccountType.STUDENT) {
            allTasks = getMapTasksForStudent(activityMap, user);
        } else {
            allTasks = getMapTasksForProfessor(activityMap);
        }
        return new ActivityMapResponse(activityMap.getId(), allTasks, activityMap.getMapSizeX(), activityMap.getMapSizeY(), activityMap.getImage());
    }

    public List<MapActivityProfessor> getMapTasksForProfessor(ActivityMap activityMap) {
        Stream<Activity> tasksWithAuctions =  Stream.of(activityMap.getGraphTasks(), activityMap.getFileTasks())
                .flatMap(Collection::stream)
                .map(task -> task.getAuction().map(auction -> auction.isResolved() ? auction.getTask() : auction).orElse(task));

        return Stream.of(tasksWithAuctions,
                        activityMap.getInfos().stream(),
                        activityMap.getSurveys().stream())
                .flatMap(s -> s)
                .map(activity -> new MapActivityProfessor(activity, activity.getIsBlocked(), taskService.getRequirementsForActivity(activity)))
                .sorted(Comparator.comparingLong(MapActivity::getId))
                .toList();
    }

    public List<MapActivityStudent> getMapTasksForStudent(ActivityMap activityMap, User student) {
        Stream<Activity> tasksWithAuctions =  Stream.of(activityMap.getGraphTasks(), activityMap.getFileTasks())
                .flatMap(Collection::stream)
                .flatMap(task -> Stream.concat(Stream.of(task), task.getAuction().stream()));

        return Stream.of(tasksWithAuctions,
                        activityMap.getInfos().stream(),
                        activityMap.getSurveys().stream())
                .flatMap(s -> s)
                .filter(activity -> !activity.getIsBlocked())
                .map(activity -> mapActivityConverter.toMapTaskStudent(activity, student))
                .sorted(Comparator.comparingLong(MapActivity::getId))
                .toList();
    }
}
