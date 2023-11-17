package com.example.api.activity.task.dto.response.result;

import com.example.api.activity.ActivityType;
import com.example.api.activity.result.model.FileTaskResult;
import com.example.api.activity.result.model.GraphTaskResult;
import com.example.api.activity.result.model.SurveyResult;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskPointsStatisticsResponse extends PointsResponse {

    public TaskPointsStatisticsResponse(GraphTaskResult result) {
        super();
        setDateAndPoints(result.getSendDateMillis(), result.getPointsReceived());
        this.activityType = ActivityType.EXPEDITION;
        this.activityName = result.getGraphTask().getTitle();
    }

    public TaskPointsStatisticsResponse(FileTaskResult result) {
        super();
        setDateAndPoints(result.getSendDateMillis(), result.getPointsReceived());
        this.activityType = ActivityType.TASK;
        this.activityName = result.getFileTask().getTitle();
    }

    public TaskPointsStatisticsResponse(SurveyResult result) {
        super();
        setDateAndPoints(result.getSendDateMillis(), result.getPointsReceived());
        this.activityType = ActivityType.SURVEY;
        this.activityName = result.getSurvey().getTitle();
    }

    private void setDateAndPoints(Long dateMillis, Double pointsReceived) {
        this.dateMillis = dateMillis;
        this.pointsReceived = pointsReceived;
    }
}
