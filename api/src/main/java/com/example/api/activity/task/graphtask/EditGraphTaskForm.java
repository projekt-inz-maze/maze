package com.example.api.activity.task.graphtask;

import com.example.api.activity.EditActivityForm;
import com.example.api.map.dto.response.task.ActivityType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EditGraphTaskForm extends EditActivityForm {
    public EditGraphTaskForm(GraphTask graphTask) {
        super(graphTask.getId(), ActivityType.EXPEDITION, new CreateGraphTaskForm(graphTask));
    }
}
