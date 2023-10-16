package com.example.api.activity.task.dto.request.requirement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityRequirementForm {
    private Long activityId;
    private Boolean isBlocked;
    private List<RequirementForm> requirements;
}
