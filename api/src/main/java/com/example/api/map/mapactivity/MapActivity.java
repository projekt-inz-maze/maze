package com.example.api.map.mapactivity;

import com.example.api.activity.ActivityType;
import com.example.api.chapter.requirement.RequirementResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
<<<<<<<< HEAD:api/src/main/java/com/example/api/chapter/requirement/task/MapTaskDTO.java
@NoArgsConstructor
public abstract class MapTaskDTO {
========
public abstract class MapActivity {
>>>>>>>> development:api/src/main/java/com/example/api/map/mapactivity/MapActivity.java
    private Long id;
    private Integer posX;
    private Integer posY;
    private ActivityType type;
    private String title;
    private Double points;
    private Long creationTime;
    private String description;
<<<<<<<< HEAD:api/src/main/java/com/example/api/chapter/requirement/task/MapTaskDTO.java
========
    private RequirementResponse requirements;
>>>>>>>> development:api/src/main/java/com/example/api/map/mapactivity/MapActivity.java
}
