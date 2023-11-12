package com.example.api.activity.result.service.util;

import com.example.api.activity.task.dto.response.result.GroupActivityStatistics;
import com.example.api.activity.ActivityType;
import com.example.api.activity.result.model.SurveyResult;
import com.example.api.activity.result.model.TaskResult;
import com.example.api.activity.Activity;
import com.example.api.activity.survey.Survey;
import com.example.api.activity.task.Task;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GroupActivityStatisticsCreator {
    private String groupName;
    private Integer answersNumber = 0;
    private Double sumPoints;
    private Double maxPointsForTask;

    public GroupActivityStatisticsCreator(Activity activity, TaskResult taskResult) {
        if (activity.getActivityType().equals(ActivityType.SURVEY)) {
            initSurvey((Survey) activity, (SurveyResult) taskResult);
        }
        else initTask((Task) activity, taskResult);
    }

    public void initTask(Task task, TaskResult taskResult) {
        this.groupName = taskResult.getMember().getGroup().getName();
        this.answersNumber = 1;
        this.sumPoints = taskResult.getPoints();
        this.maxPointsForTask = task.getMaxPoints();
    }

    public void initSurvey(Survey survey, SurveyResult surveyResult) {
        this.groupName = surveyResult.getMember().getGroup().getName();
        this.answersNumber = 1;
        this.sumPoints = surveyResult.getPoints();
    }

    public void add(TaskResult taskResult) {
        if (taskResult.getActivity().getActivityType().equals(ActivityType.SURVEY)) {
            addSurvey((SurveyResult) taskResult);
        }
        else addTask(taskResult);
    }

    public void addTask(TaskResult taskResult) {
        this.answersNumber += 1;
        this.sumPoints += taskResult.getPoints();
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
