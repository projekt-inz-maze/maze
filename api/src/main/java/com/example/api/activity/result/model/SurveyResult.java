package com.example.api.activity.result.model;

import com.example.api.activity.result.dto.response.SurveyAnswerResponse;
import com.example.api.activity.task.model.Activity;
import com.example.api.activity.task.model.Survey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SurveyResult extends TaskResult{
    @ManyToOne
    @JoinColumn(name="survey_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Survey survey;

    @Min(1)
    @Max(5)
    private Integer rate;

    @Lob
    private String feedback;

    @Override
    public boolean isEvaluated() {
        return this.getSendDateMillis() != null && this.getPointsReceived() != null;
    }

    @Override
    public Activity getActivity() {
        return survey;
    }

    public SurveyAnswerResponse getSurveyAnswerResponse() {
        return new SurveyAnswerResponse(feedback, rate);
    }
}
