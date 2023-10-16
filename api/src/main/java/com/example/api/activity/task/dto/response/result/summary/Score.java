package com.example.api.activity.task.dto.response.result.summary;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Score {
    private String groupName;
    private Double score;
}
