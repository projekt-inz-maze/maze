package com.example.api.activity.feedback.service;

import com.example.api.activity.result.dto.request.SurveyResultForm;
import com.example.api.activity.result.dto.response.SurveyResultInfoResponse;
import com.example.api.error.exception.*;
import com.example.api.activity.result.model.SurveyResult;
import com.example.api.activity.task.model.Survey;
import com.example.api.user.model.User;
import com.example.api.activity.result.repository.SurveyResultRepository;
import com.example.api.activity.task.repository.SurveyRepository;
import com.example.api.user.repository.UserRepository;
import com.example.api.security.AuthenticationService;
import com.example.api.user.service.BadgeService;
import com.example.api.user.service.UserService;
import com.example.api.validator.FeedbackValidator;
import com.example.api.validator.UserValidator;
import com.example.api.activity.validator.ActivityValidator;
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
    private final SurveyRepository surveyRepository;
    private final ActivityValidator activityValidator;
    private final BadgeService badgeService;
    private final UserService userService;

    public SurveyResult saveSurveyResult(SurveyResult surveyResult) {
        return surveyResultRepository.save(surveyResult);
    }

    public SurveyResultInfoResponse saveSurveyResult(SurveyResultForm form) throws RequestValidationException {
        User student = userService.getCurrentUserAndValidateStudentAccount();
        log.info("Saving user {} feedback for survey with id {}", student.getId(), form.getSurveyId());
        Long id = form.getSurveyId();
        Survey survey = surveyRepository.findSurveyById(id);
        activityValidator.validateActivityIsNotNull(survey, id);

        SurveyResult surveyResult = surveyResultRepository.findSurveyResultBySurveyAndUser(survey, student);
        if (surveyResult == null) {
            surveyResult = new SurveyResult();
            surveyResult.setSurvey(survey);
            surveyResult.setUser(student);
            surveyResult.setPointsReceived(survey.getMaxPoints());
            badgeService.checkAllBadges(student.getCourseMember(survey.getCourse()).orElseThrow());
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

    public SurveyResultInfoResponse getOrCreateSurveyResult(Long surveyId) throws WrongUserTypeException, EntityNotFoundException, MissingAttributeException {
        User student = userService.getCurrentUserAndValidateStudentAccount();
        log.info("Getting user {} feedback for survey with id {}", student.getEmail(), surveyId);

        Survey survey = surveyRepository.findSurveyById(surveyId);
        activityValidator.validateActivityIsNotNull(survey, surveyId);

        SurveyResult surveyResult = surveyResultRepository.findSurveyResultBySurveyAndUser(survey, student);

        if (surveyResult == null) {
            surveyResult = new SurveyResult(survey, null, null);
            surveyResult.setUser(student);
            surveyResultRepository.save(surveyResult);
            badgeService.checkAllBadges(student.getCourseMember(survey.getCourse()).orElseThrow());
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
