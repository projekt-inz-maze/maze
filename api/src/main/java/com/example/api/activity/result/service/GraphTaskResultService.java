package com.example.api.activity.result.service;

import com.example.api.activity.result.dto.response.SuperPowerResponse;
import com.example.api.activity.result.dto.response.SuperPowerUsageResponse;
import com.example.api.course.Course;
import com.example.api.course.coursemember.CourseMember;
import com.example.api.error.exception.*;
import com.example.api.activity.result.model.GraphTaskResult;
import com.example.api.activity.result.model.ResultStatus;
import com.example.api.activity.task.graphtask.GraphTask;
import com.example.api.question.Question;
import com.example.api.user.model.User;
import com.example.api.user.hero.model.Hero;
import com.example.api.activity.result.repository.GraphTaskResultRepository;
import com.example.api.activity.task.graphtask.GraphTaskRepository;
import com.example.api.question.QuestionRepository;
import com.example.api.security.LoggedInUserService;
import com.example.api.user.service.UserService;
import com.example.api.validator.ResultValidator;
import com.example.api.validator.UserValidator;
import com.example.api.activity.validator.ActivityValidator;
import com.example.api.util.calculator.PointsCalculator;
import com.example.api.util.calculator.TimeCalculator;
import com.example.api.user.hero.HeroVisitor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static com.example.api.error.exception.ExceptionMessage.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GraphTaskResultService {
    private final GraphTaskResultRepository graphTaskResultRepository;
    private final GraphTaskRepository graphTaskRepository;
    private final QuestionRepository questionRepository;
    private final PointsCalculator pointsCalculator;
    private final ResultValidator resultValidator;
    private final UserValidator userValidator;
    private final TimeCalculator timeCalculator;
    private final LoggedInUserService authService;
    private final UserService userService;
    private final ActivityValidator activityValidator;
    private final HeroVisitor heroVisitor;

    public Long getGraphTaskResultId(Long graphTaskId)
            throws WrongUserTypeException, EntityNotFoundException {
        User student = authService.getCurrentUser();
        userValidator.validateStudentAccount(student);
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

        CourseMember member = userService.getCurrentUserAndValidateStudentAccount()
                .getCourseMember(graphTask.getCourse(), true);

        if (graphTaskResultRepository.existsByGraphTaskAndMember(graphTask, member)) {
            throw new EntityAlreadyInDatabaseException(graphTaskResultAlreadyExists(id, member.getUser().getId()));
        }

        GraphTaskResult graphTaskResult = new GraphTaskResult(
                graphTask,
                System.currentTimeMillis(),
                ResultStatus.CHOOSE,
                graphTask.getQuestions().get(0),
                member);
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

    public List<GraphTaskResult> getAllGraphTaskResultsForStudentAndCourse(User student, Course course){
        return graphTaskResultRepository.findAllByUserAndCourse(student, course);
    }

    public SuperPowerResponse<?> useSuperPower(Long graphTaskId, Long questionId) throws RequestValidationException {
        User user = userService.getCurrentUserAndValidateStudentAccount();

        GraphTask graphTask = graphTaskRepository.findGraphTaskById(graphTaskId);
        GraphTaskResult result = getGraphTaskResultWithGraphTaskAndUser(graphTask, user);
        Question question = questionRepository.findQuestionById(questionId);

        Hero hero = user.getCourseMember(graphTask.getCourse())
                .orElseThrow()
                .getUserHero()
                .getHero();

        return hero.useSuperPower(heroVisitor, user, result, question);
    }

    public SuperPowerUsageResponse canSuperPowerBeUsed(Long graphTaskId) throws RequestValidationException {
        User user = userService.getCurrentUserAndValidateStudentAccount();
        GraphTaskResult result = getGraphTaskResultWithGraphTaskAndUser(graphTaskId, user);

        Hero hero = result.getMember().getUserHero().getHero();
        boolean canBeUsed = hero.canPowerBeUsed(result);
        String message = hero.getCanBeUsedMessage(result);

        return new SuperPowerUsageResponse(canBeUsed, message);
    }

    private GraphTaskResult getGraphTaskResultWithGraphTaskAndUser(GraphTask graphTask, User user) throws EntityNotFoundException {
        GraphTaskResult result = graphTaskResultRepository.findGraphTaskResultByGraphTaskAndUser(graphTask, user);
        resultValidator.validateResultIsNotNull(result, graphTask.getId(), user.getEmail());
        return result;
    }

    public GraphTaskResult getGraphTaskResultWithGraphTaskAndUser(Long graphTaskId, User user) throws EntityNotFoundException {
        GraphTaskResult result = graphTaskResultRepository.findGraphTaskResultByGraphTaskIdAndUser(graphTaskId, user);
        resultValidator.validateResultIsNotNull(result, graphTaskId, user.getEmail());
        return result;
    }
}
