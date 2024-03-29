package com.example.api.course;

import com.example.api.course.coursemember.CourseMember;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.security.LoggedInUserService;
import com.example.api.user.hero.HeroFactory;
import com.example.api.user.hero.HeroRepository;
import com.example.api.user.hero.model.Hero;
import com.example.api.user.model.AccountType;
import com.example.api.user.model.User;
import com.example.api.validator.UserValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;


@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class CourseService {
    private final CourseRepository courseRepository;
    private final CourseValidator courseValidator;
    private final UserValidator userValidator;
    private final LoggedInUserService authService;
    private final HeroRepository heroRepository;
    private final HeroFactory heroFactory;

    public Long saveCourse(SaveCourseForm form) throws RequestValidationException {

        User professor = authService.getCurrentUser();
        userValidator.validateProfessorAccount(professor);

        log.info("Saving course to database with name {}", form.getName());
        boolean courseWithSameName = courseRepository.existsCourseByName(form.getName());
        courseValidator.validatePotentialCourse(courseWithSameName, form);

        Course course = new Course(null, form.getName(), form.getDescription(), professor);
        courseRepository.save(course);

        List<Hero> heroes = form.getHeroes()
                .stream()
                .map(hero -> heroFactory.getHero(hero.getType(), hero.getValue(), hero.getCoolDownMillis(), course))
                .toList();

        heroRepository.saveAll(heroes);

        return course.getId();
    }

    public List<CourseDTO> getCoursesForUser() {
        User user = authService.getCurrentUser();

        if (user.getAccountType().equals(AccountType.PROFESSOR)) {
            return user.getCourses()
                    .stream()
                    .map(CourseDTO::new)
                    .toList();
        } else {
            return user.getCourseMemberships()
                    .stream()
                    .map(CourseMember::getCourse)
                    .map(CourseDTO::new)
                    .toList();
        }
    }

    public void deleteCourse(Long courseId) throws RequestValidationException {
        User professor = authService.getCurrentUser();
        Course course = courseRepository.getById(courseId);
        courseValidator.validateCourseOwner(course, professor);
        courseRepository.delete(course);
    }

    public CourseDTO editCourse(CourseDTO dto) throws RequestValidationException {
        User professor = authService.getCurrentUser();
        Course course = courseRepository.getById(dto.getId());
        courseValidator.validateCourseOwner(course, professor);

        if (dto.getName() != null) {
            course.setName(dto.getName());
        }

        if (dto.getDescription() != null) {
            course.setDescription(dto.getDescription());
        }
        courseRepository.save(course);

        return new CourseDTO(course);
    }

    public Course getCourse(Long courseId) throws EntityNotFoundException {
        Course course = courseRepository.getById(courseId);
        courseValidator.validateCourseIsNotNull(course, courseId);
        return course;
    }
}
