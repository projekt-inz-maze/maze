package com.example.api.map.mapactivity;

import com.example.api.activity.Activity;
import com.example.api.activity.result.model.FileTaskResult;
import com.example.api.activity.result.model.GraphTaskResult;
import com.example.api.activity.result.model.SurveyResult;
import com.example.api.activity.result.repository.ActivityResultRepository;
import com.example.api.activity.result.repository.FileTaskResultRepository;
import com.example.api.activity.result.repository.GraphTaskResultRepository;
import com.example.api.activity.result.repository.SurveyResultRepository;
import com.example.api.activity.survey.Survey;
import com.example.api.activity.task.filetask.FileTask;
import com.example.api.activity.task.graphtask.GraphTask;
import com.example.api.chapter.requirement.RequirementDTO;
import com.example.api.chapter.requirement.RequirementResponse;
import com.example.api.chapter.requirement.RequirementService;
import com.example.api.chapter.requirement.model.Requirement;
import com.example.api.user.model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
@AllArgsConstructor
public class MapActivityConverter {
    private final GraphTaskResultRepository graphTaskResultRepository;
    private final RequirementService requirementService;
    private final ActivityResultRepository activityResultRepository;

    public MapActivityStudent toMapTaskStudent(Activity activity, User student) {
        return switch (activity.getActivityType()) {
            case EXPEDITION -> new MapActivityStudent(
                    activity,
                    areRequirementsFulfilled(activity),
                    isGraphTaskCompleted((GraphTask) activity, student),
                    getRequirements(activity));
            case TASK, SURVEY, SUBMIT -> new MapActivityStudent(
                    activity,
                    areRequirementsFulfilled(activity),
                    isActivityCompleted(activity.getId(), student),
                    getRequirements(activity));
            case INFO -> new MapActivityStudent(
                    activity,
                    areRequirementsFulfilled(activity),
                    true,
                    getRequirements(activity));
            case AUCTION -> new MapActivityStudent(
                    activity,
                    areRequirementsFulfilled(activity),
                    false,
                    getRequirements(activity));
            default -> throw new IllegalStateException("Invalid activity type");
        };

    }

    private Boolean isActivityCompleted(Long activityId, User user) {
        return activityResultRepository.existsByActivity_IdAndMember_User(activityId, user);
    }

    private Boolean isGraphTaskCompleted(GraphTask graphTask, User user) {
        GraphTaskResult result = graphTaskResultRepository.findGraphTaskResultByGraphTaskAndUser(graphTask, user);
        return result != null && result.getSendDateMillis() != null;
    }

    private Boolean areRequirementsFulfilled(Activity activity) {
        return requirementService.areRequirementsFulfilled(activity.getRequirements(), activity.getCourse());
    }

    private RequirementResponse getRequirements(Activity activity) {
        List<? extends RequirementDTO<?>> requirements = activity.getRequirements()
                .stream()
                .map(Requirement::getResponse)
                .sorted(Comparator.comparingLong(RequirementDTO::getId))
                .toList();
        return new RequirementResponse(activity.getIsBlocked(), requirements);
    }
}
