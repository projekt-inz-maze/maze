package com.example.api.activity.task.dto.request.edit;

import com.example.api.activity.task.dto.request.create.CreateFileTaskForm;
import com.example.api.map.dto.response.task.ActivityType;
import com.example.api.activity.task.model.FileTask;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EditFileTaskForm extends EditActivityForm {
    public EditFileTaskForm(FileTask fileTask) {
        super(fileTask.getId(), ActivityType.TASK, new CreateFileTaskForm(fileTask));
    }
}
