package com.example.api.service.activity.feedback;

import com.example.api.dto.request.activity.result.SurveyResultForm;
import com.example.api.dto.response.activity.result.SurveyResultInfoResponse;
import com.example.api.error.exception.*;
import com.example.api.model.activity.result.SurveyResult;
import com.example.api.model.activity.task.Survey;
import com.example.api.model.user.User;
import com.example.api.repository.activity.result.SurveyResultRepository;
import com.example.api.repository.activity.task.SurveyRepository;
import com.example.api.repository.user.UserRepository;
import com.example.api.security.AuthenticationService;
import com.example.api.service.user.BadgeService;
import com.example.api.service.validator.FeedbackValidator;
import com.example.api.service.validator.UserValidator;
import com.example.api.service.validator.activity.ActivityValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SurveyResultService {
    private final SurveyResultRepository surveyResultRepository;
    private final UserRepository userRepository;
    private final SurveyRepository surveyRepository;
    private final UserValidator userValidator;
    private final AuthenticationService authService;
    private final ActivityValidator activityValidator;
    private final BadgeService badgeService;
    private final FeedbackValidator feedbackValidator;

    public SurveyResult saveSurveyResult(SurveyResult surveyResult) {
        return surveyResultRepository.save(surveyResult);
    }

    public SurveyResultInfoResponse saveSurveyResult(SurveyResultForm form) throws RequestValidationException {
        String email = authService.getAuthentication().getName();
        log.info("Saving user {} feedback for survey with id {}", email, form.getSurveyId());
        User student = userRepository.findUserByEmail(email);
        userValidator.validateStudentAccount(student, email);
        Long id = form.getSurveyId();
        Survey survey = surveyRepository.findSurveyById(id);
        activityValidator.validateActivityIsNotNull(survey, id);

        SurveyResult surveyResult = surveyResultRepository.findSurveyResultBySurveyAndUser(survey, student);
        if (surveyResult == null) {
            surveyResult = new SurveyResult();
            surveyResult.setSurvey(survey);
            surveyResult.setUser(student);
            surveyResult.setPointsReceived(survey.getMaxPoints());
            badgeService.checkAllBadges();
        }
        else if (!surveyResult.isEvaluated()) {
            surveyResult.setPointsReceived(survey.getMaxPoints());
        }

        surveyResult.setSendDateMillis(System.currentTimeMillis());
        surveyResult.setFeedback(form.getFeedback());

        if (form.getRate() < 1 || form.getRate() > 5) {
            log.error("SurveyResult rate {} is out of range", form.getRate());
            throw new RequestValidationException(ExceptionMessage.USER_FEEDBACK_RATE_OUT_OF_RANGE);
        }
        surveyResult.setRate(form.getRate());
        surveyResultRepository.save(surveyResult);
        return new SurveyResultInfoResponse(surveyResult);
    }

    public SurveyResultInfoResponse getSurveyResult(Long surveyId) throws WrongUserTypeException, EntityNotFoundException, MissingAttributeException {
        String email = authService.getAuthentication().getName();
        log.info("Getting user {} feedback for survey with id {}", email, surveyId);
        User student = userRepository.findUserByEmail(email);
        userValidator.validateStudentAccount(student, email);

        Survey survey = surveyRepository.findSurveyById(surveyId);
        activityValidator.validateActivityIsNotNull(survey, surveyId);

        SurveyResult surveyResult = surveyResultRepository.findSurveyResultBySurveyAndUser(survey, student);

        try {
            feedbackValidator.validateFeedbackIsNotNull(surveyResult, surveyId, email);
        }
        catch (EntityNotFoundException ex) {
            surveyResult = new SurveyResult(survey, null, null);
            surveyResult.setUser(student);
            surveyResultRepository.save(surveyResult);
            badgeService.checkAllBadges();
        }

        return new SurveyResultInfoResponse(surveyResult);

    }

    public List<SurveyResult> getAllSurveyResultsForStudent(User student) {
        return surveyResultRepository.findAllByUser(student)
                .stream()
                .filter(SurveyResult::isEvaluated)
                .toList();
    }
}
