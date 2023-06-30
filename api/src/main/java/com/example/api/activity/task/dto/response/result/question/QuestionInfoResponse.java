package com.example.api.activity.task.dto.response.result.question;

import com.example.api.activity.result.model.ResultStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionInfoResponse {
    private ResultStatus status;
    private Long timeRemaining;
    private Double actualPointsReceived;
    private List<QuestionList> questions;
    private QuestionDetails questionDetails;
    private boolean finished;
    private List<Long> currentPath;
}
