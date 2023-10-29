package com.example.api.user.service;

import com.example.api.activity.Activity;
import com.example.api.activity.info.Info;
import com.example.api.activity.result.dto.response.RankingResponse;
import com.example.api.activity.result.model.FileTaskResult;
import com.example.api.activity.result.model.GraphTaskResult;
import com.example.api.activity.result.model.SurveyResult;
import com.example.api.activity.result.model.TaskResult;
import com.example.api.activity.result.repository.AdditionalPointsRepository;
import com.example.api.activity.result.repository.FileTaskResultRepository;
import com.example.api.activity.result.repository.GraphTaskResultRepository;
import com.example.api.activity.result.repository.SurveyResultRepository;
import com.example.api.activity.result.service.ActivityResultService;
import com.example.api.activity.result.service.ranking.RankingService;
import com.example.api.activity.survey.Survey;
import com.example.api.activity.task.filetask.FileTask;
import com.example.api.activity.task.graphtask.GraphTask;
import com.example.api.activity.task.graphtask.GraphTaskController;
import com.example.api.activity.task.filetask.FileTaskService;
import com.example.api.activity.info.InfoService;
import com.example.api.activity.survey.SurveyService;
import com.example.api.course.model.Course;
import com.example.api.course.model.CourseMember;
import com.example.api.course.service.CourseService;
import com.example.api.course.validator.CourseValidator;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.MissingAttributeException;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.map.dto.response.task.ActivityType;
import com.example.api.map.model.Chapter;
import com.example.api.map.model.requirement.Requirement;
import com.example.api.map.service.ChapterService;
import com.example.api.user.dto.response.dashboard.*;
import com.example.api.user.hero.HeroStatsDTO;
import com.example.api.user.hero.HeroTypeStatsDTO;
import com.example.api.user.model.Rank;
import com.example.api.user.model.User;
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
    private final RankingService rankingService;
    private final GraphTaskResultRepository graphTaskResultRepository;
    private final FileTaskResultRepository fileTaskResultRepository;
    private final SurveyResultRepository surveyResultRepository;
    private final AdditionalPointsRepository additionalPointsRepository;
    private final GraphTaskController.GraphTaskService graphTaskService;
    private final FileTaskService fileTaskService;
    private final SurveyService surveyService;
    private final InfoService infoService;
    private final ChapterService chapterService;
    private final RankService rankService;
    private final BadgeService badgeService;
    private final UserService userService;
    private final CourseService courseService;
    private final CourseValidator courseValidator;
    private final ActivityResultService activityResultService;

    private final long MAX_LAST_ACTIVITIES_IN_DASHBOARD = 8;

    public DashboardResponse getStudentDashboard(Long courseId) throws WrongUserTypeException, EntityNotFoundException, MissingAttributeException {
        User student = userService.getCurrentUserAndValidateStudentAccount();
        CourseMember member = student.getCourseMember(courseId).orElseThrow();
        Course course = courseService.getCourse(courseId);

        badgeService.checkAllBadges(member);

        return new DashboardResponse(
                getHeroTypeStats(member),
                getGeneralStats(student, course),
                getLastAddedActivities(course),
                getHeroStats(member)
        );
    }

    private HeroTypeStatsDTO getHeroTypeStats(CourseMember member) throws EntityNotFoundException {
        String heroType = String.valueOf(member.getHeroType());

        List<RankingResponse> ranking = rankingService.getRanking(member.getCourse().getId());
        RankingResponse rank = getRank(member.getUser(), ranking);

        if (rank == null) {
            log.error("Student {} not found in ranking", member.getUser().getId());
            throw new EntityNotFoundException("Student " + member.getUser().getId() + " not found in ranking");
        }

        Integer rankPosition = rank.getPosition();
        Long rankLength = (long) ranking.size();
        Double betterPlayerPoints = rankPosition > 1 ? ranking.get(rankPosition - 2).getPoints() : null;
        Double worsePlayerPoints = rankPosition < rankLength ? ranking.get(rankPosition).getPoints() : null;

        return new HeroTypeStatsDTO(heroType, rankPosition, rankLength, betterPlayerPoints, worsePlayerPoints);
    }

    private RankingResponse getRank(User student, List<RankingResponse> ranking) {
        return ranking
                .stream()
                .filter(rankingResponse -> rankingResponse.getEmail().equals(student.getEmail()))
                .findAny()
                .orElse(null);
    }

    private GeneralStats getGeneralStats(User student, Course course) {
        log.info("getGeneralStats");

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
        OptionalDouble avg = fileTaskResultRepository.findAllByMember_UserAndCourse(student, course)
                .stream()
                .filter(FileTaskResult::isEvaluated)
                .mapToDouble(result -> 100 * result.getPointsReceived() / result.getFileTask().getMaxPoints())
                .average();
        return avg.isPresent() ? PointsCalculator.round(avg.getAsDouble(), 2) : null;
    }

    private Long getSurveysNumber(User student, Course course) {
        return surveyResultRepository.countAllByMember_UserAndCourse(student, course);
    }

    private Double getGraphTaskPoints(User student, Course course) {
        return getTaskPoints(graphTaskResultRepository.findAllByUserAndCourse(student, course));
    }

    private Double getFileTaskPoints(User student, Course course) {
        return getTaskPoints(fileTaskResultRepository.findAllByMember_UserAndCourse(student, course));
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
        Double maxPointsFileTask = fileTaskResultRepository.findAllByMember_UserAndCourse(student, course)
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
        log.info("getLastAddedActivities");
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

    private HeroStatsDTO getHeroStats(CourseMember member) {
        log.info("getHeroStats");
        Double experiencePoints = member.getPoints();
        Double nextLvlPoints = getNexLvlPoints(member);

        Rank rank = rankService.getCurrentRank(member);
        String rankName = rank != null ? rank.getName() : null;
        Long badgesNumber = (long) member.getUnlockedBadges().size();
        Long completedActivities = activityResultService.countCompletedActivities(member);

        return new HeroStatsDTO(
                experiencePoints,
                nextLvlPoints,
                rankName,
                badgesNumber,
                completedActivities
        );
    }

    private Double getNexLvlPoints(CourseMember member) {
        List<Rank> sortedRanks = rankService.getSortedRanksForHeroType(member);
        for (int i=sortedRanks.size()-1; i >= 0; i--) {
            if (member.getPoints() >= sortedRanks.get(i).getMinPoints()) {
                if (i == sortedRanks.size() - 1) return null;
                else return sortedRanks.get(i+1).getMinPoints();
            }
        }
        return null;
    }
}
