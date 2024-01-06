package com.example.api.chapter;
import com.example.api.activity.task.filetask.FileTask;
import com.example.api.activity.task.graphtask.GraphTask;
import com.example.api.activity.survey.Survey;
import com.example.api.course.Course;
import com.example.api.map.ActivityMap;
import com.example.api.chapter.requirement.model.Requirement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

    @OneToOne(cascade=CascadeType.REMOVE, orphanRemoval = true)
    private ActivityMap activityMap;

    @OneToMany
    private List<Requirement> requirements = new LinkedList<>();
    private Boolean isBlocked = true;

    @ManyToOne
    @JoinColumn(name = "course_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Course course;

    public Chapter(String name, ActivityMap activityMap, Integer posX, Integer poxY, Course course) {
        this.name = name;
        this.activityMap = activityMap;
        this.posX = posX;
        this.posY = poxY;
        this.course = course;
    }

    public int getNoActivities() {
        return (int) activityMap.getActivityCount();
    }

    public double getMaxPoints() {
        double maxPoints = activityMap.getGraphTasks().stream().mapToDouble(GraphTask::getMaxPoints).sum();
        maxPoints += activityMap.getFileTasks().stream().mapToDouble(FileTask::getMaxPoints).sum();
        maxPoints += activityMap.getSurveys().stream().mapToDouble(Survey::getPoints).sum();
        return maxPoints;
    }
}
