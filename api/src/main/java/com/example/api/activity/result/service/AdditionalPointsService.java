package com.example.api.activity.result.service;

import com.example.api.activity.result.dto.request.AddAdditionalPointsForm;
import com.example.api.activity.task.dto.response.result.AdditionalPointsResponse;
import com.example.api.course.model.Course;
import com.example.api.course.service.CourseService;
import com.example.api.course.validator.CourseValidator;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.activity.result.model.AdditionalPoints;
import com.example.api.user.model.User;
import com.example.api.activity.result.repository.AdditionalPointsRepository;
import com.example.api.user.repository.UserRepository;
import com.example.api.security.AuthenticationService;
import com.example.api.user.service.BadgeService;
import com.example.api.user.service.UserService;
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
public class AdditionalPointsService {
    private final AdditionalPointsRepository additionalPointsRepository;
    private final UserRepository userRepository;
    private final AuthenticationService authService;
    private final BadgeService badgeService;
    private final UserValidator userValidator;
    private final CourseService courseService;
    private final CourseValidator courseValidator;

    public void saveAdditionalPoints(AddAdditionalPointsForm form)
            throws RequestValidationException {
        log.info("Saving additional points for student with id {}", form.getStudentId());
        User user = userRepository.findUserById(form.getStudentId());
        userValidator.validateStudentAccount(user, form.getStudentId());
        User professor = authService.getCurrentUser();
        Course course = courseService.getCourse(form.getCourseId());
        courseValidator.validateCourseOwner(course, professor);

        AdditionalPoints additionalPoints = new AdditionalPoints(null,
                user,
                form.getPoints(),
                form.getDateInMillis(),
                professor.getEmail(),
                "",
                course);
        if (form.getDescription() != null) {
            additionalPoints.setDescription(form.getDescription());
        }
        additionalPointsRepository.save(additionalPoints);
        badgeService.checkAllBadges(user.getCourseMember(course).orElseThrow());
    }

    public List<AdditionalPointsResponse> getAdditionalPoints(Long courseId) throws EntityNotFoundException {
        User user = authService.getCurrentUser();
        return getAdditionalPoints(user, courseId);
    }

    public List<AdditionalPointsResponse> getAdditionalPoints(User user, Long courseId) throws EntityNotFoundException {
        log.info("Fetching additional points for user {}", user.getEmail());
        Course course = courseService.getCourse(courseId);
        List<AdditionalPoints> additionalPoints = additionalPointsRepository.findAllByUserAndCourse(user, course);
        return additionalPoints.stream()
                .map(additionalPoint -> {
                    String professorEmail = additionalPoint.getProfessorEmail();
                    User professor = userRepository.findUserByEmail(professorEmail);
                    String professorName = professor.getFirstName() + " " + professor.getLastName();
                    return new AdditionalPointsResponse(additionalPoint.getSendDateMillis(),
                            professorName,
                            additionalPoint.getPointsReceived(),
                            additionalPoint.getDescription());
                })
                .sorted(((o1, o2) -> Long.compare(o2.getDateMillis(), o1.getDateMillis())))
                .toList();
    }
}
