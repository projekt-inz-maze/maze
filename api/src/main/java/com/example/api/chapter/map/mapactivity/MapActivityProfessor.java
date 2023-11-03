package com.example.api.chapter.map.mapactivity;

import com.example.api.activity.Activity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MapActivityProfessor extends MapActivity {
    private Boolean isActivityBlocked;

    public MapActivityProfessor(Activity activity, Boolean isActivityBlocked) {
        super(activity.getId(), activity.getPosX(), activity.getPosY(), activity.getActivityType(), activity.getTitle(), activity.getMaxPoints(), activity.getCreationTime(), activity.getDescription());
        this.isActivityBlocked = isActivityBlocked;
    }
}
