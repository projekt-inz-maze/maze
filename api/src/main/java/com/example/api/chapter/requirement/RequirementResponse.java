package com.example.api.chapter.requirement;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RequirementsDTO {
    private Boolean isBlocked;
    private List<? extends RequirementDTO<?>> requirements;
}
