package com.example.api.map.mapactivity;

import com.example.api.activity.ActivityType;
import com.example.api.chapter.requirement.RequirementResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public abstract class MapActivity {
    private Long id;
    private Integer posX;
    private Integer posY;
    private ActivityType type;
    private String title;
    private Double points;
    private Long creationTime;
    private String description;
    private RequirementResponse requirements;
}
