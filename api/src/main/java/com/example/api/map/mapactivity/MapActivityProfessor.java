package com.example.api.map.mapactivity;

import com.example.api.activity.Activity;
import com.example.api.chapter.requirement.RequirementResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MapActivityProfessor extends MapActivity {
    private Boolean isActivityBlocked;

    public MapActivityProfessor(Activity activity, Boolean isActivityBlocked, RequirementResponse requirements) {
        super(activity.getId(), activity.getPosX(), activity.getPosY(), activity.getActivityType(), activity.getTitle(), activity.getMaxPoints(), activity.getCreationTime(), activity.getDescription(), requirements);
        this.isActivityBlocked = isActivityBlocked;
    }
}
