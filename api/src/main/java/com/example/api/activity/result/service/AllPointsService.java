package com.example.api.activity.result.service;

import com.example.api.activity.task.dto.response.result.AdditionalPointsListResponse;
import com.example.api.activity.task.dto.response.result.AdditionalPointsResponse;
import com.example.api.activity.task.dto.response.result.TaskPointsStatisticsResponse;
import com.example.api.activity.task.dto.response.result.TotalPointsResponse;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.activity.result.model.FileTaskResult;
import com.example.api.user.model.User;
import com.example.api.activity.result.repository.AdditionalPointsRepository;
import com.example.api.activity.result.repository.FileTaskResultRepository;
import com.example.api.activity.result.repository.GraphTaskResultRepository;
import com.example.api.activity.result.repository.SurveyResultRepository;
import com.example.api.user.repository.UserRepository;
import com.example.api.security.AuthenticationService;
import com.example.api.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AllPointsService {
    private final UserRepository userRepository;
    private final AuthenticationService authService;
    private final UserValidator userValidator;
    private final TaskResultService taskResultService;
    private final AdditionalPointsService additionalPointsService;
    private final GraphTaskResultRepository graphTaskResultRepository;
    private final FileTaskResultRepository fileTaskResultRepository;
    private final SurveyResultRepository surveyResultRepository;
    private final AdditionalPointsRepository additionalPointsRepository;

    public List<?> getAllPointsListForProfessor(String studentEmail) throws WrongUserTypeException {
        String professorEmail = authService.getAuthentication().getName();
        User professor = userRepository.findUserByEmail(professorEmail);
        userValidator.validateProfessorAccount(professor, professorEmail);
        log.info("Fetching student all points {} for professor {}", studentEmail, professorEmail);

        return getAllPointsList(studentEmail);
    }

    public List<?> getAllPointsListForStudent() throws WrongUserTypeException {
        String studentEmail = authService.getAuthentication().getName();
        log.info("Fetching all points for student {}", studentEmail);
        return getAllPointsList(studentEmail);
    }

    public TotalPointsResponse getAllPointsTotal() throws WrongUserTypeException {
        String studentEmail = authService.getAuthentication().getName();
        User student = userRepository.findUserByEmail(studentEmail);
        userValidator.validateStudentAccount(student, studentEmail);

        log.info("Fetching student all points total {}", studentEmail);
        AtomicReference<Double> totalPointsReceived = new AtomicReference<>(0D);
        AtomicReference<Double> totalPointsToReceive = new AtomicReference<>(0D);
        graphTaskResultRepository.findAllByUser(student)
                .stream()
                .filter(graphTaskResult -> graphTaskResult.getPointsReceived() != null)
                .forEach(graphTaskResult -> {
                    totalPointsReceived.updateAndGet(v -> v + graphTaskResult.getPointsReceived());
                    totalPointsToReceive.updateAndGet(v -> v + graphTaskResult.getGraphTask().getMaxPoints());
                });
        fileTaskResultRepository.findAllByUser(student)
                .stream()
                .filter(FileTaskResult::isEvaluated)
                .forEach(fileTaskResult -> {
                    totalPointsReceived.updateAndGet(v -> v + fileTaskResult.getPointsReceived());
                    totalPointsToReceive.updateAndGet(v -> v + fileTaskResult.getFileTask().getMaxPoints());
                });
        surveyResultRepository.findAllByUser(student)
                .forEach(surveyTaskResult -> {
                    totalPointsReceived.updateAndGet(v -> v + surveyTaskResult.getPointsReceived());
                    totalPointsToReceive.updateAndGet(v -> v + surveyTaskResult.getPointsReceived());
                });
        additionalPointsRepository.findAllByUser(student)
                .forEach(additionalPoints -> {
                    totalPointsReceived.updateAndGet(v -> v + additionalPoints.getPointsReceived());
                });
        return new TotalPointsResponse(totalPointsReceived.get(), totalPointsToReceive.get());
    }

    private List<?> getAllPointsList(String studentEmail) throws WrongUserTypeException {
        User student = userRepository.findUserByEmail(studentEmail);
        userValidator.validateStudentAccount(student, studentEmail);

        List<TaskPointsStatisticsResponse> taskPoints = taskResultService.getUserPointsStatistics(studentEmail);
        List<AdditionalPointsResponse> additionalPoints = additionalPointsService.getAdditionalPoints(studentEmail);
        List<AdditionalPointsListResponse> additionalPointsList = additionalPoints
                .stream()
                .map(AdditionalPointsListResponse::new)
                .toList();

        return Stream.of(taskPoints, additionalPointsList)
                .flatMap(Collection::stream)
                .sorted(((o1, o2) -> Long.compare(o2.getDateMillis(), o1.getDateMillis())))
                .toList();
    }
}
