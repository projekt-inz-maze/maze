package com.example.api.course.service;

import com.example.api.course.dto.request.SaveCourseForm;
import com.example.api.course.dto.response.CourseDTO;
import com.example.api.course.model.Course;
import com.example.api.course.repository.CourseRepository;
import com.example.api.course.validator.CourseValidator;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.security.AuthenticationService;
import com.example.api.user.model.AccountType;
import com.example.api.user.model.User;
import com.example.api.user.repository.UserRepository;
import com.example.api.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CourseService {
    private final CourseRepository courseRepository;
    private final CourseValidator courseValidator;
    private final UserValidator userValidator;
    private final AuthenticationService authService;
    private final UserRepository userRepository;


    public Long saveCourse(SaveCourseForm form) throws RequestValidationException {

        String email = authService.getAuthentication().getName();
        User professor = userRepository.findUserByEmail(email);
        userValidator.validateProfessorAccount(professor, email);

        log.info("Saving course to database with name {}", form.getName());
        boolean courseWithSameName = courseRepository.existsCourseByName(form.getName());
        courseValidator.validateCourse(courseWithSameName, form);

        Course course = new Course(null, form.getName(), form.getDescription(), false, professor);
        courseRepository.save(course);
        return course.getId();
    }

    public List<CourseDTO> getCoursesForUser() {
        String email = authService.getAuthentication().getName();
        User user = userRepository.findUserByEmail(email);
        userValidator.validateUserIsNotNull(user, email);

        if (user.getAccountType().equals(AccountType.PROFESSOR)) {
            return user.getCourses()
                    .stream()
                    .map(CourseDTO::new)
                    .toList();
        } else {
            return List.of(new CourseDTO(user.getGroup().getCourse()));
        }
    }

    public void deleteCourse(Long courseId) throws RequestValidationException {
        String email = authService.getAuthentication().getName();
        User professor = userRepository.findUserByEmail(email);
        Course course = courseRepository.getById(courseId);
        courseValidator.validateCourseOwner(course, courseId, professor, email);

        courseRepository.delete(course);
    }

    public CourseDTO editCourse(CourseDTO dto) throws RequestValidationException {
        String email = authService.getAuthentication().getName();
        User professor = userRepository.findUserByEmail(email);
        Course course = courseRepository.getById(dto.getId());
        courseValidator.validateCourseOwner(course, dto.getId(), professor, email);

        if (dto.getName() != null) {
            course.setName(dto.getName());
        }

        if (dto.getDescription() != null) {
            course.setDescription(dto.getDescription());
        }
        courseRepository.save(course);

        return new CourseDTO(course);
    }
}
