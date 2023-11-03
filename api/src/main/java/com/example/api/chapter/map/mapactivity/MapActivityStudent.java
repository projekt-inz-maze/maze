package com.example.api.chapter.map.mapactivity;

import com.example.api.activity.Activity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MapActivityStudent extends MapActivity {
    private Boolean isFulfilled;
    private Boolean isCompleted;

    public MapActivityStudent(Activity activity, Boolean isFulfilled, Boolean isCompleted) {
        super(activity.getId(),
                activity.getPosX(),
                activity.getPosY(),
                activity.getActivityType(),
                activity.getTitle(),
                activity.getMaxPoints(),
                activity.getCreationTime(),
                activity.getDescription());
        this.isFulfilled = isFulfilled;
        this.isCompleted = isCompleted;
    }
}
