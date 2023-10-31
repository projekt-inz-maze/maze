package com.example.api.activity.task.filetask;

import com.example.api.activity.task.CreateTaskForm;
import com.example.api.map.dto.response.task.ActivityType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateFileTaskForm extends CreateTaskForm {
    @Schema(required = true) private String requiredKnowledge;
    @Schema(required = true) private Double maxPoints;

    public CreateFileTaskForm(String title,
                              String description,
                              Integer posX,
                              Integer posY,
                              String requiredKnowledge,
                              Double maxPoints) {
        super(ActivityType.TASK, title, description, posX, posY);
        this.requiredKnowledge = requiredKnowledge;
        this.maxPoints = maxPoints;
    }

    public CreateFileTaskForm(FileTask fileTask) {
        super(fileTask);
        this.requiredKnowledge = fileTask.getRequiredKnowledge();
        this.maxPoints = fileTask.getMaxPoints();
    }
}
