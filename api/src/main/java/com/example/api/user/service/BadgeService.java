package com.example.api.user.service;

import com.example.api.course.model.Course;
import com.example.api.course.service.CourseService;
import com.example.api.course.validator.CourseValidator;
import com.example.api.user.dto.request.badge.BadgeAddForm;
import com.example.api.user.dto.request.badge.BadgeType;
import com.example.api.user.dto.request.badge.BadgeUpdateForm;
import com.example.api.user.dto.response.badge.BadgeResponse;
import com.example.api.user.dto.response.badge.UnlockedBadgeResponse;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.MissingAttributeException;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.user.model.User;
import com.example.api.user.model.badge.*;
import com.example.api.util.model.Image;
import com.example.api.util.model.ImageType;
import com.example.api.user.repository.BadgeRepository;
import com.example.api.user.repository.UnlockedBadgeRepository;
import com.example.api.util.repository.FileRepository;
import com.example.api.validator.BadgeValidator;
import com.example.api.util.visitor.BadgeVisitor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BadgeService {
    private final BadgeRepository badgeRepository;
    private final UnlockedBadgeRepository unlockedBadgeRepository;
    private final FileRepository fileRepository;
    private final UserService userService;
    private final BadgeValidator badgeValidator;
    private final BadgeVisitor badgeVisitor;
    private final CourseService courseService;
    private final CourseValidator courseValidator;

    public List<? extends BadgeResponse<?>> getAllBadges(Long courseId) throws EntityNotFoundException {
        Course course = courseService.getCourse(courseId);
        courseValidator.validateUserCanAccess(courseId);

        return badgeRepository.findBadgesByCourse(course)
                .stream()
                .sorted(Comparator.comparingLong(Badge::getId))
                .map(Badge::getResponse)
                .toList();
    }

    public List<UnlockedBadgeResponse> getAllUnlockedBadges(Long courseId) throws WrongUserTypeException, EntityNotFoundException, MissingAttributeException {
        checkAllBadges();
        User student = userService.getCurrentUser();
        return student.getUnlockedBadges()
                .stream()
                .filter(badge -> badge.getBadge().getCourse().getId().equals(courseId))
                .map(UnlockedBadgeResponse::new)
                .toList();
    }

    public void checkAllBadges() throws WrongUserTypeException, EntityNotFoundException, MissingAttributeException {
        User student = userService.getCurrentUser();
        List<Badge> studentBadges = student.getUnlockedBadges()
                .stream()
                .map(UnlockedBadge::getBadge)
                .toList();
        List<Badge> badges = badgeRepository.findAll()
                .stream()
                .filter(badge -> !studentBadges.contains(badge))
                .toList();
        for (Badge badge: badges) {
            if (badge.isGranted(badgeVisitor)) {
                UnlockedBadge unlockedBadge = new UnlockedBadge(badge, System.currentTimeMillis(), student);
                unlockedBadgeRepository.save(unlockedBadge);
                student.getUnlockedBadges().add(unlockedBadge);
            }
        }
    }

    public void deleteBadge(Long badgeId) {
        badgeRepository.deleteById(badgeId);
    }

    public void updateBadge(BadgeUpdateForm form) throws RequestValidationException, IOException {
        Long id = form.getId();
        Badge badge = badgeRepository.findBadgeById(id);
        badgeValidator.validateBadgeIsNotNull(badge, id);

        badge.update(form, badgeValidator);
        checkAllBadges();
        badgeRepository.save(badge);
    }

    public void addBadge(BadgeAddForm form) throws IOException, RequestValidationException {
        badgeValidator.validateBadgeForm(form);
        Badge badge = getBadgeFromForm(form);
        badgeRepository.save(badge);
    }

    private Badge getBadgeFromForm(BadgeAddForm form) throws RequestValidationException, IOException {

        Course course = courseService.getCourse(form.getCourseId());
        courseValidator.validateCourseOwner(course, userService.getCurrentUser());

        BadgeType type = form.getType();
        String title = form.getTitle();
        String description = form.getDescription();
        Image image = new Image("badge", form.getImage().getBytes(), ImageType.BADGE);
        fileRepository.save(image);
        String value = form.getValue();
        Boolean forValue = form.getForValue();
        Badge badge = null;
        switch (type) {
            case ACTIVITY_NUMBER ->
                    badge = new ActivityNumberBadge(null, title, description, image, badgeValidator.validateAndGetIntegerValue(value), course);
            case ACTIVITY_SCORE ->
                    badge = new ActivityScoreBadge(null, title, description, image, badgeValidator.validateAndGetDoubleValue(value), forValue, course);
            case CONSISTENCY ->
                    badge = new ConsistencyBadge(null, title, description, image, badgeValidator.validateAndGetIntegerValue(value), course);
            case FILE_TASK_NUMBER ->
                    badge = new FileTaskNumberBadge(null, title, description, image, badgeValidator.validateAndGetIntegerValue(value), course);
            case GRAPH_TASK_NUMBER ->
                    badge = new GraphTaskNumberBadge(null, title, description, image, badgeValidator.validateAndGetIntegerValue(value), course);
            case TOP_SCORE ->
                    badge = new TopScoreBadge(null, title, description, image, badgeValidator.validateAndGetDoubleValue(value), forValue, course);
        }
        return badge;
    }
}
