package com.example.api.activity.task.service;

import com.example.api.activity.task.dto.request.create.CreateSurveyChapterForm;
import com.example.api.activity.task.dto.request.create.CreateSurveyForm;
import com.example.api.activity.task.dto.request.edit.EditSurveyForm;
import com.example.api.activity.result.dto.response.SurveyResultInfoResponse;
import com.example.api.activity.task.dto.response.SurveyInfoResponse;
import com.example.api.course.model.Course;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.activity.result.model.SurveyResult;
import com.example.api.activity.task.model.Survey;
import com.example.api.map.model.Chapter;
import com.example.api.user.model.User;
import com.example.api.activity.result.repository.SurveyResultRepository;
import com.example.api.activity.task.repository.SurveyRepository;
import com.example.api.map.repository.ChapterRepository;
import com.example.api.user.repository.UserRepository;
import com.example.api.security.AuthenticationService;
import com.example.api.map.service.RequirementService;
import com.example.api.validator.ChapterValidator;
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
public class SurveyService {
    private final SurveyRepository surveyRepository;
    private final UserRepository userRepository;
    private final ChapterRepository chapterRepository;
    private final ActivityValidator activityValidator;
    private final UserValidator userValidator;
    private final AuthenticationService authService;
    private final RequirementService requirementService;
    private final ChapterValidator chapterValidator;
    private final SurveyResultRepository surveyResultRepository;

    public Survey saveSurvey(Survey survey){
        return surveyRepository.save(survey);
    }

    public SurveyInfoResponse getSurveyInfo(Long id) throws EntityNotFoundException, WrongUserTypeException {
        String email = authService.getAuthentication().getName();
        User student = userRepository.findUserByEmail(email);
        userValidator.validateStudentAccount(student);
        Survey survey = surveyRepository.findSurveyById(id);
        activityValidator.validateActivityIsNotNull(survey, id);
        log.info("Fetching survey info");

        SurveyInfoResponse response = new SurveyInfoResponse(survey);
        SurveyResult feedback = surveyResultRepository.findSurveyResultBySurveyAndUser(survey, student);
        if (feedback != null) {
            response.setFeedback(new SurveyResultInfoResponse(feedback));
        }

        return response;
    }

    public void createSurvey(CreateSurveyChapterForm chapterForm) throws RequestValidationException {
        log.info("Starting the creation of survey");
        CreateSurveyForm form = chapterForm.getForm();
        Chapter chapter = chapterRepository.findChapterById(chapterForm.getChapterId());

        chapterValidator.validateChapterIsNotNull(chapter, chapterForm.getChapterId());
        activityValidator.validateCreateSurveyForm(form);
        activityValidator.validateActivityPosition(form, chapter);

        String email = authService.getAuthentication().getName();
        User professor = userRepository.findUserByEmail(email);
        userValidator.validateProfessorAccount(professor);

        Survey survey = new Survey(
                form,
                professor,
                null
        );
        survey.setRequirements(requirementService.getDefaultRequirements(true));
        surveyRepository.save(survey);
        chapter.getActivityMap().getSurveys().add(survey);
    }

    public List<Survey> getStudentSurvey(Course course) {
        return surveyRepository.findAllByCourseAndIsBlockedFalse(course);
    }

    public void editSurvey(Survey survey, EditSurveyForm form) {
        CreateSurveyForm surveyForm = (CreateSurveyForm) form.getActivityBody();
        survey.setPoints(surveyForm.getPoints());
    }
}
