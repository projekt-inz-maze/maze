package com.example.api.activity.task.dto.response.result;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TotalPointsResponse {
    Double totalPointsReceived;
    Double totalPointsPossibleToReceive;
}
