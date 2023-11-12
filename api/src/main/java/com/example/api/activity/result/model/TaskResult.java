package com.example.api.activity.result.model;

import com.example.api.course.Course;
import com.example.api.course.coursemember.CourseMember;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.MissingAttributeException;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.activity.Activity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class TaskResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private CourseMember member;

    @ManyToOne
    private Course course;

    private Double points;
    private Long sendDateMillis;

    public abstract boolean isEvaluated();
    public abstract Activity getActivity();

    public TaskResult(Long id, Double points, Long sendDateMillis, Course course, CourseMember courseMember)
            throws WrongUserTypeException, EntityNotFoundException, MissingAttributeException {
        this.id = id;
        this.setPoints(points);
        this.sendDateMillis = sendDateMillis;
        this.course= course;
        this.member = courseMember;
    }

    public void setPoints(Double newPoints) {
        member.changePoints(newPoints - Optional.ofNullable(points).orElse(0D));
        points = newPoints;
    }
}
