package com.example.api.course.validator;

import com.example.api.course.dto.request.SaveCourseForm;
import com.example.api.course.model.Course;
import com.example.api.error.exception.EntityAlreadyInDatabaseException;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.ExceptionMessage;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.group.dto.request.SaveGroupForm;
import com.example.api.group.model.Group;
import com.example.api.user.model.User;
import com.example.api.validator.UserValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class CourseValidator {

    UserValidator userValidator;
    public void validateCourse(boolean courseExists, SaveCourseForm form) throws RequestValidationException {
        if (courseExists) {
            log.error("Course with {} name already exists.", form.getName());
            throw new RequestValidationException(ExceptionMessage.COURSE_NAME_TAKEN);
        }

        if (form.getName().contains(";")) {
            log.error("Course name cannot contain a semicolon.");
            throw new RequestValidationException(ExceptionMessage.COURSE_CONTAINS_SEMICOLON);
        }
    }

    public void validateCourseOwner(Course course, Long courseId, User professor, String email) throws RequestValidationException {
        validateCourseIsNotNull(course, courseId);
        userValidator.validateProfessorAccount(professor, email);

        if (!course.getOwner().equals(professor)) {
            log.error("Course owner invalid.");
            throw new RequestValidationException(ExceptionMessage.COURSE_OWNER_INVALID);
        }
    }

    public void validateCourseIsNotNull(Course course, Long courseId) throws EntityNotFoundException {
        if(course == null) {
            log.error("Course with id {} was not found in database", courseId);
            throw new EntityNotFoundException("Course with id" + courseId + " not found in database");
        }
    }

}
