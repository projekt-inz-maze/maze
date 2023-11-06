package com.example.api.activity.task.dto.response;

import com.example.api.activity.ActivityType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ActivitiesResponse {
    private Long id;
    private String name;
    private String chapterName;
    private ActivityType type;
}
