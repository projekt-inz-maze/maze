package com.example.api.activity.result.dto.response;

import com.example.api.activity.result.model.SurveyResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Lob;

@Data
@AllArgsConstructor
public class SurveyAnswerResponse {
    @Schema(required = true) @Lob private String answer;
    @Schema(required = true) private Integer studentPoints;

    public SurveyAnswerResponse(SurveyResult result) {
        this(result.getFeedback(), result.getRate());
    }
}
