package com.example.api.activity.task.dto.response;

import com.example.api.activity.ActivityType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ActivityToEvaluateResponse {
    private Long activityId;
    private ActivityType activityType;
    private Long toGrade;
}
