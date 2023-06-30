package com.example.api.activity.task.dto.response.result;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdditionalPointsResponse {
    private Long dateMillis;
    private String professor;
    private Double points;
    private String description;
}
