package com.example.api.activity.result.model;

import com.example.api.course.model.Course;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.MissingAttributeException;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.activity.task.model.Activity;
import com.example.api.user.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public abstract class TaskResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne
    private Course course;

    private Double pointsReceived;
    private Long sendDateMillis;

    public abstract boolean isEvaluated();
    public abstract Activity getActivity();

    public TaskResult(Long id, User user, Double pointsReceived, Long sendDateMillis, Course course)
            throws WrongUserTypeException, EntityNotFoundException, MissingAttributeException {
        this.id = id;
        this.user = user;
        this.setPointsReceived(pointsReceived);
        this.sendDateMillis = sendDateMillis;
        this.course= course;
    }

    public void setPointsReceived(Double newPoints) {
        if (pointsReceived == null) user.changePoints(newPoints);
        else user.changePoints(newPoints - pointsReceived);
        pointsReceived = newPoints;
    }
}
