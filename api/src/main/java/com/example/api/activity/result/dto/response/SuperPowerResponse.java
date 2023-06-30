package com.example.api.activity.result.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

@Data
@AllArgsConstructor
@Setter
public class SuperPowerResponse<T> {
    private T value;
}
