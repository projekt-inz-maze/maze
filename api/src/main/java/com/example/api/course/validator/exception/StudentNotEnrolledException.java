package com.example.api.course.validator.exception;

import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.user.model.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StudentNotEnrolledException extends EntityNotFoundException {
    public StudentNotEnrolledException(User student, Long courseId) {
        super("Student " + student.getId() + " not enrolled in course " + courseId);
    }
}
