package com.example.api.activity.task.filetask;

import com.example.api.activity.EditActivityForm;
import com.example.api.map.dto.response.task.ActivityType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EditFileTaskForm extends EditActivityForm {
    public EditFileTaskForm(FileTask fileTask) {
        super(fileTask.getId(), ActivityType.TASK, new CreateFileTaskForm(fileTask));
    }
}
