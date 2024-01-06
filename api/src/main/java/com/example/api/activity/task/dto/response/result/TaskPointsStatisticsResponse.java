package com.example.api.activity.task.dto.response.result;

import com.example.api.activity.ActivityType;
import com.example.api.activity.result.model.ActivityResult;
import com.example.api.activity.result.model.FileTaskResult;
import com.example.api.activity.result.model.GraphTaskResult;
import com.example.api.activity.result.model.SurveyResult;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskPointsStatisticsResponse extends PointsResponse {


    public TaskPointsStatisticsResponse(ActivityResult result) {
        super();
        setDateAndPoints(result.getSendDateMillis(), result.getPoints());
        this.activityType = result.getActivity().getActivityType();
        this.activityName = result.getActivity().getTitle();
    }

    private void setDateAndPoints(Long dateMillis, Double pointsReceived) {
        this.dateMillis = dateMillis;
        this.pointsReceived = pointsReceived;
    }
}
