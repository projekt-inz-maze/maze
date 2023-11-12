package com.example.api.user.badge;

import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.activity.result.model.FileTaskResult;
import com.example.api.activity.result.model.GraphTaskResult;
import com.example.api.activity.result.model.TaskResult;
import com.example.api.activity.Activity;
import com.example.api.security.LoggedInUserService;
import com.example.api.user.badge.types.*;
import com.example.api.user.model.AccountType;
import com.example.api.user.model.User;
import com.example.api.activity.result.service.FileTaskResultService;
import com.example.api.activity.result.service.GraphTaskResultService;
import com.example.api.activity.result.service.TaskResultService;
import com.example.api.activity.result.service.ranking.RankingService;
import com.example.api.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class BadgeVisitor {
    private final TaskResultService taskResultService;
    private final GraphTaskResultService graphTaskResultService;
    private final FileTaskResultService fileTaskResultService;
    private final UserService userService;
    private final RankingService rankingService;
    private final LoggedInUserService authService;

    public boolean visitActivityNumberBadge(ActivityNumberBadge badge) {
        User student = authService.getCurrentUser();
        List<? extends TaskResult> results = taskResultService.getAllResultsForStudent(student, badge.getCourse())
                .stream()
                .filter(TaskResult::isEvaluated)
                .toList();
        int activityNumber = results.size();
        return activityNumber >= badge.getActivityNumber();
    }

    public boolean visitActivityScoreBadge(ActivityScoreBadge badge) {
        User student = authService.getCurrentUser();
        List<? extends TaskResult> results = taskResultService.getGraphAndFileResultsForStudent(student, badge.getCourse())
                .stream()
                .filter(TaskResult::isEvaluated)
                .toList();

        Boolean forOneActivity = badge.getForOneActivity();
        if (forOneActivity!= null && forOneActivity) {
            BigDecimal activityScore = BigDecimal.valueOf(badge.getActivityScore());
            return results.stream().anyMatch(result -> {
                Activity activity = result.getActivity();
                BigDecimal maxPoints = BigDecimal.valueOf(activity.getMaxPoints());
                BigDecimal resultPoints = BigDecimal.valueOf(result.getPoints());
                BigDecimal score = resultPoints.divide(maxPoints, 2, RoundingMode.HALF_UP);
                return score.compareTo(activityScore) >= 0;
            });
        }

        if (results.size() < 3) {
            return false;
        }
        List<Activity> activities = results.stream()
                .map(TaskResult::getActivity)
                .toList();

        BigDecimal currentPoints = BigDecimal.valueOf(results.stream()
                .mapToDouble(TaskResult::getPoints)
                .sum());
        BigDecimal maxPoints = BigDecimal.valueOf(activities.stream()
                .mapToDouble(Activity::getMaxPoints)
                .sum());

        if (maxPoints.compareTo(BigDecimal.valueOf(0)) == 0) {
            return badge.getActivityScore() == 0.0;
        }
        BigDecimal score = currentPoints.divide(maxPoints, 2, RoundingMode.HALF_UP);
        return score.compareTo(BigDecimal.valueOf(badge.getActivityScore())) >= 0;
    }

    public boolean visitConsistencyBadge(ConsistencyBadge badge) {
        User student = authService.getCurrentUser();
        List<? extends TaskResult> results = taskResultService.getAllResultsForStudent(student, badge.getCourse());
        Long[] datesInMillis = results.stream()
                .filter(TaskResult::isEvaluated)
                .map(TaskResult::getSendDateMillis)
                .sorted()
                .toArray(Long[]::new);

        int weeksInRow = badge.getWeeksInRow();
        int counter = 1;
        for (int i=0; i < datesInMillis.length-1; i++) {
            long diff = Math.abs(datesInMillis[i] - datesInMillis[i + 1]);
            long daysDiff = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

            if (daysDiff < 7) {
                counter++;
            } else {
                counter = 0;
            }

            if (counter >= weeksInRow) {
                return true;
            }
        }
        return false;
    }

    public boolean visitFileTaskNumberBadge(FileTaskNumberBadge badge) {
        User student = authService.getCurrentUser();
        List<FileTaskResult> results = fileTaskResultService.getAllFileTaskResultsForStudent(student, badge.getCourse())
                .stream()
                .filter(FileTaskResult::isEvaluated)
                .toList();
        int fileTaskNumber = results.size();
        return fileTaskNumber >= badge.getFileTaskNumber();
    }

    public boolean visitGraphTaskNumberBadge(GraphTaskNumberBadge badge) {
        User student = authService.getCurrentUser();
        List<GraphTaskResult> results = graphTaskResultService.getAllGraphTaskResultsForStudentAndCourse(student, badge.getCourse())
                .stream()
                .filter(GraphTaskResult::isEvaluated)
                .toList();
        int graphTaskNumber = results.size();
        return graphTaskNumber >= badge.getGraphTaskNumber();
    }

    public boolean visitTopScoreBadge(TopScoreBadge badge) throws WrongUserTypeException, EntityNotFoundException {
        User student = authService.getCurrentUser();
        List<? extends TaskResult> results = taskResultService.getGraphAndFileResultsForStudent(student, badge.getCourse())
                .stream()
                .filter(TaskResult::isEvaluated)
                .toList();

        if (results.size() < 5) {
            return false;
        }

        Boolean forGroup = badge.getForGroup();
        Long courseId = badge.getCourse().getId();

        if (forGroup != null && forGroup) {
            BigDecimal rankingInGroupPosition = BigDecimal.valueOf(rankingService.getGroupRankingPosition(courseId));

            if (badge.getTopScore() == 0) {
                return rankingInGroupPosition.equals(BigDecimal.ONE);
            }
            BigDecimal numStudentsInGroup = BigDecimal.valueOf(userService.getCurrentUserGroup(courseId)
                    .getUsers()
                    .stream()
                    .filter(user -> user.getAccountType() == AccountType.STUDENT)
                    .count());

            BigDecimal topScore = rankingInGroupPosition.divide(numStudentsInGroup, 2, RoundingMode.HALF_UP);
            return topScore.compareTo(BigDecimal.valueOf(badge.getTopScore())) <= 0;
        } else {
            BigDecimal rankingPosition = BigDecimal.valueOf(rankingService.getRankingPosition(courseId));

            if (badge.getTopScore() == 0) {
                return rankingPosition.equals(BigDecimal.ONE);
            }

            BigDecimal numOfStudents = BigDecimal.valueOf(badge.getCourse().getCourseMembers().size());

            BigDecimal topScore = rankingPosition.divide(numOfStudents, 2, RoundingMode.HALF_UP);
            return topScore.compareTo(BigDecimal.valueOf(badge.getTopScore())) <= 0;
        }
    }
}

