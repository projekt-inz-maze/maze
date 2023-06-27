package com.example.api.map.dto.response;

import com.example.api.map.model.requirement.RequirementValueType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequirementDTO<T> {
    private Long id;
    private String name;
    private T value;
    private RequirementValueType type;
    private Boolean selected;
}
