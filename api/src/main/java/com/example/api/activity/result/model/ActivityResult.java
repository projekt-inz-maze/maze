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
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.Instant;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class ActivityResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @ManyToOne
    public CourseMember member;

    @ManyToOne
    protected Course course;

    protected Double points;

    @CreationTimestamp
    protected Instant creationTime;

    protected Long sendDateMillis;

    @ManyToOne
    @JoinColumn(name="activity_id")
    public Activity activity;

    public abstract boolean isEvaluated();

    public ActivityResult(Long id, Double points, Long sendDateMillis, Course course, CourseMember courseMember)
            throws WrongUserTypeException, EntityNotFoundException, MissingAttributeException {
        this.id = id;
        this.sendDateMillis = sendDateMillis;
        this.course= course;
        this.member = courseMember;
        this.setPoints(points);
    }

    public ActivityResult(Double points, Long sendDateMillis, Course course, CourseMember courseMember) {
        this.sendDateMillis = sendDateMillis;
        this.course= course;
        this.member = courseMember;
        this.setPoints(points);
    }

    public void setPoints(Double newPoints) {
        member.changePoints(newPoints - Optional.ofNullable(points).orElse(0D));
        points = newPoints;
    }
}
