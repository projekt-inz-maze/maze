package com.example.api.activity.task.dto.response.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityStatisticsResponse {
    private Double activity100;
    private Integer answersNumber;
    private Double avgPoints;
    private Double avgPercentageResult;
    private Double bestScore;
    private Double worstScore;
    private List<GroupActivityStatistics> avgScores;
    private List<ScaleActivityStatistics> scaleScores;
}
