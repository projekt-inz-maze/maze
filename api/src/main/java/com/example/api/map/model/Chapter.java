package com.example.api.map.model;
import com.example.api.activity.task.model.FileTask;
import com.example.api.activity.task.model.GraphTask;
import com.example.api.activity.task.model.Survey;
import com.example.api.course.model.Course;
import com.example.api.map.model.requirement.Requirement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Integer posX;
    private Integer posY;

    @OneToOne
    private Chapter nextChapter;

    @OneToOne
    private ActivityMap activityMap;

    @OneToMany
    private List<Requirement> requirements = new LinkedList<>();
    private Boolean isBlocked = true;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    public Chapter(String name, ActivityMap activityMap, Integer posX, Integer poxY, Course course) {
        this.name = name;
        this.activityMap = activityMap;
        this.posX = posX;
        this.posY = poxY;
        this.course = course;
    }

    public int getNoActivities() {
        return activityMap.getGraphTasks().size() + activityMap.getFileTasks().size() +
                activityMap.getInfos().size() + activityMap.getSurveys().size();
    }

    public double getMaxPoints() {
        double maxPoints = activityMap.getGraphTasks().stream().mapToDouble(GraphTask::getMaxPoints).sum();
        maxPoints += activityMap.getFileTasks().stream().mapToDouble(FileTask::getMaxPoints).sum();
        maxPoints += activityMap.getSurveys().stream().mapToDouble(Survey::getPoints).sum();
        return maxPoints;
    }
}
