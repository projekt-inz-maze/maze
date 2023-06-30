package com.example.api.map.dto.request;

import com.example.api.activity.task.dto.request.requirement.RequirementForm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChapterRequirementForm {
    private Long chapterId;
    private Boolean isBlocked;
    private List<RequirementForm> requirements;
}
