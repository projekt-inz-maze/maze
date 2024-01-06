package com.example.api.activity.result.model;

import com.example.api.activity.task.graphtask.GraphTask;
import com.example.api.course.coursemember.CourseMember;
import com.example.api.question.answer.Answer;
import com.example.api.question.Question;
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
public class GraphTaskResult extends ActivityResult {
    @OneToMany
    private List<Answer> answers = new LinkedList<>();

    private int timeSpentSec;
    private Long startDateMillis;
    private ResultStatus status;
    private boolean finished;

    @OneToOne
    private Question currQuestion;

    @Override
    public boolean isEvaluated() {
        return this.getPoints() != null;
    }

    public GraphTask getGraphTask() {
        return (GraphTask) activity;
    }

    public void setGraphTask(GraphTask graphTask) {
        this.activity = graphTask;
    }

    public GraphTaskResult(GraphTask graphTask,
                           Long startDateMillis,
                           ResultStatus status,
                           Question currQuestion,
                           CourseMember member) {
        this.activity = graphTask;
        this.startDateMillis = startDateMillis;
        this.setSendDateMillis(startDateMillis);
        this.status = status;
        this.currQuestion = currQuestion;
        this.finished = false;
        this.setMember(member);
    }
}

