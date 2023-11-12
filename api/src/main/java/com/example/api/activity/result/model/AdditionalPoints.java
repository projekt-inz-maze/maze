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

import javax.persistence.Entity;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class AdditionalPoints extends TaskResult{
    private String professorEmail;
    private String description;

    public AdditionalPoints(Long id,
                            Double points,
                            Long sendDateMillis,
                            String professorEmail,
                            String description,
                            Course course,
                            CourseMember courseMember)
            throws WrongUserTypeException, EntityNotFoundException, MissingAttributeException {
        super(id, points, sendDateMillis, course, courseMember);
        this.professorEmail = professorEmail;
        this.description = description;
    }

    @Override
    public boolean isEvaluated() {
        return true;
    }
}
