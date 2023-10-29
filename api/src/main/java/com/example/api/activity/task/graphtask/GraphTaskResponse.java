package com.example.api.activity.task.graphtask;

import com.example.api.map.model.requirement.Requirement;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GraphTaskResponse {
    private Long id;
    private String title;
    private String description;
    private Integer posX;
    private Integer posY;
    private Double experience;
    private List<Requirement> requirements;
    private String requiredKnowledge;
    private Double maxPoints;
    private Long timeToSolveMillis;

    public GraphTaskResponse(GraphTask graphTask) {
        this.id = graphTask.getId();
        this.title = graphTask.getTitle();
        this.description = graphTask.getDescription();
        this.posX = graphTask.getPosX();
        this.posY = graphTask.getPosY();
        this.experience = graphTask.getExperience();
        this.requirements = graphTask.getRequirements();
        this.requiredKnowledge = graphTask.getRequiredKnowledge();
        this.maxPoints = graphTask.getMaxPoints();
        this.timeToSolveMillis = graphTask.getTimeToSolveMillis();
    }
}
