package com.example.api.user.service;

import com.example.api.activity.result.service.ranking.RankingService;
import com.example.api.activity.task.model.*;
import com.example.api.activity.task.service.FileTaskService;
import com.example.api.activity.task.service.GraphTaskService;
import com.example.api.activity.task.service.InfoService;
import com.example.api.activity.task.service.SurveyService;
import com.example.api.course.model.Course;
import com.example.api.course.service.CourseService;
import com.example.api.course.validator.CourseValidator;
import com.example.api.map.dto.response.task.ActivityType;
import com.example.api.activity.result.dto.response.RankingResponse;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.MissingAttributeException;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.activity.result.model.FileTaskResult;
import com.example.api.activity.result.model.GraphTaskResult;
import com.example.api.activity.result.model.SurveyResult;
import com.example.api.activity.result.model.TaskResult;
import com.example.api.map.model.Chapter;
import com.example.api.map.model.requirement.Requirement;
import com.example.api.user.model.Rank;
import com.example.api.user.model.User;
import com.example.api.activity.result.repository.AdditionalPointsRepository;
import com.example.api.activity.result.repository.FileTaskResultRepository;
import com.example.api.activity.result.repository.GraphTaskResultRepository;
import com.example.api.activity.result.repository.SurveyResultRepository;
import com.example.api.user.repository.UserRepository;
import com.example.api.security.AuthenticationService;
import com.example.api.map.service.ChapterService;
import com.example.api.validator.UserValidator;
import com.example.api.user.dto.response.dashboard.*;
import com.example.api.util.calculator.PointsCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DashboardService {
    private final UserRepository userRepository;
    private final AuthenticationService authService;
    private final UserValidator userValidator;
    private final RankingService rankingService;
    private final GraphTaskResultRepository graphTaskResultRepository;
    private final FileTaskResultRepository fileTaskResultRepository;
    private final SurveyResultRepository surveyResultRepository;
    private final AdditionalPointsRepository additionalPointsRepository;
    private final GraphTaskService graphTaskService;
    private final FileTaskService fileTaskService;
    private final SurveyService surveyService;
    private final InfoService infoService;
    private final ChapterService chapterService;
    private final RankService rankService;
    private final BadgeService badgeService;
    private final UserService userService;
    private final CourseService courseService;
    private final CourseValidator courseValidator;

    private final long MAX_LAST_ACTIVITIES_IN_DASHBOARD = 8;

    public DashboardResponse getStudentDashboard(Long courseId) throws WrongUserTypeException, EntityNotFoundException, MissingAttributeException {
        User student = userService.getCurrentUserAndValidateStudentAccount();
        Course course = courseService.getCourse(courseId);
        courseValidator.validateUserCanAccess(student, courseId);
        badgeService.checkAllBadges();

        return new DashboardResponse(
                getHeroTypeStats(student, course),
                getGeneralStats(student, course),
                getLastAddedActivities(course),
                getHeroStats(student, course)
        );
    }

    private HeroTypeStats getHeroTypeStats(User student, Course course) throws EntityNotFoundException {
        String heroType = String.valueOf(student.getHeroType());

        List<RankingResponse> ranking = rankingService.getRanking(course);
        RankingResponse rank = getRank(student, ranking);
        if (rank == null) {
            log.error("Student {} not found in ranking", student.getEmail());
            throw new EntityNotFoundException("Student " + student.getEmail() + " not found in ranking");
        }
        Integer rankPosition = rank.getPosition();
        Long rankLength = (long) ranking.size();
        Double betterPlayerPoints = rankPosition > 1 ? ranking.get(rankPosition - 2).getPoints() : null;
        Double worsePlayerPoints = rankPosition < rankLength ? ranking.get(rankPosition).getPoints() : null;

        return new HeroTypeStats(heroType, rankPosition, rankLength, betterPlayerPoints, worsePlayerPoints);
    }

    private RankingResponse getRank(User student, List<RankingResponse> ranking) {
        return ranking
                .stream()
                .filter(rankingResponse -> rankingResponse.getEmail().equals(student.getEmail()))
                .findAny()
                .orElse(null);
    }

    private GeneralStats getGeneralStats(User student, Course course) {
        Double avgGraphTask = getAvgGraphTask(student, course);
        Double avgFileTask = getAvgFileTask(student, course);
        Long surveysNumber = getSurveysNumber(student, course);
        Double graphTaskPoints = getGraphTaskPoints(student, course);
        Double fileTaskPoints = getFileTaskPoints(student, course);
        Double additionalPoints =  getAdditionalPoints(student, course);
        Double surveyPoints = getSurveyPoints(student, course);
        Double allPoints = graphTaskPoints + fileTaskPoints + additionalPoints + surveyPoints;
        Double maxPoints = getMaxPoints(student, course);

        return new GeneralStats(
                avgGraphTask,
                avgFileTask,
                surveysNumber,
                graphTaskPoints,
                fileTaskPoints,
                allPoints,
                maxPoints
        );
    }

    private Double getAvgGraphTask(User student, Course course) {
        OptionalDouble avg = graphTaskResultRepository.findAllByUserAndCourse(student, course)
                .stream()
                .filter(GraphTaskResult::isEvaluated)
                .mapToDouble(result -> 100 * result.getPointsReceived() / result.getGraphTask().getMaxPoints())
                .average();
        return avg.isPresent() ? PointsCalculator.round(avg.getAsDouble(), 2) : null;
    }

    private Double getAvgFileTask(User student, Course course) {
        OptionalDouble avg = fileTaskResultRepository.findAllByUserAndCourse(student, course)
                .stream()
                .filter(FileTaskResult::isEvaluated)
                .mapToDouble(result -> 100 * result.getPointsReceived() / result.getFileTask().getMaxPoints())
                .average();
        return avg.isPresent() ? PointsCalculator.round(avg.getAsDouble(), 2) : null;
    }

    private Long getSurveysNumber(User student, Course course) {
        return surveyResultRepository.countAllByUserAndCourse(student, course);
    }

    private Double getGraphTaskPoints(User student, Course course) {
        return getTaskPoints(graphTaskResultRepository.findAllByUserAndCourse(student, course));
    }

    private Double getFileTaskPoints(User student, Course course) {
        return getTaskPoints(fileTaskResultRepository.findAllByUserAndCourse(student, course));
    }

    private Double getAdditionalPoints(User student, Course course) {
        return getTaskPoints(additionalPointsRepository.findAllByUserAndCourse(student, course));
    }

    private Double getSurveyPoints(User student, Course course) {
        return getTaskPoints(surveyResultRepository.findAllByUserAndCourse(student, course));
    }

    private Double getTaskPoints(List<? extends TaskResult> taskResults) {
        return taskResults
                .stream()
                .filter(TaskResult::isEvaluated)
                .mapToDouble(TaskResult::getPointsReceived)
                .sum();
    }

    private Double getMaxPoints(User student, Course course) {
        Double maxPointsGraphTask = graphTaskResultRepository.findAllByUserAndCourse(student, course)
                .stream()
                .filter(GraphTaskResult::isEvaluated)
                .mapToDouble(result -> result.getGraphTask().getMaxPoints())
                .sum();
        Double maxPointsFileTask = fileTaskResultRepository.findAllByUserAndCourse(student, course)
                .stream()
                .filter(FileTaskResult::isEvaluated)
                .mapToDouble(result -> result.getFileTask().getMaxPoints())
                .sum();
        Double maxPointsSurvey = surveyResultRepository.findAllByUserAndCourse(student, course)
                .stream()
                .filter(SurveyResult::isEvaluated)
                .mapToDouble(result -> result.getSurvey().getMaxPoints())
                .sum();
        return maxPointsGraphTask + maxPointsFileTask + maxPointsSurvey;
    }

    private List<LastAddedActivity> getLastAddedActivities(Course course) {
        List<GraphTask> graphTasks = graphTaskService.getStudentGraphTasks(course);
        List<FileTask> fileTasks = fileTaskService.getStudentFileTasks(course);
        List<Survey> surveys = surveyService.getStudentSurvey(course);
        List<Info> infos = infoService.getStudentInfos(course);

        return Stream.of(graphTasks, fileTasks, surveys, infos)
                .flatMap(Collection::stream)
                .sorted(((o1, o2) -> Long.compare(o2.getCreationTime(), o1.getCreationTime())))
                .limit(MAX_LAST_ACTIVITIES_IN_DASHBOARD)
                .map(this::toLastAddedActivity)
                .toList();
    }

    private LastAddedActivity toLastAddedActivity(Activity activity) {
        Chapter chapter = chapterService.getChapterWithActivity(activity);
        String chapterName = Objects.nonNull(chapter) ? chapter.getName() : null;
        String activityType = activity.getActivityType().toString();
        Double points = activity.getMaxPoints();
        if (activity.getActivityType().equals(ActivityType.INFO)) {
            points = 0D;
        }

        Requirement requirement = activity.getRequirements()
                .stream()
                .filter(req -> req.getSelected() && Objects.nonNull(req.getDateToMillis()))
                .findAny()
                .orElse(null);
        Long availableUntil = Objects.nonNull(requirement) ? requirement.getDateToMillis() : null;
        return new LastAddedActivity(chapterName, activityType, points, availableUntil);

    }

    private HeroStats getHeroStats(User student, Course course) throws EntityNotFoundException {
        Double experiencePoints = student.getPoints();
        Double nextLvlPoints = getNexLvlPoints(student, course);

        Rank rank = rankService.getCurrentRank(student, course);
        String rankName = rank != null ? rank.getName() : null;
        Long badgesNumber = (long) student.getUnlockedBadges().size();
        Long completedActivities = getCompletedActivities(student);

        return new HeroStats(
                experiencePoints,
                nextLvlPoints,
                rankName,
                badgesNumber,
                completedActivities
        );
    }

    private Double getNexLvlPoints(User student, Course course) throws EntityNotFoundException {
        //TODO add actual course
        List<Rank> sortedRanks = rankService.getSortedRanksForHeroType(student.getHeroType(), course);
        for (int i=sortedRanks.size()-1; i >= 0; i--) {
            if (student.getPoints() >= sortedRanks.get(i).getMinPoints()) {
                if (i == sortedRanks.size() - 1) return null;
                else return sortedRanks.get(i+1).getMinPoints();
            }
        }
        return null;
    }

    // Completed means answer was sent (not necessarily rated)
    private Long getCompletedActivities(User student) {
        Long graphTasksCompleted = graphTaskResultRepository.findAllByUser(student)
                .stream()
                .filter(result -> Objects.nonNull(result.getSendDateMillis()))
                .count();
        Long fileTasksCompleted = (long) fileTaskResultRepository.findAllByUser(student)
                .size();
        Long surveysCompleted = (long) surveyResultRepository.findAllByUser(student).size();
        return graphTasksCompleted + fileTasksCompleted + surveysCompleted;
    }
}
