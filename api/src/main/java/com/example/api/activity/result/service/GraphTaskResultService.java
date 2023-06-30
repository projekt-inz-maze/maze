package com.example.api.activity.result.service;

import com.example.api.activity.result.dto.response.SuperPowerResponse;
import com.example.api.activity.result.dto.response.SuperPowerUsageResponse;
import com.example.api.error.exception.*;
import com.example.api.activity.result.model.GraphTaskResult;
import com.example.api.activity.result.model.ResultStatus;
import com.example.api.activity.task.model.GraphTask;
import com.example.api.question.model.Question;
import com.example.api.user.model.User;
import com.example.api.user.model.hero.Hero;
import com.example.api.activity.repository.result.GraphTaskResultRepository;
import com.example.api.activity.repository.task.GraphTaskRepository;
import com.example.api.question.repository.QuestionRepository;
import com.example.api.user.repository.UserRepository;
import com.example.api.security.AuthenticationService;
import com.example.api.user.service.UserService;
import com.example.api.validator.ResultValidator;
import com.example.api.validator.UserValidator;
import com.example.api.activity.validator.ActivityValidator;
import com.example.api.util.calculator.PointsCalculator;
import com.example.api.util.calculator.TimeCalculator;
import com.example.api.util.visitor.HeroVisitor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GraphTaskResultService {
    private final GraphTaskResultRepository graphTaskResultRepository;
    private final GraphTaskRepository graphTaskRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final PointsCalculator pointsCalculator;
    private final ResultValidator resultValidator;
    private final UserValidator userValidator;
    private final TimeCalculator timeCalculator;
    private final AuthenticationService authService;
    private final UserService userService;
    private final ActivityValidator activityValidator;
    private final HeroVisitor heroVisitor;

    public Long getGraphTaskResultId(Long graphTaskId)
            throws WrongUserTypeException, EntityNotFoundException {
        String email = authService.getAuthentication().getName();
        User student = userRepository.findUserByEmail(email);
        userValidator.validateStudentAccount(student, email);
        GraphTask graphTask = graphTaskRepository.findGraphTaskById(graphTaskId);
        activityValidator.validateActivityIsNotNull(graphTask, graphTaskId);
        GraphTaskResult graphTaskResult = graphTaskResultRepository.findGraphTaskResultByGraphTaskAndUser(graphTask, student);
        return graphTaskResult == null ? null : graphTaskResult.getId();
    }

    public GraphTaskResult saveGraphTaskResult(GraphTaskResult result) {
        return graphTaskResultRepository.save(result);
    }

    public void startGraphTaskResult(Long id) throws EntityNotFoundException, WrongUserTypeException, EntityAlreadyInDatabaseException {
        log.info("Saving graph task result");
        GraphTask graphTask = graphTaskRepository.findGraphTaskById(id);
        activityValidator.validateActivityIsNotNull(graphTask, id);

        String email = authService.getAuthentication().getName();
        User user = userRepository.findUserByEmail(email);
        userValidator.validateStudentAccount(user, email);

        GraphTaskResult result = graphTaskResultRepository.findGraphTaskResultByGraphTaskAndUser(graphTask, user);
        resultValidator.validateGraphTaskResultIsNotInDatabase(result, id, email);

        GraphTaskResult graphTaskResult = new GraphTaskResult(
                graphTask,
                user,
                System.currentTimeMillis(),
                ResultStatus.CHOOSE,
                graphTask.getQuestions().get(0)
        );
        graphTaskResultRepository.save(graphTaskResult);
    }

    public Double getPointsFromClosedQuestions(Long id) throws EntityNotFoundException {
        log.info("Calculating points from closed questions for graph task result with id {}", id);
        GraphTaskResult result = graphTaskResultRepository.findGraphTaskResultById(id);
        activityValidator.validateTaskResultIsNotNull(result, id);
        return pointsCalculator.calculatePointsForClosedQuestions(result);
    }

    public Double getPointsFromOpenedQuestions(Long id) throws EntityNotFoundException {
        log.info("Calculating points from opened questions for graph task result with id {}", id);
        GraphTaskResult result = graphTaskResultRepository.findGraphTaskResultById(id);
        activityValidator.validateTaskResultIsNotNull(result, id);
        return pointsCalculator.calculatePointsForOpenedQuestions(result);
    }

    public Double getAllPoints(Long id) throws EntityNotFoundException {
        log.info("Fetching points from graph task result with id {}", id);
        GraphTaskResult result = graphTaskResultRepository.findGraphTaskResultById(id);
        activityValidator.validateTaskResultIsNotNull(result, id);
        return result.getPointsReceived();
    }

    public Double getMaxAvailablePoints(Long id) throws EntityNotFoundException {
        log.info("Calculating maximum available points for graph task result with id {}", id);
        GraphTaskResult result = graphTaskResultRepository.findGraphTaskResultById(id);
        activityValidator.validateTaskResultIsNotNull(result, id);
        return pointsCalculator.calculateMaxAvailablePoints(result);
    }

    public Double getMaxClosedPoints(Long id) throws EntityNotFoundException {
        log.info("Calculating maximum closed points for graph task result with id {}", id);
        GraphTaskResult result = graphTaskResultRepository.findGraphTaskResultById(id);
        activityValidator.validateTaskResultIsNotNull(result, id);
        return pointsCalculator.calculateMaxClosedPoints(result);
    }

    public Double getMaxOpenedPoints(Long id) throws EntityNotFoundException {
        log.info("Calculating maximum opened points for graph task result with id {}", id);
        GraphTaskResult result = graphTaskResultRepository.findGraphTaskResultById(id);
        activityValidator.validateTaskResultIsNotNull(result, id);
        return pointsCalculator.calculateMaxOpenedPoints(result);
    }

    public Long getTimeRemaining(Long resultId) throws EntityNotFoundException, EntityRequiredAttributeNullException {
        log.info("Calculating time remaining for graph task result with id {}", resultId);
        GraphTaskResult graphTaskResult = graphTaskResultRepository.findGraphTaskResultById(resultId);
        activityValidator.validateGraphTaskResultExistsAndHasStartDate(graphTaskResult, resultId);
        GraphTask graphTask = graphTaskResult.getGraphTask();
        return timeCalculator.getTimeRemaining(graphTaskResult.getStartDateMillis(), graphTask.getTimeToSolveMillis());
    }

    public Long getTimeLeftAfterEnd(Long resultId) throws EntityNotFoundException, EntityRequiredAttributeNullException {
        log.info("Calculating how much time left after last action for graph task result with id {}", resultId);
        GraphTaskResult graphTaskResult = graphTaskResultRepository.findGraphTaskResultById(resultId);
        activityValidator.validateGraphTaskResultExistsAndHasStartAndEndDate(graphTaskResult, resultId);
        GraphTask graphTask = graphTaskResult.getGraphTask();
        return timeCalculator.getTimeLeftAfterLastAnswer(graphTaskResult.getStartDateMillis(), graphTask.getTimeToSolveMillis(), graphTaskResult.getSendDateMillis());
    }

    public Long getTimeRemaining(GraphTaskResult result) throws EntityNotFoundException, EntityRequiredAttributeNullException {
        log.info("Calculating time remaining for graph task result with id {}", result.getId());
        activityValidator.validateGraphTaskResultExistsAndHasStartDate(result, result.getId());
        GraphTask graphTask = result.getGraphTask();
        return timeCalculator.getTimeRemaining(result.getStartDateMillis(), graphTask.getTimeToSolveMillis());
    }

    public GraphTaskResult getGraphTaskResult(Long graphTaskId, String email)
            throws EntityNotFoundException, WrongUserTypeException {
        User user = userRepository.findUserByEmail(email);
        userValidator.validateStudentAccount(user, email);

        GraphTask graphTask = graphTaskRepository.findGraphTaskById(graphTaskId);
        activityValidator.validateActivityIsNotNull(graphTask, graphTaskId);

        return getGraphTaskResultWithGraphTaskAndUser(graphTask, user);
    }

    public List<GraphTaskResult> getAllGraphTaskResultsForStudent(User student){
        return graphTaskResultRepository.findAllByUser(student);
    }

    public SuperPowerResponse<?> useSuperPower(Long graphTaskId, Long questionId) throws RequestValidationException {
        User user = userService.getCurrentUserAndValidateStudentAccount();
        Hero hero = user.getUserHero().getHero();

        GraphTask graphTask = graphTaskRepository.findGraphTaskById(graphTaskId);
        GraphTaskResult result = getGraphTaskResultWithGraphTaskAndUser(graphTask, user);
        Question question = questionRepository.findQuestionById(questionId);

        return hero.useSuperPower(heroVisitor, user, result, question);
    }

    public SuperPowerUsageResponse canSuperPowerBeUsed(Long graphTaskId) throws RequestValidationException {
        User user = userService.getCurrentUserAndValidateStudentAccount();
        Hero hero = user.getUserHero().getHero();

        GraphTask graphTask = graphTaskRepository.findGraphTaskById(graphTaskId);
        GraphTaskResult result = getGraphTaskResultWithGraphTaskAndUser(graphTask, user);

        boolean canBeUsed = hero.canPowerBeUsed(user, result);
        String message = hero.getCanBeUsedMessage(user, result);

        return new SuperPowerUsageResponse(canBeUsed, message);
    }

    private GraphTaskResult getGraphTaskResultWithGraphTaskAndUser(GraphTask graphTask, User user) throws EntityNotFoundException {
        GraphTaskResult result = graphTaskResultRepository.findGraphTaskResultByGraphTaskAndUser(graphTask, user);
        resultValidator.validateResultIsNotNull(result, graphTask.getId(), user.getEmail());
        return result;
    }
}
