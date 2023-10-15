package com.example.api.map.dto.response.task;

import com.example.api.activity.task.model.Activity;
import com.example.api.map.dto.response.RequirementsDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
public class MapTaskStudentDTO extends MapTaskDTO {
    private Boolean isFulfilled;
    private Boolean isCompleted;
    private Optional<Integer> wager;
    private RequirementsDTO requirements;
    private Long timeLimit;

    public MapTaskStudentDTO(Activity activity, Boolean isFulfilled, Boolean isCompleted, RequirementsDTO requirements) {
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
        this.requirements = requirements;
    }

    public MapTaskStudentDTO(Activity activity, Boolean isFulfilled, Boolean isCompleted, RequirementsDTO requirements, Long timeLimit) {
        this(activity, isFulfilled, isCompleted, requirements);
        this.timeLimit = timeLimit;
    }
}
