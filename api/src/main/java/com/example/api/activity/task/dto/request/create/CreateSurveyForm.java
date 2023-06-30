package com.example.api.activity.task.dto.request.create;

import com.example.api.map.dto.response.task.ActivityType;
import com.example.api.activity.task.model.Survey;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateSurveyForm extends CreateActivityForm{
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
