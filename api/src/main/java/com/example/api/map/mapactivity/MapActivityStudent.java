package com.example.api.map.mapactivity;

import com.example.api.activity.Activity;
import com.example.api.chapter.requirement.RequirementResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MapActivityStudent extends MapActivity {
    private Boolean isFulfilled;
    private Boolean isCompleted;

    public MapActivityStudent(Activity activity, Boolean isFulfilled, Boolean isCompleted, RequirementResponse requirements) {
        super(activity.getId(),
                activity.getPosX(),
                activity.getPosY(),
                activity.getActivityType(),
                activity.getTitle(),
                activity.getMaxPoints(),
                activity.getCreationTime(),
                activity.getDescription(),
                requirements);
        this.isFulfilled = isFulfilled;
        this.isCompleted = isCompleted;
    }
}
