package com.example.api.map.dto.response.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class MapTaskDTO {
    private Long id;
    private Integer posX;
    private Integer posY;
    private ActivityType type;
    private String title;
    private Double points;
    private Long creationTime;
    private String description;
}
