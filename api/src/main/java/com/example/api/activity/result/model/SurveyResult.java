package com.example.api.activity.result.model;

import com.example.api.activity.Activity;
import com.example.api.activity.survey.Survey;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class SurveyResult extends ActivityResult {

    @Min(1)
    @Max(5)
    private Integer rate;

    @Lob
    private String feedback;

    public SurveyResult(Activity survey, @Min(1) @Max(5) Integer rate, String feedback) {
        this.activity = survey;
        this.rate = rate;
        this.feedback = feedback;
    }

    @Override
    public boolean isEvaluated() {
        return this.getSendDateMillis() != null && this.getPoints() != null;
    }

    public Survey getSurvey() {
        return (Survey) activity;
    }

    public void setSurvey(Survey survey) {
        this.activity = survey;
    }
}
