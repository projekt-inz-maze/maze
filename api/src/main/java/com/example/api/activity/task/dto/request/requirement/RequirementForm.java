package com.example.api.activity.task.dto.request.requirement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class  RequirementForm {
    private Long id;
    private Boolean selected;
    private String value;
}
