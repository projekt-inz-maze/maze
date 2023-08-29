package com.example.api.course.validator;

import com.example.api.course.dto.request.SaveCourseForm;
import com.example.api.course.model.Course;
import com.example.api.course.repository.CourseRepository;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.ExceptionMessage;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.security.LoggedInUserService;
import com.example.api.user.model.AccountType;
import com.example.api.user.model.User;
import com.example.api.validator.UserValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class CourseValidator {

    UserValidator userValidator;
    CourseRepository courseRepository;
    LoggedInUserService authService;

    public void validatePotentialCourse(boolean courseExists, SaveCourseForm form) throws RequestValidationException {
        if (courseExists) {
            log.error("Course with {} name already exists.", form.getName());
            throw new RequestValidationException(ExceptionMessage.COURSE_NAME_TAKEN);
        }

        if (form.getName().contains(";")) {
            log.error("Course name cannot contain a semicolon.");
            throw new RequestValidationException(ExceptionMessage.COURSE_CONTAINS_SEMICOLON);
        }
    }

    public void validateCourseOwner(Course course, User professor) throws RequestValidationException {
        userValidator.validateProfessorAccount(professor);

        if (!course.getOwner().equals(professor)) {
            log.error("Course owner invalid.");
            throw new RequestValidationException(ExceptionMessage.COURSE_OWNER_INVALID);
        }
    }

    public void validateCourseOwner(Long courseId, User professor) throws RequestValidationException {
        userValidator.validateProfessorAccount(professor);

        if (!courseRepository.existsCourseByIdIsAndOwnerIs(courseId, professor)) {
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

    public void validateCurrentUserCanAccess(Long courseId) throws EntityNotFoundException {
        validateUserCanAccess(authService.getCurrentUser(), courseId);
    }

    public void validateUserCanAccess(User user, Long courseId) throws EntityNotFoundException {
        if (user.getAccountType().equals(AccountType.PROFESSOR)) {
            validateProfessorCanAccess(user, courseId);
        }
        if (user.getAccountType().equals(AccountType.STUDENT)) {
            validateStudentCanAccess(user, courseId);
        }
    }

    public void validateStudentCanAccess(User user, Long courseId) throws EntityNotFoundException {
        if (!user.inCourse(courseId)) {
            log.error("User {} does not have access to course {}", user.getId(), courseId);
            throw new EntityNotFoundException("User " + user.getId() +" does not have access to course " + courseId);
        }
    }

    public void validateProfessorCanAccess(User user, Long courseId) throws EntityNotFoundException {
        if (user.getCourses().stream().map(Course::getId).noneMatch(id -> id.equals(courseId))) {
            log.error("User {} does not have access to course {}", user.getId(), courseId);
            throw new EntityNotFoundException("User " + user.getId() +" does not have access to course " + courseId);
        }
    }


}
