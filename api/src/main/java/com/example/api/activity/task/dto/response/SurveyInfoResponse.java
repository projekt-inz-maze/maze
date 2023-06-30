package com.example.api.activity.task.dto.response;

import com.example.api.activity.result.dto.response.SurveyResultInfoResponse;
import com.example.api.activity.task.model.Survey;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class SurveyInfoResponse {
    private String name;
    private String description;
    private Double experience;
    private SurveyResultInfoResponse feedback;

    public SurveyInfoResponse(Survey survey) {
        this.name = survey.getTitle();
        this.description = survey.getDescription();
        this.experience = survey.getMaxPoints();
    }
}
