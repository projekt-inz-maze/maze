package com.example.api.activity.task.dto.response.result;

import com.example.api.activity.ActivityType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class PointsResponse {
    protected Long dateMillis;
    protected Double pointsReceived;
    protected ActivityType activityType;
    protected String activityName;

    public Long getDateMillis() {
        return this.dateMillis;
    }
}
