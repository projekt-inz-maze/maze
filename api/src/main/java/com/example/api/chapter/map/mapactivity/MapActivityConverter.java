package com.example.api.chapter.map.mapactivity;

import com.example.api.activity.Activity;
import com.example.api.activity.result.model.FileTaskResult;
import com.example.api.activity.result.model.GraphTaskResult;
import com.example.api.activity.result.model.SurveyResult;
import com.example.api.activity.result.repository.FileTaskResultRepository;
import com.example.api.activity.result.repository.GraphTaskResultRepository;
import com.example.api.activity.result.repository.SurveyResultRepository;
import com.example.api.activity.survey.Survey;
import com.example.api.activity.task.Task;
import com.example.api.activity.task.filetask.FileTask;
import com.example.api.activity.task.graphtask.GraphTask;
import com.example.api.chapter.requirement.RequirementService;
import com.example.api.user.model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MapActivityConverter {
    GraphTaskResultRepository graphTaskResultRepository;
    FileTaskResultRepository fileTaskResultRepository;
    SurveyResultRepository surveyResultRepository;
    RequirementService requirementService;

    public MapActivityStudent toMapTaskStudent(Activity activity, User student) {

        return switch (activity.getActivityType()) {
            case EXPEDITION -> new MapActivityStudent(
                    activity,
                    areRequirementsFulfilled(activity),
                    isGraphTaskCompleted((GraphTask) activity, student));
            case TASK -> ((Task) activity).getAuction()/*.map(Auction::isResolved).orElse(true)*/.isEmpty() ?
                    new MapActivityStudent(
                    activity,
                    areRequirementsFulfilled(activity),
                    isFileTaskCompleted((FileTask) activity, student))
                    : new MapActivityStudent(((Task) activity).getAuction().get(),
                    areRequirementsFulfilled(activity),
                    false);
            case INFO -> new MapActivityStudent(
                    activity,
                    areRequirementsFulfilled(activity),
                    true);
            case SURVEY -> new MapActivityStudent(
                    activity,
                    areRequirementsFulfilled(activity),
                    isSurveyCompleted((Survey) activity, student));
            case AUCTION -> new MapActivityStudent(activity, areRequirementsFulfilled(activity), false);
            default -> throw new IllegalStateException("Invalid activity type");
        };

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

    private Boolean areRequirementsFulfilled(Activity activity) {
        return requirementService.areRequirementsFulfilled(activity.getRequirements(), activity.getCourse());
    }
}
