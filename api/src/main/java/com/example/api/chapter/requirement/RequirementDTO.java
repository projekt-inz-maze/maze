package com.example.api.chapter.requirement;

import com.example.api.chapter.requirement.model.RequirementValueType;
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
