package com.example.api.chapter.map.maptask;

import com.example.api.activity.Activity;
import com.example.api.activity.result.model.FileTaskResult;
import com.example.api.activity.result.model.GraphTaskResult;
import com.example.api.activity.result.model.SurveyResult;
import com.example.api.activity.result.repository.FileTaskResultRepository;
import com.example.api.activity.result.repository.GraphTaskResultRepository;
import com.example.api.activity.result.repository.SurveyResultRepository;
import com.example.api.activity.survey.Survey;
import com.example.api.activity.task.filetask.FileTask;
import com.example.api.activity.task.graphtask.GraphTask;
import com.example.api.chapter.requirement.RequirementService;
import com.example.api.user.model.User;
import org.springframework.stereotype.Component;

@Component
public class MapTaskMapper {
    GraphTaskResultRepository graphTaskResultRepository;
    FileTaskResultRepository fileTaskResultRepository;
    SurveyResultRepository surveyResultRepository;
    RequirementService requirementService;

    public MapTaskStudent toMapTaskStudent(Activity activity, User student) {

        return switch (activity.getActivityType()) {
            case EXPEDITION -> new MapTaskStudent(
                    activity,
                    areRequirementsFulfilled(activity),
                    isGraphTaskCompleted((GraphTask) activity, student));
            case TASK -> new MapTaskStudent(
                    activity,
                    areRequirementsFulfilled(activity),
                    isFileTaskCompleted((FileTask) activity, student));
            case INFO -> new MapTaskStudent(
                    activity,
                    areRequirementsFulfilled(activity),
                    true);
            case SURVEY -> new MapTaskStudent(
                    activity,
                    areRequirementsFulfilled(activity),
                    isSurveyCompleted((Survey) activity, student));
            case AUCTION -> new MapTaskStudent(activity, areRequirementsFulfilled(activity), false);
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
