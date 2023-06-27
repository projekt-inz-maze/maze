package com.example.api.activity.task.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ActivityToEvaluateResponse {
    private Long activityId;
    private Long toGrade;
}
