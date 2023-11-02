package com.example.api.activity.info;

import com.example.api.activity.EditActivityForm;
import com.example.api.activity.ActivityType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EditInfoForm extends EditActivityForm {
    public EditInfoForm(Info info) {
        super(info.getId(), ActivityType.INFO, new CreateInfoForm(info));
    }
}
