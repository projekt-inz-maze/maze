package com.example.api.activity.result.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetCSVForm {
    List<Long> studentIds;
    List<Long> activityIds;
}
