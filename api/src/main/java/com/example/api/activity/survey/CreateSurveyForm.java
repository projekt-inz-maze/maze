package com.example.api.activity.survey;

import com.example.api.activity.CreateActivityForm;
import com.example.api.activity.ActivityType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateSurveyForm extends CreateActivityForm {
    @Schema(required = true) private Double points;

    public CreateSurveyForm(String title, String description, Integer posX, Integer posY, Double points){
        super(ActivityType.SURVEY, title, description, posX, posY);
        this.points = points;
    }

    public CreateSurveyForm(Survey survey) {
        super(survey);
        this.points = survey.getPoints();
    }
}
