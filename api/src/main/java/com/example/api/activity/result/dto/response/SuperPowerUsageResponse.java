package com.example.api.activity.result.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

@Data
@AllArgsConstructor
@Setter
public class SuperPowerUsageResponse {
    private Boolean canBeUsed;
    private String message;
}
