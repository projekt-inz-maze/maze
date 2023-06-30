package com.example.api.activity.task.dto.request.edit;

import com.example.api.activity.task.dto.request.create.CreateInfoForm;
import com.example.api.map.dto.response.task.ActivityType;
import com.example.api.activity.task.model.Info;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EditInfoForm extends EditActivityForm {
    public EditInfoForm(Info info) {
        super(info.getId(), ActivityType.INFO, new CreateInfoForm(info));
    }
}
