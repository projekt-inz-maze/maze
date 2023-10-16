package com.example.api.activity.result.dto.response;

import com.example.api.activity.result.model.SurveyResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@AllArgsConstructor
@Setter
public class SurveyResultInfoResponse {
    private Long surveyResultId;
    private Long surveyId;
    private String from;
    private String feedback;
    private Integer rate;

    public SurveyResultInfoResponse(SurveyResult surveyResult) {
        this.surveyResultId = surveyResult.getId();
        this.surveyId = surveyResult.getSurvey().getId();
        this.from = surveyResult.getMember().getUser().getEmail();
        this.feedback = surveyResult.getFeedback();
        this.rate = surveyResult.getRate();
    }
}
