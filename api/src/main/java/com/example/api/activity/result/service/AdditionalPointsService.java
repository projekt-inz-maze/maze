package com.example.api.activity.result.service;

import com.example.api.activity.result.dto.request.AddAdditionalPointsForm;
import com.example.api.activity.task.dto.response.result.AdditionalPointsResponse;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.MissingAttributeException;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.activity.result.model.AdditionalPoints;
import com.example.api.user.model.User;
import com.example.api.activity.result.repository.AdditionalPointsRepository;
import com.example.api.user.repository.UserRepository;
import com.example.api.security.AuthenticationService;
import com.example.api.user.service.BadgeService;
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

    public void saveAdditionalPoints(AddAdditionalPointsForm form)
            throws WrongUserTypeException, EntityNotFoundException, MissingAttributeException {
        log.info("Saving additional points for student with id {}", form.getStudentId());
        User user = userRepository.findUserById(form.getStudentId());
        userValidator.validateStudentAccount(user, form.getStudentId());
        String professorEmail = authService.getAuthentication().getName();
        AdditionalPoints additionalPoints = new AdditionalPoints(null,
                user,
                form.getPoints(),
                form.getDateInMillis(),
                professorEmail,
                "",
                null);
        if (form.getDescription() != null) {
            additionalPoints.setDescription(form.getDescription());
        }
        additionalPointsRepository.save(additionalPoints);
        badgeService.checkAllBadges();
    }

    public List<AdditionalPointsResponse> getAdditionalPoints() {
        String email = authService.getAuthentication().getName();
        return getAdditionalPoints(email);
    }

    public List<AdditionalPointsResponse> getAdditionalPoints(String email) {
        User user = userRepository.findUserByEmail(email);
        log.info("Fetching additional points for user {}", email);
        List<AdditionalPoints> additionalPoints = additionalPointsRepository.findAllByUser(user);
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
