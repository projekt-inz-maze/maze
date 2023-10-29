package com.example.api.map.dto.response.task;

import com.example.api.activity.Activity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MapTaskStudent extends MapTask{
    private Boolean isFulfilled;
    private Boolean isCompleted;

    public MapTaskStudent(Activity activity, Boolean isFulfilled, Boolean isCompleted) {
        super(activity.getId(), activity.getPosX(), activity.getPosY(), activity.getActivityType(), activity.getTitle(), activity.getMaxPoints(), activity.getCreationTime());
        this.isFulfilled = isFulfilled;
        this.isCompleted = isCompleted;
    }
}
