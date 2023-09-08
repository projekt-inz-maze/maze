package com.example.api.activity.result.model;

import com.example.api.activity.task.model.Activity;
import com.example.api.activity.task.model.GraphTask;
import com.example.api.course.model.CourseMember;
import com.example.api.question.model.Answer;
import com.example.api.question.model.Question;
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
public class GraphTaskResult extends TaskResult {
    @OneToMany
    private List<Answer> answers = new LinkedList<>();

    @ManyToOne
    @JoinColumn(name = "graphTask_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private GraphTask graphTask;

    private int timeSpentSec;
    private Long startDateMillis;
    private ResultStatus status;
    private boolean finished;

    @OneToOne
    private Question currQuestion;

    @Override
    public boolean isEvaluated() {
        return this.getPointsReceived() != null;
    }

    @Override
    public Activity getActivity() {
        return graphTask;
    }

    public GraphTaskResult(GraphTask graphTask,
                           Long startDateMillis,
                           ResultStatus status,
                           Question currQuestion,
                           CourseMember member) {
        this.graphTask = graphTask;
        this.startDateMillis = startDateMillis;
        this.setSendDateMillis(startDateMillis);
        this.status = status;
        this.currQuestion = currQuestion;
        this.finished = false;
        this.setMember(member);
    }
}

