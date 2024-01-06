package com.example.api.unit.util.visitor;

import com.example.api.activity.result.dto.response.SuperPowerResponse;
import com.example.api.course.coursemember.CourseMember;
import com.example.api.activity.ActivityType;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.activity.result.model.GraphTaskResult;
import com.example.api.activity.result.model.ResultStatus;
import com.example.api.activity.task.graphtask.GraphTask;
import com.example.api.question.Difficulty;
import com.example.api.question.Question;
import com.example.api.question.QuestionType;
import com.example.api.user.hero.model.Priest;
import com.example.api.user.hero.model.Rogue;
import com.example.api.user.hero.model.UserHero;
import com.example.api.user.model.AccountType;
import com.example.api.user.hero.HeroType;
import com.example.api.user.model.User;
import com.example.api.util.calculator.TimeCalculator;
import com.example.api.user.hero.HeroVisitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

public class HeroVisitorTest {
    private HeroVisitor heroVisitor;
    @Mock private TimeCalculator timeCalculator;
    private GraphTaskResult result;

    private CourseMember courseMember;
    private Long currTime;
    private Question firstQuestion;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        heroVisitor = new HeroVisitor(timeCalculator);
        User user = new User("email", "Name", "LastName", AccountType.STUDENT);

        firstQuestion = new Question();
        Question question1 = new Question(QuestionType.MULTIPLE_CHOICE, "", "", Difficulty.EASY, List.of(), 10.0, new LinkedList<>(), null);
        Question question2 = new Question(QuestionType.SINGLE_CHOICE, "", "", Difficulty.MEDIUM, List.of(), 20.0, new LinkedList<>(), null);
        Question question3 = new Question(QuestionType.OPENED, "", "", Difficulty.HARD, null, 30.0, new LinkedList<>(), "");
        Question question4 = new Question(QuestionType.OPENED, "", "", Difficulty.MEDIUM, null, 20.0, new LinkedList<>(), "");
        Question question5 = new Question(QuestionType.OPENED, "", "", Difficulty.HARD, null, 30.0, new LinkedList<>(), "");
        firstQuestion.getNext().addAll(List.of(question1, question2, question3));
        question1.getNext().addAll(List.of(question2, question4));
        question3.getNext().add(question5);

        GraphTask graphTask = new GraphTask(ActivityType.EXPEDITION,
                List.of(firstQuestion, question1, question2, question3, question4, question5),
                TimeUnit.MINUTES.toMillis(10)
        );
        currTime = System.currentTimeMillis();
        courseMember = new CourseMember();
        courseMember.setLevel(1);
        result = new GraphTaskResult(graphTask, currTime, ResultStatus.CHOOSE, firstQuestion, courseMember);
        result.setFinished(false);
    }

    @Test
    public void shouldUsePriestSuperPowerNormally() throws RequestValidationException {
        //given
        courseMember.setLevel(1);
        Priest priest = new Priest(HeroType.PRIEST, TimeUnit.DAYS.toMillis(10), null);
        courseMember.setUserHero(new UserHero(priest, 0, 0L, null));
        long startDateMillis = result.getStartDateMillis();
        long healValue = priest.getHealValueForLevel(courseMember.getLevel());
        long timeToSolve = result.getGraphTask().getTimeToSolveMillis();
        long newTimeRemaining = TimeUnit.MINUTES.toMillis(10) + healValue;
        when(timeCalculator.getTimeRemaining(startDateMillis+healValue, timeToSolve)).thenReturn(newTimeRemaining);

        //when
        SuperPowerResponse<Long> response = heroVisitor.visitPriest(priest, result);
        Long time = response.getValue();

        //then
        assertThat(time).isEqualTo(newTimeRemaining);
        assertThat(result.getStartDateMillis()).isEqualTo(startDateMillis + healValue);
    }

    @Test
    public void shouldUsePriestSuperPowerNormallyWithHigherUserLevel() throws RequestValidationException {
        //given
        Priest priest = new Priest(HeroType.PRIEST, TimeUnit.DAYS.toMillis(10), null);
        courseMember.setUserHero(new UserHero(priest, 0, 0L, null));
        courseMember.setLevel(3);
        long startDateMillis = result.getStartDateMillis();
        long healValue = priest.getHealValueForLevel(courseMember.getLevel());
        long timeToSolve = result.getGraphTask().getTimeToSolveMillis();
        long newTimeRemaining = TimeUnit.MINUTES.toMillis(10) + healValue;
        when(timeCalculator.getTimeRemaining(startDateMillis + healValue, timeToSolve)).thenReturn(newTimeRemaining);

        //when
        SuperPowerResponse<Long> response = heroVisitor.visitPriest(priest, result);
        Long time = response.getValue();

        //then
        assertThat(time).isEqualTo(newTimeRemaining);
        assertThat(result.getStartDateMillis()).isEqualTo(startDateMillis + healValue);
    }

    @Test
    public void shouldThrowRequestValidationExceptionBecauseResultIsFinished() {
        //given
        Priest priest = new Priest(HeroType.PRIEST, TimeUnit.DAYS.toMillis(10), null);
        //user.setUserHero(new UserHero(priest, 0, 0L, null));
        result.setFinished(true);

        //when
        //then
        assertThatThrownBy(() -> heroVisitor.visitPriest(priest, result))
                .isInstanceOf(RequestValidationException.class)
                .hasMessageContaining("You cannot use hero power now!");
    }

    @Test
    public void shouldThrowRequestValidationExceptionBecauseCoolDownIsActive() {
        //given
        Priest priest = new Priest(HeroType.PRIEST, TimeUnit.DAYS.toMillis(10), null);
        courseMember.setUserHero(new UserHero(priest, 0, currTime - TimeUnit.DAYS.toMillis(9), null));

        //then
        assertThatThrownBy(() -> heroVisitor.visitPriest(priest, result))
                .isInstanceOf(RequestValidationException.class)
                .hasMessageContaining("You cannot use hero power now!");
    }

    @Test
    public void shouldUseRogueSuperPowerNormally() throws RequestValidationException {
        //given
        Rogue rogue = new Rogue(HeroType.ROGUE, TimeUnit.DAYS.toMillis(10), null);
        courseMember.setUserHero(new UserHero(rogue, 0, 0L, null));
        courseMember.setLevel(11);
        result.setCurrQuestion(firstQuestion.getNext().get(0));
        result.setStatus(ResultStatus.ANSWER);


        //when
        SuperPowerResponse<Boolean> response = heroVisitor.visitRogue(rogue, result);
        Boolean isDone = response.getValue();

        //then
        assertThat(isDone).isEqualTo(true);
        assertThat(result.getStatus()).isEqualTo(ResultStatus.CHOOSE);
    }

    @Test
    public void shouldThrowRequestValidationExceptionBecauseLevelIsTooLowRogue() {
        //given
        Rogue rogue = new Rogue(HeroType.ROGUE, TimeUnit.DAYS.toMillis(10), null);
        //user.setUserHero(new UserHero(rogue, 0, 0L, null));
        result.setCurrQuestion(firstQuestion.getNext().get(0));
        result.setStatus(ResultStatus.ANSWER);

        //when
        //then
        assertThatThrownBy(() -> heroVisitor.visitRogue(rogue, result))
                .isInstanceOf(RequestValidationException.class)
                .hasMessageContaining("You cannot use hero power now!");
    }

    @Test
    public void shouldThrowRequestValidationExceptionBecauseStatusIsIncorrect() {
        //given
        Rogue rogue = new Rogue(HeroType.ROGUE, TimeUnit.DAYS.toMillis(10), null);
        courseMember.setUserHero(new UserHero(rogue, 0, 0L, null));
        Question question = firstQuestion.getNext().get(0);
        courseMember.setLevel((int) Math.round(rogue.getMultiplier() * question.getPoints()) + 1);
        result.setCurrQuestion(firstQuestion.getNext().get(0));
        result.setStatus(ResultStatus.CHOOSE);

        //then
        assertThatThrownBy(() -> heroVisitor.visitRogue(rogue, result))
                .isInstanceOf(RequestValidationException.class)
                .hasMessageContaining("You cannot use hero power now!");
    }

//    @Test
//    public void shouldUseWarriorSuperPowerNormally() throws RequestValidationException {
//        //given
//        Warrior warrior = new Warrior(HeroType.WARRIOR, TimeUnit.DAYS.toMillis(10));
//        user.setUserHero(new UserHero(warrior, 2, 0L, null));
//        Question question = firstQuestion.getNext().get(0);
//        user.setLevel((int) Math.round(user.getUserHero().getTimesSuperPowerUsedInResult() / warrior.getMultiplier()) + 1);
//
//
//        //when
//        SuperPowerResponse<QuestionType> response = heroVisitor.visitWarrior(warrior, result, question);
//        QuestionType type = response.getValue();
//
//        //then
//        assertThat(type).isEqualTo(QuestionType.MULTIPLE_CHOICE);
//    }

//    @Test
//    public void shouldThrowRequestValidationExceptionBecauseLevelIsTooLowWarrior() {
//        //given
//        Warrior warrior = new Warrior(HeroType.WARRIOR, TimeUnit.DAYS.toMillis(10));
//        user.setUserHero(new UserHero(warrior, 1, 0L, null));
//        Question question = firstQuestion.getNext().get(0);
//        user.setLevel((int) Math.round(user.getUserHero().getTimesSuperPowerUsedInResult() / warrior.getMultiplier()) - 1);
//
//
//        //when
//        //then
//        assertThatThrownBy(() -> heroVisitor.visitWarrior(warrior, result, question))
//                .isInstanceOf(RequestValidationException.class)
//                .hasMessageContaining("You cannot use hero power now!");
//    }

//    @Test
//    public void shouldUseWizardSuperPowerNormally() throws RequestValidationException {
//        //given
//        Wizard wizard = new Wizard(HeroType.WIZARD, TimeUnit.DAYS.toMillis(10));
//        user.setUserHero(new UserHero(wizard, 0, 0L, null));
//        Question question = firstQuestion.getNext().get(0);
//        user.setLevel((int) Math.round(user.getUserHero().getTimesSuperPowerUsedInResult() / wizard.getMultiplier()) + 1);
//
//
//        //when
//        SuperPowerResponse<Double> response = heroVisitor.visitWizard(wizard, result, question);
//        Double points = response.getValue();
//
//        //then
//        assertThat(points).isEqualTo(10.0);
//    }

//    @Test
//    public void shouldThrowRequestValidationExceptionBecauseCoolDownIsActiveWizard() {
//        //given
//        Wizard wizard = new Wizard(HeroType.WIZARD, TimeUnit.DAYS.toMillis(10));
//        user.setUserHero(new UserHero(wizard, 0, currTime - TimeUnit.DAYS.toMillis(9), null));
//        Question question = firstQuestion.getNext().get(0);
//        user.setLevel((int) Math.round(user.getUserHero().getTimesSuperPowerUsedInResult() / wizard.getMultiplier()) + 1);
//
//
//        //when
//        //then
//        assertThatThrownBy(() -> heroVisitor.visitWizard(wizard, result, question))
//                .isInstanceOf(RequestValidationException.class)
//                .hasMessageContaining("You cannot use hero power now!");
//    }
}
