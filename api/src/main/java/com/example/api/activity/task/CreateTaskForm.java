package com.example.api.activity.task;

import com.example.api.activity.CreateActivityForm;
import com.example.api.activity.auction.CreateAuctionDTO;
import com.example.api.activity.task.filetask.CreateFileTaskForm;
import com.example.api.activity.task.graphtask.CreateGraphTaskForm;
import com.example.api.activity.ActivityType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonSubTypes({
        @JsonSubTypes.Type(value = CreateGraphTaskForm.class, name = "EXPEDITION"),
        @JsonSubTypes.Type(value = CreateFileTaskForm.class, name = "TASK")
})
public abstract class CreateTaskForm extends CreateActivityForm {
    @Schema CreateAuctionDTO auction;
    @Schema String requiredKnowledge;


    public CreateTaskForm(ActivityType activityType, String title, String description, Integer posX, Integer posY, String requiredKnowledge) {
        super(activityType, title, description, posX, posY);
        this.requiredKnowledge = requiredKnowledge;
    }

    public  CreateTaskForm(Task task) {
     super(task);
     this.requiredKnowledge = task.getRequiredKnowledge();
    }
}
