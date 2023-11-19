package com.example.api.activity.task.filetask;

import com.example.api.activity.auction.CreateAuctionDTO;
import com.example.api.activity.task.CreateTaskForm;
import com.example.api.activity.ActivityType;
import com.example.api.util.message.MessageManager;
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
    @Schema(required = true) private Double maxPoints;

    public CreateFileTaskForm(String title,
                              String description,
                              Integer posX,
                              Integer posY,
                              String requiredKnowledge,
                              CreateAuctionDTO auction,
                              Double maxPoints) {
        super(ActivityType.TASK, title, description, posX, posY, requiredKnowledge);
        this.maxPoints = maxPoints;
        this.setAuction(auction);
    }

    public CreateFileTaskForm(FileTask fileTask) {
        super(fileTask);
        this.maxPoints = fileTask.getMaxPoints();
    }

    public static CreateFileTaskForm example() {
        return new CreateFileTaskForm(
                MessageManager.TITLE,
                MessageManager.DESC,
                3,
                4,
                MessageManager.REQ_KNOWLEDGE,
                new CreateAuctionDTO(0D, 100D, 0L, 10D),
                30.0
        );
    }
}
