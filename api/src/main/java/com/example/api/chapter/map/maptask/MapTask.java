package com.example.api.chapter.map.maptask;

import com.example.api.activity.ActivityType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class MapTask {
    private Long id;
    private Integer posX;
    private Integer posY;
    private ActivityType type;
    private String title;
    private Double points;
    private Long creationTime;
    private String description;
}
