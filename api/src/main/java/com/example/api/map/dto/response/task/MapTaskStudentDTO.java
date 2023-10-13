package com.example.api.map.dto.response.task;

import com.example.api.activity.task.model.Activity;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
public class MapTaskStudentDTO extends MapTaskDTO {
    private Boolean isFulfilled;
    private Boolean isCompleted;
    private Optional<Integer> wager;

    public MapTaskStudentDTO(Activity activity, Boolean isFulfilled, Boolean isCompleted) {
        super(activity.getId(), activity.getPosX(), activity.getPosY(), activity.getActivityType(), activity.getTitle(), activity.getMaxPoints(), activity.getCreationTime());
        this.isFulfilled = isFulfilled;
        this.isCompleted = isCompleted;

    }
}
