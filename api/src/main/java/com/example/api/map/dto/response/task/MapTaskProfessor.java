package com.example.api.map.dto.response.task;

import com.example.api.activity.task.model.Activity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MapTaskProfessor extends MapTask{
    private Boolean isActivityBlocked;

    public MapTaskProfessor(Activity activity, Boolean isActivityBlocked) {
        super(activity.getId(), activity.getPosX(), activity.getPosY(), activity.getActivityType(), activity.getTitle(), activity.getMaxPoints(), activity.getCreationTime());
        this.isActivityBlocked = isActivityBlocked;
    }
}
