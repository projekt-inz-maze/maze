package com.example.api.chapter.map.maptask;

import com.example.api.activity.Activity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MapTaskProfessor extends MapTask{
    private Boolean isActivityBlocked;

    public MapTaskProfessor(Activity activity, Boolean isActivityBlocked) {
        super(activity.getId(), activity.getPosX(), activity.getPosY(), activity.getActivityType(), activity.getTitle(), activity.getMaxPoints(), activity.getCreationTime(), activity.getDescription());
        this.isActivityBlocked = isActivityBlocked;
    }
}
