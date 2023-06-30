package com.example.api.activity.task.dto.request.edit;

import com.example.api.activity.task.dto.request.create.CreateGraphTaskForm;
import com.example.api.map.dto.response.task.ActivityType;
import com.example.api.activity.task.model.GraphTask;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EditGraphTaskForm extends EditActivityForm {
    public EditGraphTaskForm(GraphTask graphTask) {
        super(graphTask.getId(), ActivityType.EXPEDITION, new CreateGraphTaskForm(graphTask));
    }
}
