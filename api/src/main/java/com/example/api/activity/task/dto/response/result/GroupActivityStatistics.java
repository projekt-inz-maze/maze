package com.example.api.activity.task.dto.response.result;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GroupActivityStatistics {
    private String groupName;
    private Double avgPoints;
    private Double avgPercentageResult;
}
