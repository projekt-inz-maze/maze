package com.example.api.activity.task.graphtask;

import com.example.api.activity.task.Task;
import com.example.api.course.model.Course;
import com.example.api.activity.ActivityType;
import com.example.api.question.Question;
import com.example.api.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class GraphTask extends Task {
    private ActivityType activityType = ActivityType.EXPEDITION;
    @OneToMany
    @JoinColumn
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Question> questions = new LinkedList<>();
    private Long timeToSolveMillis;

    public GraphTask(CreateGraphTaskForm form,
                     User professor,
                     List<Question> questions,
                     long timeToSolveMillis,
                     double maxPoints,
                     Course course){
        super(form.getTitle(), form.getDescription(), form.getPosX(), form.getPosY(), professor,
                form.getRequiredKnowledge(), maxPoints, course);
        this.questions = questions;
        this.timeToSolveMillis = timeToSolveMillis;
        super.setExperience(maxPoints * 10);
    }
}
