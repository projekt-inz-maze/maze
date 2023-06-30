package com.example.api.activity.task.dto.request.edit;

import com.example.api.activity.task.dto.request.create.CreateSurveyForm;
import com.example.api.map.dto.response.task.ActivityType;
import com.example.api.activity.task.model.Survey;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EditSurveyForm extends EditActivityForm {
    public EditSurveyForm(Survey survey) {
        super(survey.getId(), ActivityType.SURVEY, new CreateSurveyForm(survey));
    }
}
