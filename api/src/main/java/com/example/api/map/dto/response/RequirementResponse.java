package com.example.api.map.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RequirementResponse {
    private Boolean isBlocked;
    private List<? extends RequirementDTO<?>> requirements;
}
