package com.example.api.question.service;

import com.example.api.activity.result.dto.request.QuestionActionForm;
import com.example.api.activity.task.dto.response.result.question.QuestionDetails;
import com.example.api.activity.task.dto.response.result.question.QuestionInfoResponse;
import com.example.api.activity.task.dto.response.result.question.QuestionList;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.EntityRequiredAttributeNullException;
import com.example.api.error.exception.ExceptionMessage;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.activity.result.model.GraphTaskResult;
import com.example.api.activity.result.model.ResultStatus;
import com.example.api.question.model.Answer;
import com.example.api.question.model.Question;
import com.example.api.user.model.User;
import com.example.api.question.repository.AnswerRepository;
import com.example.api.question.repository.QuestionRepository;
import com.example.api.user.repository.UserRepository;
import com.example.api.security.AuthenticationService;
import com.example.api.activity.result.service.GraphTaskResultService;
import com.example.api.user.service.BadgeService;
import com.example.api.user.service.UserService;
import com.example.api.validator.QuestionValidator;
import com.example.api.validator.ResultValidator;
import com.example.api.util.calculator.PointsCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.naming.TimeLimitExceededException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final QuestionValidator questionValidator;
    private final ResultValidator resultValidator;
    private final AuthenticationService authService;
    private final GraphTaskResultService graphTaskResultService;
    private final BadgeService badgeService;
    private final PointsCalculator pointsCalculator;
    private final UserService userService;

    public List<Question> saveQuestions(List<Question> questions) {
        return questionRepository.saveAll(questions);
    }

    public Question getQuestion(Long id) throws EntityNotFoundException {
        log.info("Fetching question with id {}", id);
        Question question = questionRepository.findQuestionById(id);
        questionValidator.validateQuestionIsNotNull(question, id);
        return question;
    }

    public Long performQuestionAction(QuestionActionForm form) throws RequestValidationException, TimeLimitExceededException {
        ResultStatus status = form.getStatus();
        Long graphTaskId = form.getGraphTaskId();
        User user = userService.getCurrentUserAndValidateStudentAccount();

        GraphTaskResult result = graphTaskResultService.getGraphTaskResultWithGraphTaskAndUser(graphTaskId, user);

        Long timeRemaining = graphTaskResultService.getTimeRemaining(result);
        if (timeRemaining < 0) {
            throw new TimeLimitExceededException(ExceptionMessage.TIME_REMAINING_IS_UP);
        }

        switch (status) {
            case CHOOSE -> {
                resultValidator.validateGraphTaskResultStatusIsChoose(result);
                Long questionId = form.getQuestionId();
                Question question = questionRepository.findQuestionById(questionId);
                questionValidator.validateQuestionIsNotNull(question, questionId);
                result.setSendDateMillis(System.currentTimeMillis());
                result.setCurrQuestion(question);
                result.setStatus(ResultStatus.ANSWER);
                return timeRemaining;
            }
            case ANSWER -> {
                resultValidator.validateGraphTaskResultStatusIsAnswer(result);
                Question question = result.getCurrQuestion();
                Answer answer = resultValidator.validateAndCreateAnswer(form.getAnswerForm(), question);
                answer.setQuestion(question);
                answerRepository.save(answer);
                result.setSendDateMillis(System.currentTimeMillis());
                result.getAnswers().add(answer);
                result.setStatus(ResultStatus.CHOOSE);

                // counting current state of points
                double allPoints = pointsCalculator.calculateAllPoints(result);
                result.setPointsReceived(allPoints);
                
                // if it's the last question, set finished
                List<Question> nextQuestions = question.getNext();

                if (nextQuestions.isEmpty()){
                    result.setFinished(true);
                    result.getMember().getUserHero().setTimesSuperPowerUsedInResult(0);
                    log.info("Expedition finished");
                    badgeService.checkAllBadges(result.getMember());
                }

                return timeRemaining;
            }
            default ->
                    throw new RequestValidationException("Status must be CHOOSE or ANSWER");
        }
    }

    public QuestionInfoResponse getQuestionInfo(Long graphTaskId) throws RequestValidationException {
        User user = authService.getCurrentUser();
        GraphTaskResult result = graphTaskResultService.getGraphTaskResultWithGraphTaskAndUser(graphTaskId, user);
        ResultStatus status = result.getStatus();

        switch (status) {
            case CHOOSE -> {
                List<QuestionList> questionList = getNextQuestions(
                        graphTaskId,
                        result
                );
                return new QuestionInfoResponse(
                        status,
                        graphTaskResultService.getTimeRemaining(result),
                        pointsCalculator.calculateAllPoints(result),
                        questionList,
                        null,
                        result.isFinished(),
                        getGraphTaskResultPath(result)
                );
            }
            case ANSWER -> {
                Question question = result.getCurrQuestion();
                return new QuestionInfoResponse(
                        status,
                        graphTaskResultService.getTimeRemaining(result),
                        pointsCalculator.calculateAllPoints(result),
                        null,
                        new QuestionDetails(question),
                        result.isFinished(),
                        getGraphTaskResultPath(result)
                );
            }
            default ->
                    throw new EntityRequiredAttributeNullException("GraphTask must have status CHOOSE or ANSWER");
        }
    }

    private List<QuestionList> getNextQuestions(Long graphTaskId,
                                                GraphTaskResult result) {
        log.info("Fetching next questions for graph task with id {} and user {}", graphTaskId, result.getMember().getUser().getId());

        Question currQuestion = result.getCurrQuestion();
        List<Question> nextQuestions = currQuestion.getNext();

        return nextQuestions.stream()
                .map(QuestionList::new)
                .toList();
    }

    private List<Long> getGraphTaskResultPath(GraphTaskResult result) {
        Long startQuestionID = result.getGraphTask().getQuestions().get(0).getId();
        List<Long> path = result.getAnswers()
                .stream()
                .map(answer -> answer.getQuestion().getId())
                .toList();
        List<Long> fullPath = new LinkedList<>();
        fullPath.add(startQuestionID);
        fullPath.addAll(path);
        fullPath.add(result.getCurrQuestion().getId());
        return new ArrayList<>(new HashSet<>(fullPath)); // removing duplicates
    }
}
