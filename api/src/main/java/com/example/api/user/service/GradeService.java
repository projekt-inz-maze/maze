package com.example.api.user.service;

import com.example.api.activity.result.model.*;
import com.example.api.user.dto.response.BasicUser;
import com.example.api.user.dto.response.grade.GradeResponse;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.user.model.AccountType;
import com.example.api.user.model.User;
import com.example.api.activity.result.repository.AdditionalPointsRepository;
import com.example.api.activity.result.repository.FileTaskResultRepository;
import com.example.api.activity.result.repository.GraphTaskResultRepository;
import com.example.api.activity.result.repository.SurveyResultRepository;
import com.example.api.user.repository.UserRepository;
import com.example.api.security.AuthenticationService;
import com.example.api.validator.UserValidator;
import com.example.api.util.csv.PointsToGradeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GradeService {
    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final AuthenticationService authService;
    private final GraphTaskResultRepository graphTaskResultRepository;
    private final FileTaskResultRepository fileTaskResultRepository;
    private final SurveyResultRepository surveyResultRepository;
    private final AdditionalPointsRepository additionalPointsRepository;

    public List<GradeResponse> getAllGrades() throws WrongUserTypeException {
        String email = authService.getAuthentication().getName();
        User professor = userRepository.findUserByEmail(email);
        userValidator.validateProfessorAccount(professor, email);

        return userRepository.findAllByAccountTypeEquals(AccountType.STUDENT)
                .stream()
                .map(this::getStudentFinalGrade)
                .sorted(Comparator.comparing(entry -> entry.getStudent().getLastName().toLowerCase() + entry.getStudent().getFirstName().toLowerCase()))
                .toList();
    }

    public GradeResponse getStudentFinalGrade(User student) {
        List<GraphTaskResult> graphTaskResults = graphTaskResultRepository.findAllByUser(student);
        List<FileTaskResult> fileTaskResults = fileTaskResultRepository.findAllByUser(student);
        List<SurveyResult> surveyResults = surveyResultRepository.findAllByUser(student)
                .stream()
                .filter(SurveyResult::isEvaluated)
                .toList();
        List<AdditionalPoints> additionalPointsList = additionalPointsRepository.findAllByUser(student);

        Double pointsReceived = Stream.of(graphTaskResults, fileTaskResults, surveyResults)
                .flatMap(Collection::stream)
                .filter(TaskResult::isEvaluated)
                .mapToDouble(TaskResult::getPointsReceived)
                .sum();

        Double additionalPoints = additionalPointsList.stream()
                .mapToDouble(AdditionalPoints::getPointsReceived)
                .sum();

        Double pointsPossibleToGet = Stream.of(graphTaskResults, fileTaskResults, surveyResults)
                .flatMap(Collection::stream)
                .filter(TaskResult::isEvaluated)
                .mapToDouble(this::getMaxPointsFromTaskResult)
                .sum();

        pointsReceived = pointsReceived + additionalPoints;
        PointsToGradeMapper pointsToGradeMapper = new PointsToGradeMapper();
        Double finalGrade = pointsToGradeMapper.getGrade(pointsReceived, pointsPossibleToGet);
        return new GradeResponse(new BasicUser(student), finalGrade);
    }

    private Double getMaxPointsFromTaskResult(TaskResult taskResult) {
        switch (taskResult.getActivity().getActivityType()) {
            case TASK -> {
                return ((FileTaskResult) taskResult).getFileTask().getMaxPoints();
            }
            case EXPEDITION -> {
                return ((GraphTaskResult) taskResult).getGraphTask().getMaxPoints();
            }
            case SURVEY -> {
                return ((SurveyResult) taskResult).getSurvey().getMaxPoints();
            }
        }
        return 0.0;
    }
}
