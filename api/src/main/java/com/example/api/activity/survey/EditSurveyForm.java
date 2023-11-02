package com.example.api.activity.survey;

import com.example.api.activity.EditActivityForm;
import com.example.api.activity.ActivityType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EditSurveyForm extends EditActivityForm {
    public EditSurveyForm(Survey survey) {
        super(survey.getId(), ActivityType.SURVEY, new CreateSurveyForm(survey));
    }
}
