package com.example.api.map.dto.response.task;

import com.example.api.activity.task.model.Activity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MapTaskProfessorDTO extends MapTaskDTO {
    private Boolean isActivityBlocked;

    public MapTaskProfessorDTO(Activity activity, Boolean isActivityBlocked) {
        super(activity.getId(),
                activity.getPosX(),
                activity.getPosY(),
                activity.getActivityType(),
                activity.getTitle(),
                activity.getMaxPoints(),
                activity.getCreationTime(),
                activity.getDescription());
        this.isActivityBlocked = isActivityBlocked;
    }
}
