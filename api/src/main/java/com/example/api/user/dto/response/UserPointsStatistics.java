package com.example.api.user.dto.response;

import com.example.api.activity.ActivityType;
import com.example.api.activity.result.model.FileTaskResult;
import com.example.api.activity.result.model.GraphTaskResult;
import com.example.api.activity.result.model.SurveyResult;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserPointsStatistics {
    private Long dateInMillis;
    private Double pointsReceived;
    private ActivityType activityType;
    private String activityName;

    public UserPointsStatistics(GraphTaskResult result) {
        this.dateInMillis = result.getSendDateMillis();
        this.pointsReceived = result.getPoints();
        this.activityType = ActivityType.EXPEDITION;
        this.activityName = result.getGraphTask().getTitle();
    }

    public UserPointsStatistics(FileTaskResult result) {
        this.dateInMillis = result.getSendDateMillis();
        this.pointsReceived = result.getPoints();
        this.activityType = ActivityType.TASK;
        this.activityName = result.getFileTask().getTitle();
    }

    public UserPointsStatistics(SurveyResult result) {
        this.dateInMillis = result.getSendDateMillis();
        this.pointsReceived = result.getPoints();
        this.activityType = ActivityType.SURVEY;
        this.activityName = result.getSurvey().getTitle();
    }
}
