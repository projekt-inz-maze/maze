package com.example.api.unit.util.visitor;

import com.example.api.activity.result.model.*;
import com.example.api.course.model.Course;
import com.example.api.course.model.CourseMember;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.MissingAttributeException;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.activity.task.filetask.FileTask;
import com.example.api.activity.task.graphtask.GraphTask;
import com.example.api.group.Group;
import com.example.api.security.LoggedInUserService;
import com.example.api.user.badge.types.*;
import com.example.api.user.model.AccountType;
import com.example.api.user.model.User;
import com.example.api.activity.result.service.FileTaskResultService;
import com.example.api.activity.result.service.GraphTaskResultService;
import com.example.api.activity.result.service.TaskResultService;
import com.example.api.activity.result.service.ranking.RankingService;
import com.example.api.user.model.badge.*;
import com.example.api.user.service.UserService;
import com.example.api.user.badge.BadgeVisitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

public class BadgeVisitorTest {
    private BadgeVisitor badgeVisitor;

    @Mock private TaskResultService taskResultService;
    @Mock private GraphTaskResultService graphTaskResultService;
    @Mock private FileTaskResultService fileTaskResultService;
    @Mock private  UserService userService;
    @Mock private RankingService rankingService;
    @Mock private LoggedInUserService authService;

    private User user;
    private List<TaskResult> results;
    private GraphTaskResult graphTaskResult1;
    private GraphTaskResult graphTaskResult2;
    private FileTaskResult fileTaskResult1;
    private FileTaskResult fileTaskResult2;
    private SurveyResult surveyResult;
    private AdditionalPoints additionalPoints;

    private CourseMember member;
    private Course course;

    private Group group;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        badgeVisitor = new BadgeVisitor(
                taskResultService,
                graphTaskResultService,
                fileTaskResultService,
                userService,
                rankingService,
                authService
        );

        user = new User();
        user.setId(1L);
        user.setEmail("user@gmail.com");
        user.setPassword("password");
        user.setAccountType(AccountType.STUDENT);

        member = new CourseMember();
        member.setPoints(0D);
        member.setLevel(1);

        group = new Group();
        group.setId(2L);
        group.getUsers().add(user);
        group.getMembers().add(member);

        course = new Course();
        course.setId(3L);
        course.getGroups().add(group);

        graphTaskResult1 = new GraphTaskResult();
        graphTaskResult2 = new GraphTaskResult();
        fileTaskResult1 = new FileTaskResult();
        fileTaskResult2 = new FileTaskResult();
        surveyResult = new SurveyResult();
        additionalPoints = new AdditionalPoints();

        graphTaskResult1.setMember(member);
        graphTaskResult2.setMember(member);
        fileTaskResult1.setMember(member);
        fileTaskResult2.setMember(member);
        surveyResult.setMember(member);
        additionalPoints.setMember(member);

        graphTaskResult1.setCourse(course);
        graphTaskResult2.setCourse(course);
        fileTaskResult1.setCourse(course);
        fileTaskResult2.setCourse(course);
        surveyResult.setCourse(course);
        additionalPoints.setCourse(course);

        graphTaskResult1.setPointsReceived(100d);
        graphTaskResult2.setPointsReceived(20d);
        fileTaskResult1.setPointsReceived(80d);
        fileTaskResult2.setPointsReceived(30d);
        surveyResult.setPointsReceived(5d);
        additionalPoints.setPointsReceived(10d);
        surveyResult.setSendDateMillis(System.currentTimeMillis());

        results = List.of(
                graphTaskResult1,
                graphTaskResult2,
                fileTaskResult1,
                fileTaskResult2,
                surveyResult,
                additionalPoints
        );
    }

    @Test
    public void visitActivityNumberBadgeGranted() {
        //given
        when(authService.getCurrentUser()).thenReturn(user);
        doReturn(results).when(taskResultService).getAllResultsForStudent(user, course);
        ActivityNumberBadge activityNumberBadge = new ActivityNumberBadge(6);
        activityNumberBadge.setCourse(course);

        //when
        boolean isGranted = badgeVisitor.visitActivityNumberBadge(activityNumberBadge);

        //then
        assertThat(isGranted).isEqualTo(true);
    }

    @Test
    public void visitActivityNumberBadgeNotGranted() {
        //given
        when(authService.getCurrentUser()).thenReturn(user);
        doReturn(results).when(taskResultService).getAllResultsForStudent(user, course);
        ActivityNumberBadge activityNumberBadge = new ActivityNumberBadge(7);
        activityNumberBadge.setCourse(course);

        //when
        boolean isGranted = badgeVisitor.visitActivityNumberBadge(activityNumberBadge);

        //then
        assertThat(isGranted).isEqualTo(false);
    }

    @Test
    public void visitActivityScoreBadgeGranted() {
        //given
        when(authService.getCurrentUser()).thenReturn(user);
        results = new ArrayList<>(results);
        results.remove(surveyResult);
        results.remove(additionalPoints);

        GraphTask graphTask1 = new GraphTask();
        graphTask1.setMaxPoints(100d);
        GraphTask graphTask2 = new GraphTask();
        graphTask2.setMaxPoints(40d);
        FileTask fileTask1 = new FileTask();
        fileTask1.setMaxPoints(100d);
        FileTask fileTask2 = new FileTask();
        fileTask2.setMaxPoints(47.5);
        graphTaskResult1.setGraphTask(graphTask1);
        graphTaskResult2.setGraphTask(graphTask2);
        fileTaskResult1.setFileTask(fileTask1);
        fileTaskResult2.setFileTask(fileTask2);

        doReturn(results).when(taskResultService).getGraphAndFileResultsForStudent(user, course);
        ActivityScoreBadge activityScoreBadge = new ActivityScoreBadge(0.8, false);
        activityScoreBadge.setCourse(course);

        //when
        boolean isGranted = badgeVisitor.visitActivityScoreBadge(activityScoreBadge);

        //then
        assertThat(isGranted).isEqualTo(true);
    }

    @Test
    public void visitActivityScoreBadgeNotGranted() {
        //given
        when(authService.getCurrentUser()).thenReturn(user);
        results = new ArrayList<>(results);
        results.remove(surveyResult);
        results.remove(additionalPoints);

        GraphTask graphTask1 = new GraphTask();
        graphTask1.setMaxPoints(100d);
        GraphTask graphTask2 = new GraphTask();
        graphTask2.setMaxPoints(40d);
        FileTask fileTask1 = new FileTask();
        fileTask1.setMaxPoints(100d);
        FileTask fileTask2 = new FileTask();
        fileTask2.setMaxPoints(50d);
        graphTaskResult1.setGraphTask(graphTask1);
        graphTaskResult2.setGraphTask(graphTask2);
        fileTaskResult1.setFileTask(fileTask1);
        fileTaskResult2.setFileTask(fileTask2);

        doReturn(results).when(taskResultService).getGraphAndFileResultsForStudent(user, course);
        ActivityScoreBadge activityScoreBadge = new ActivityScoreBadge(0.8, false);
        activityScoreBadge.setCourse(course);

        //when
        boolean isGranted = badgeVisitor.visitActivityScoreBadge(activityScoreBadge);

        //then
        assertThat(isGranted).isEqualTo(false);
    }

    @Test
    public void visitConsistencyBadgeGranted() {
        //given
        when(authService.getCurrentUser()).thenReturn(user);

        Calendar calendar = Calendar.getInstance();
        calendar.set(2022, Calendar.OCTOBER, 1, 0, 0);
        graphTaskResult1.setSendDateMillis(calendar.getTimeInMillis());
        calendar.set(2022, Calendar.OCTOBER, 7, 23, 59);
        graphTaskResult2.setSendDateMillis(calendar.getTimeInMillis());
        calendar.set(2022, Calendar.OCTOBER, 13, 12, 59);
        fileTaskResult1.setSendDateMillis(calendar.getTimeInMillis());
        calendar.set(2022, Calendar.OCTOBER, 19, 1, 1);
        fileTaskResult2.setSendDateMillis(calendar.getTimeInMillis());
        calendar.set(2022, Calendar.OCTOBER, 25, 0,0);
        surveyResult.setSendDateMillis(calendar.getTimeInMillis());
        calendar.set(2022, Calendar.OCTOBER, 31, 0, 0);
        additionalPoints.setSendDateMillis(calendar.getTimeInMillis());

        doReturn(results).when(taskResultService).getAllResultsForStudent(user, course);
        ConsistencyBadge consistencyBadge = new ConsistencyBadge(6);
        consistencyBadge.setCourse(course);

        //when
        boolean isGranted = badgeVisitor.visitConsistencyBadge(consistencyBadge);

        //then
        assertThat(isGranted).isEqualTo(true);
    }

    @Test
    public void visitConsistencyBadgeNorGranted() {
        //given
        when(authService.getCurrentUser()).thenReturn(user);

        Calendar calendar = Calendar.getInstance();
        calendar.set(2022, Calendar.OCTOBER, 1, 0, 0);
        graphTaskResult1.setSendDateMillis(calendar.getTimeInMillis());
        calendar.set(2022, Calendar.OCTOBER, 7, 23, 59);
        graphTaskResult2.setSendDateMillis(calendar.getTimeInMillis());
        calendar.set(2022, Calendar.OCTOBER, 13, 12, 59);
        fileTaskResult1.setSendDateMillis(calendar.getTimeInMillis());
        calendar.set(2022, Calendar.OCTOBER, 20, 13, 0);
        fileTaskResult2.setSendDateMillis(calendar.getTimeInMillis());
        calendar.set(2022, Calendar.OCTOBER, 25, 0,0);
        surveyResult.setSendDateMillis(calendar.getTimeInMillis());
        calendar.set(2022, Calendar.OCTOBER, 31, 0, 0);
        additionalPoints.setSendDateMillis(calendar.getTimeInMillis());

        doReturn(results).when(taskResultService).getAllResultsForStudent(user, course);
        ConsistencyBadge consistencyBadge = new ConsistencyBadge(6);
        consistencyBadge.setCourse(course);

        //when
        boolean isGranted = badgeVisitor.visitConsistencyBadge(consistencyBadge);

        //then
        assertThat(isGranted).isEqualTo(false);
    }

    @Test
    public void visitGraphTaskNumberBadgeGranted() {
        //given
        when(authService.getCurrentUser()).thenReturn(user);

        results = List.of(graphTaskResult1, graphTaskResult2);

        doReturn(results).when(graphTaskResultService).getAllGraphTaskResultsForStudentAndCourse(user, course);
        GraphTaskNumberBadge graphTaskNumberBadge = new GraphTaskNumberBadge(2);
        graphTaskNumberBadge.setCourse(course);

        //when
        boolean isGranted = badgeVisitor.visitGraphTaskNumberBadge(graphTaskNumberBadge);

        //then
        assertThat(isGranted).isEqualTo(true);
    }

    @Test
    public void visitGraphTaskNumberBadgeNotGranted() {
        //given
        when(authService.getCurrentUser()).thenReturn(user);

        results = List.of(graphTaskResult1, graphTaskResult2);

        doReturn(results).when(graphTaskResultService).getAllGraphTaskResultsForStudentAndCourse(user, course);
        GraphTaskNumberBadge graphTaskNumberBadge = new GraphTaskNumberBadge(3);
        graphTaskNumberBadge.setCourse(course);

        //when
        boolean isGranted = badgeVisitor.visitGraphTaskNumberBadge(graphTaskNumberBadge);

        //then
        assertThat(isGranted).isEqualTo(false);
    }

    @Test
    public void visitFileTaskNumberBadgeGranted() {
        //given
        when(authService.getCurrentUser()).thenReturn(user);

        results = List.of(fileTaskResult1, fileTaskResult2);

        doReturn(results).when(fileTaskResultService).getAllFileTaskResultsForStudent(user, course);
        FileTaskNumberBadge fileTaskNumberBadge = new FileTaskNumberBadge(2);
        fileTaskNumberBadge.setCourse(course);

        //when
        boolean isGranted = badgeVisitor.visitFileTaskNumberBadge(fileTaskNumberBadge);

        //then
        assertThat(isGranted).isEqualTo(true);
    }

    @Test
    public void visitFileTaskNumberBadgeNotGranted() {
        //given
        when(authService.getCurrentUser()).thenReturn(user);

        results = List.of(fileTaskResult1, fileTaskResult2);

        doReturn(results).when(fileTaskResultService).getAllFileTaskResultsForStudent(user, course);
        FileTaskNumberBadge fileTaskNumberBadge = new FileTaskNumberBadge(4);
        fileTaskNumberBadge.setCourse(course);

        //when
        boolean isGranted = badgeVisitor.visitFileTaskNumberBadge(fileTaskNumberBadge);

        //then
        assertThat(isGranted).isEqualTo(false);
    }

    @Test
    public void visitTopScoreTaskNumberBadgeResultsLessThanFive() throws WrongUserTypeException, EntityNotFoundException, MissingAttributeException {
        //given
        when(authService.getCurrentUser()).thenReturn(user);
        results = new ArrayList<>(results);
        results.remove(surveyResult);
        results.remove(additionalPoints);

        doReturn(results).when(taskResultService).getGraphAndFileResultsForStudent(user, course);
        TopScoreBadge topScoreBadge = new TopScoreBadge(0.2, false);
        topScoreBadge.setCourse(course);

        //when
        boolean isGranted = badgeVisitor.visitTopScoreBadge(topScoreBadge);

        //then
        assertThat(isGranted).isEqualTo(false);
    }

    @Test
    public void visitTopScoreTaskNumberBadgeResultsGroupIsGranted() throws WrongUserTypeException, EntityNotFoundException, MissingAttributeException {
        //given
        when(authService.getCurrentUser()).thenReturn(user);
        results = new ArrayList<>(results);
        results.remove(surveyResult);
        results.remove(additionalPoints);
        GraphTaskResult graphTaskResult = new GraphTaskResult();
        graphTaskResult.setMember(member);
        graphTaskResult.setPointsReceived(20d);
        results.add(graphTaskResult);

        Group group = new Group();
        group.setUsers(List.of(new User(), new User(), new User(), new User(), new User()));
        group.getUsers().forEach(user1 -> user1.setAccountType(AccountType.STUDENT));

        doReturn(results).when(taskResultService).getGraphAndFileResultsForStudent(user, course);
        when(userService.getCurrentUserGroup(0L)).thenReturn(group);
        when(rankingService.getGroupRankingPosition(0l)).thenReturn(2);

        TopScoreBadge topScoreBadge = new TopScoreBadge(0.5, true);
        topScoreBadge.setCourse(course);

        //when
        boolean isGranted = badgeVisitor.visitTopScoreBadge(topScoreBadge);

        //then
        assertThat(isGranted).isEqualTo(true);
    }

    @Test
    public void visitTopScoreTaskNumberBadgeResultsGroupIsGrantedFirstPlace() throws WrongUserTypeException, EntityNotFoundException, MissingAttributeException {
        //given
        when(authService.getCurrentUser()).thenReturn(user);
        results = new ArrayList<>(results);
        results.remove(surveyResult);
        results.remove(additionalPoints);
        GraphTaskResult graphTaskResult = new GraphTaskResult();
        graphTaskResult.setMember(member);
        graphTaskResult.setPointsReceived(20d);
        results.add(graphTaskResult);

        Group group = new Group();
        group.setUsers(List.of(new User(), new User(), new User(), new User(), new User()));
        group.getUsers().forEach(user1 -> user1.setAccountType(AccountType.STUDENT));

        doReturn(results).when(taskResultService).getGraphAndFileResultsForStudent(user, course);
        when(userService.getCurrentUserGroup(0L)).thenReturn(group);
        when(rankingService.getGroupRankingPosition(0L)).thenReturn(1);

        TopScoreBadge topScoreBadge = new TopScoreBadge(0.0, true);
        topScoreBadge.setCourse(course);

        //when
        boolean isGranted = badgeVisitor.visitTopScoreBadge(topScoreBadge);

        //then
        assertThat(isGranted).isEqualTo(true);
    }

    @Test
    public void visitTopScoreTaskNumberBadgeResultsIsGranted() throws WrongUserTypeException, EntityNotFoundException, MissingAttributeException {
        //given
        when(authService.getCurrentUser()).thenReturn(user);
        results = new ArrayList<>(results);
        results.remove(surveyResult);
        results.remove(additionalPoints);
        GraphTaskResult graphTaskResult = new GraphTaskResult();
        graphTaskResult.setMember(member);
        graphTaskResult.setPointsReceived(20d);
        graphTaskResult.setCourse(course);
        results.add(graphTaskResult);

        List<User> users = List.of(new User(), new User(), new User(), new User(), new User());
        users.forEach(user1 -> user1.setAccountType(AccountType.STUDENT));

        doReturn(results).when(taskResultService).getGraphAndFileResultsForStudent(user, course);
        when(course.getCourseMembers().size()).thenReturn(users.size());
        when(rankingService.getRankingPosition(0L)).thenReturn(2);

        TopScoreBadge topScoreBadge = new TopScoreBadge(0.5, false);
        topScoreBadge.setCourse(course);

        //when
        boolean isGranted = badgeVisitor.visitTopScoreBadge(topScoreBadge);

        //then
        assertThat(isGranted).isEqualTo(true);
    }

    @Test
    public void visitTopScoreTaskNumberBadgeResultsIsGrantedFirstPlace() throws WrongUserTypeException, EntityNotFoundException, MissingAttributeException {
        //given
        when(authService.getCurrentUser()).thenReturn(user);
        results = new ArrayList<>(results);
        results.remove(surveyResult);
        results.remove(additionalPoints);
        GraphTaskResult graphTaskResult = new GraphTaskResult();
        graphTaskResult.setMember(member);
        graphTaskResult.setPointsReceived(20d);
        results.add(graphTaskResult);

        Group group = new Group();
        group.setId(2L);
        group.getUsers().add(user);
        group.getMembers().add(member);

        Course course = new Course();
        course.setId(3L);
        course.getGroups().add(group);

        List<User> users = List.of(new User(), new User(), new User(), new User(), new User());
        users.forEach(user1 -> user1.setAccountType(AccountType.STUDENT));

        group.getUsers().addAll(users);

        doReturn(results).when(taskResultService).getGraphAndFileResultsForStudent(user, course);
        when(userService.getCurrentUserGroup(course.getId())).thenReturn(group);
        when(rankingService.getRankingPosition(0L)).thenReturn(1);

        TopScoreBadge topScoreBadge = new TopScoreBadge(0.0, false);
        topScoreBadge.setCourse(course);

        //when
        boolean isGranted = badgeVisitor.visitTopScoreBadge(topScoreBadge);

        //then
        assertThat(isGranted).isEqualTo(true);
    }
}
