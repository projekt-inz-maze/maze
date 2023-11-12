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
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class TaskResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private CourseMember member;

    @ManyToOne
    private Course course;

    private Double points;

    @CreationTimestamp
    Instant creationTime;

    private Long sendDateMillis;

    @ManyToOne
    @JoinColumn(name="activity_id")
    protected Activity activity;

    public abstract boolean isEvaluated();

    public TaskResult(Long id, Double points, Long sendDateMillis, Course course, CourseMember courseMember)
            throws WrongUserTypeException, EntityNotFoundException, MissingAttributeException {
        this.id = id;
        this.setPoints(points);
        this.sendDateMillis = sendDateMillis;
        this.course= course;
        this.member = courseMember;
    }

    public TaskResult(Double points, Long sendDateMillis, Course course, CourseMember courseMember) {
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
