package com.example.api.unit.service.activity.result;

import com.example.api.activity.result.dto.request.AnswerForm;
import com.example.api.error.exception.*;
import com.example.api.activity.result.model.GraphTaskResult;
import com.example.api.activity.task.model.GraphTask;
import com.example.api.question.model.Question;
import com.example.api.user.model.AccountType;
import com.example.api.user.model.User;
import com.example.api.activity.result.repository.GraphTaskResultRepository;
import com.example.api.activity.task.repository.GraphTaskRepository;
import com.example.api.question.repository.QuestionRepository;
import com.example.api.user.repository.UserRepository;
import com.example.api.security.AuthenticationService;
import com.example.api.activity.result.service.GraphTaskResultService;
import com.example.api.user.service.UserService;
import com.example.api.validator.ResultValidator;
import com.example.api.validator.UserValidator;
import com.example.api.activity.validator.ActivityValidator;
import com.example.api.util.calculator.PointsCalculator;
import com.example.api.util.calculator.TimeCalculator;
import com.example.api.util.visitor.HeroVisitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

public class GraphTaskResultServiceTest {
    private GraphTaskResultService graphTaskResultService;
    @Mock private GraphTaskResultRepository graphTaskResultRepository;
    @Mock private GraphTaskRepository graphTaskRepository;
    @Mock private UserRepository userRepository;
    @Mock private QuestionRepository questionRepository;
    @Mock private ResultValidator answerFormValidator;
    @Mock private PointsCalculator pointsCalculator;
    @Mock private UserValidator userValidator;
    @Mock private TimeCalculator timeCalculator;
    @Mock private AuthenticationService authService;
    @Mock private UserService userService;
    @Mock private ActivityValidator activityValidator;
    @Mock private HeroVisitor heroVisitor;
    @Mock private Authentication authentication;
    GraphTaskResult result;
    GraphTask graphTask;
    @Captor ArgumentCaptor<User> userArgumentCaptor;
    @Captor ArgumentCaptor<GraphTask> graphTaskArgumentCaptor;
    @Captor ArgumentCaptor<GraphTaskResult> resultArgumentCaptor;
    @Captor ArgumentCaptor<Long> idArgumentCaptor;
    @Captor ArgumentCaptor<String> emailArgumentCaptor;
    @Captor ArgumentCaptor<Long> resultIdArgumentCaptor;
    @Captor ArgumentCaptor<AnswerForm> answerFormArgumentCaptor;
    @Captor ArgumentCaptor<Long> questionIdArgumentCaptor;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        graphTaskResultService = new GraphTaskResultService(
                graphTaskResultRepository,
                graphTaskRepository,
                questionRepository,
                pointsCalculator,
                answerFormValidator,
                userValidator,
                timeCalculator,
                authService,
                userService,
                activityValidator,
                heroVisitor
        );
        graphTask = new GraphTask();
        graphTask.setId(2L);
        graphTask.setQuestions(List.of(new Question()));
        result = new GraphTaskResult();
        result.setId(1L);
        result.setGraphTask(graphTask);
    }

    @Test
    public void getGraphTaskResult() throws WrongUserTypeException, EntityNotFoundException {
        // given
        User user = new User();
        user.setEmail("random@email.com");
        user.setAccountType(AccountType.STUDENT);
        given(authService.getAuthentication()).willReturn(authentication);
        given(authentication.getName()).willReturn("random@email.com");
        given(userRepository.findUserByEmail(user.getEmail())).willReturn(user);
        given(graphTaskRepository.findGraphTaskById(graphTask.getId())).willReturn(graphTask);

        // when
        graphTaskResultService.getGraphTaskResultId(graphTask.getId());

        // then
        verify(graphTaskResultRepository).findGraphTaskResultByGraphTaskAndUser(
                graphTaskArgumentCaptor.capture(), userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();
        GraphTask capturedGraphTask = graphTaskArgumentCaptor.getValue();

        assertThat(capturedUser).isEqualTo(user);
        assertThat(capturedGraphTask).isEqualTo(graphTask);
    }

    @Test
    public void saveGraphTaskResult() {
        // given
        GraphTaskResult result = new GraphTaskResult();

        // when
        graphTaskResultService.saveGraphTaskResult(result);

        // then
        verify(graphTaskResultRepository).save(resultArgumentCaptor.capture());
        GraphTaskResult capturedResult = resultArgumentCaptor.getValue();
        assertThat(capturedResult).isEqualTo(result);
    }

    @Test
    public void saveGraphTaskResultForm() throws EntityNotFoundException, EntityAlreadyInDatabaseException, WrongUserTypeException {
        // given
        User user = new User();
        user.setEmail("random@email.com");
        given(authService.getAuthentication()).willReturn(authentication);
        given(authentication.getName()).willReturn("random@email.com");
        given(graphTaskRepository.findGraphTaskById(graphTask.getId())).willReturn(graphTask);
        given(userRepository.findUserByEmail(user.getEmail())).willReturn(user);


        // when
        graphTaskResultService.startGraphTaskResult(graphTask.getId());

        // then
        verify(graphTaskRepository).findGraphTaskById(idArgumentCaptor.capture());
        verify(userRepository).findUserByEmail(emailArgumentCaptor.capture());
        Long capturedId = idArgumentCaptor.getValue();
        String capturedEmail = emailArgumentCaptor.getValue();
        assertThat(capturedId).isEqualTo(graphTask.getId());
        assertThat(capturedEmail).isEqualTo(user.getEmail());
    }

    @Test
    public void getPointsFromClosedQuestions() throws WrongAnswerTypeException, EntityNotFoundException {
        //given
        given(graphTaskResultRepository.findGraphTaskResultById(result.getId())).willReturn(result);

        // when
        graphTaskResultService.getPointsFromClosedQuestions(result.getId());

        // then
        verify(graphTaskResultRepository).findGraphTaskResultById(idArgumentCaptor.capture());
        Long capturedId = idArgumentCaptor.getValue();
        assertThat(capturedId).isEqualTo(result.getId());
    }

    @Test
    public void getPointsFromOpenedQuestions() throws WrongAnswerTypeException, EntityNotFoundException {
        //given
        given(graphTaskResultRepository.findGraphTaskResultById(result.getId())).willReturn(result);

        // when
        graphTaskResultService.getPointsFromOpenedQuestions(result.getId());

        // then
        verify(graphTaskResultRepository).findGraphTaskResultById(idArgumentCaptor.capture());
        Long capturedId = idArgumentCaptor.getValue();
        assertThat(capturedId).isEqualTo(result.getId());
    }

    @Test
    public void getMaxAvailablePoints() throws EntityNotFoundException {
        //given
        given(graphTaskResultRepository.findGraphTaskResultById(result.getId())).willReturn(result);

        // when
        graphTaskResultService.getMaxAvailablePoints(result.getId());

        // then
        verify(graphTaskResultRepository).findGraphTaskResultById(idArgumentCaptor.capture());
        Long capturedId = idArgumentCaptor.getValue();
        assertThat(capturedId).isEqualTo(result.getId());
    }

    @Test
    public void getMaxClosedPoints() throws EntityNotFoundException {
        //given
        given(graphTaskResultRepository.findGraphTaskResultById(result.getId())).willReturn(result);

        // when
        graphTaskResultService.getMaxClosedPoints(result.getId());

        // then
        verify(graphTaskResultRepository).findGraphTaskResultById(idArgumentCaptor.capture());
        Long capturedId = idArgumentCaptor.getValue();
        assertThat(capturedId).isEqualTo(result.getId());
    }

    @Test
    public void getMaxOpenedPoints() throws EntityNotFoundException {
        //given
        given(graphTaskResultRepository.findGraphTaskResultById(result.getId())).willReturn(result);

        // when
        graphTaskResultService.getMaxOpenedPoints(result.getId());

        // then
        verify(graphTaskResultRepository).findGraphTaskResultById(idArgumentCaptor.capture());
        Long capturedId = idArgumentCaptor.getValue();
        assertThat(capturedId).isEqualTo(result.getId());
    }

    @Test
    public void getTimeRemaining() throws EntityNotFoundException, EntityRequiredAttributeNullException {
        // given
        given(graphTaskResultRepository.findGraphTaskResultById(result.getId())).willReturn(result);
        result.setStartDateMillis(System.currentTimeMillis());
        graphTask.setTimeToSolveMillis(1_000_000L);

        //when
        graphTaskResultService.getTimeRemaining(result.getId());

        // then
        verify(graphTaskResultRepository).findGraphTaskResultById(idArgumentCaptor.capture());
        Long capturedId = idArgumentCaptor.getValue();
        assertThat(capturedId).isEqualTo(result.getId());
    }
}
