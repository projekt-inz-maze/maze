package com.example.api.activity.result.service.util;

import com.example.api.activity.task.dto.response.result.GroupActivityStatistics;
import com.example.api.activity.ActivityType;
import com.example.api.activity.result.model.SurveyResult;
import com.example.api.activity.result.model.ActivityResult;
import com.example.api.activity.Activity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GroupActivityStatisticsCreator {
    private String groupName;
    private Integer answersNumber = 0;
    private Double sumPoints;
    private Double maxPointsForTask;

    public GroupActivityStatisticsCreator(Activity activity, ActivityResult activityResult) {
        if (activity.getActivityType().equals(ActivityType.SURVEY)) {
            initSurvey((SurveyResult) activityResult);
        }
        else initTask(activity, activityResult);
    }

    public void initTask(Activity task, ActivityResult activityResult) {
        this.groupName = activityResult.getMember().getGroup().getName();
        this.answersNumber = 1;
        this.sumPoints = activityResult.getPoints();
        this.maxPointsForTask = task.getMaxPoints();
    }

    public void initSurvey(SurveyResult surveyResult) {
        this.groupName = surveyResult.getMember().getGroup().getName();
        this.answersNumber = 1;
        this.sumPoints = surveyResult.getPoints();
    }

    public void add(ActivityResult activityResult) {
        if (activityResult.getActivity().getActivityType().equals(ActivityType.SURVEY)) {
            addSurvey((SurveyResult) activityResult);
        }
        else addTask(activityResult);
    }

    public void addTask(ActivityResult activityResult) {
        this.answersNumber += 1;
        this.sumPoints += activityResult.getPoints();
    }

    public void addSurvey(SurveyResult surveyResult) {
        this.answersNumber += 1;
        this.sumPoints += surveyResult.getPoints();
    }

    public GroupActivityStatistics create() {
        GroupActivityStatistics result = new GroupActivityStatistics();
        result.setGroupName(groupName);
        if (answersNumber > 0) {
            result.setAvgPoints(sumPoints / answersNumber);
            if (maxPointsForTask != null && maxPointsForTask > 0) {
                result.setAvgPercentageResult(100 * sumPoints / (maxPointsForTask * answersNumber));
            }
        }
        return result;
    }
}
