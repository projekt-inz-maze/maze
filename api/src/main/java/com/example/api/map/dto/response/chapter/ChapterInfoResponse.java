package com.example.api.map.dto.response.chapter;

import com.example.api.map.dto.response.task.MapTaskDTO;
import com.example.api.map.model.Chapter;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ChapterInfoResponse {
    private Long id;
    private String name;
    private Integer noActivities;
    private Integer noGraphTasks;
    private Integer noSurveyTasks;
    private Integer noInfoTasks;
    private Integer noFileTasks;
    private Double maxPoints;
    private String mapSize;
    private List<? extends MapTaskDTO> mapTasks;
    private Integer posX;
    private Integer posY;
    private Long imageId;

    public ChapterInfoResponse(Chapter chapter, List<? extends MapTaskDTO> mapTasks) {
        this.id = chapter.getId();
        this.name = chapter.getName();
        this.noActivities = chapter.getNoActivities();
        this.noGraphTasks = chapter.getActivityMap().getGraphTasks().size();
        this.noSurveyTasks = chapter.getActivityMap().getSurveys().size();
        this.noInfoTasks = chapter.getActivityMap().getInfos().size();
        this.noFileTasks = chapter.getActivityMap().getFileTasks().size();
        this.maxPoints = chapter.getMaxPoints();
        this.mapSize = chapter.getActivityMap().getMapSizeX() + " x " + chapter.getActivityMap().getMapSizeY();
        this.mapTasks = mapTasks;
        this.posX = chapter.getPosX();
        this.posY = chapter.getPosY();
        this.imageId = chapter.getActivityMap().getImage().getId();
    }
}
