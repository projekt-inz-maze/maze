package com.example.api.map.mapactivity;

import com.example.api.activity.ActivityType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
<<<<<<<< HEAD:api/src/main/java/com/example/api/map/mapactivity/MapActivity.java
public abstract class MapActivity {
========
public abstract class MapTaskDTO {
>>>>>>>> 31c5c0e73eae389ad40c474e0707f4f3bbbbce5b:api/src/main/java/com/example/api/chapter/requirement/task/MapTaskDTO.java
    private Long id;
    private Integer posX;
    private Integer posY;
    private ActivityType type;
    private String title;
    private Double points;
    private Long creationTime;
    private String description;
}
