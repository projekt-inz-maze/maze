package com.example.api.user.service;

import com.example.api.activity.Activity;
import com.example.api.activity.info.Info;
import com.example.api.activity.info.InfoService;
import com.example.api.activity.result.dto.response.RankingResponse;
import com.example.api.activity.result.model.FileTaskResult;
import com.example.api.activity.result.model.GraphTaskResult;
import com.example.api.activity.result.model.SurveyResult;
import com.example.api.activity.result.model.ActivityResult;
import com.example.api.activity.result.repository.AdditionalPointsRepository;
import com.example.api.activity.result.repository.FileTaskResultRepository;
import com.example.api.activity.result.repository.GraphTaskResultRepository;
import com.example.api.activity.result.repository.SurveyResultRepository;
import com.example.api.activity.result.service.ActivityResultService;
import com.example.api.activity.result.service.ranking.RankingService;
import com.example.api.activity.survey.Survey;
import com.example.api.activity.survey.SurveyService;
import com.example.api.activity.task.filetask.FileTask;
import com.example.api.activity.task.filetask.FileTaskService;
import com.example.api.activity.task.graphtask.GraphTask;
import com.example.api.activity.task.graphtask.GraphTaskService;
import com.example.api.course.Course;
import com.example.api.course.coursemember.CourseMember;
import com.example.api.course.CourseService;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.MissingAttributeException;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.activity.ActivityType;
import com.example.api.chapter.Chapter;
import com.example.api.chapter.requirement.model.Requirement;
import com.example.api.chapter.ChapterService;
import com.example.api.user.badge.BadgeService;
import com.example.api.user.dto.response.dashboard.DashboardResponse;
import com.example.api.user.dto.response.dashboard.GeneralStats;
import com.example.api.user.dto.response.dashboard.LastAddedActivity;
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
    private final GraphTaskService graphTaskService;
    private final FileTaskService fileTaskService;
    private final SurveyService surveyService;
    private final InfoService infoService;
    private final ChapterService chapterService;
    private final RankService rankService;
    private final BadgeService badgeService;
    private final UserService userService;
    private final CourseService courseService;
    private final ActivityResultService activityResultService;

    private final long MAX_LAST_ACTIVITIES_IN_DASHBOARD = 8;

    public DashboardResponse getStudentDashboard(Long courseId) throws WrongUserTypeException, EntityNotFoundException, MissingAttributeException {
        log.info("getStudentDashboard");
        User student = userService.getCurrentUserAndValidateStudentAccount();
        CourseMember member = student.getCourseMember(courseId).orElseThrow();
        Course course = courseService.getCourse(courseId);

        badgeService.checkAllBadges(member);

        return new DashboardResponse(
                getHeroTypeStats(member),
                getGeneralStats(student, course, member),
                getLastAddedActivities(course),
                getHeroStats(member)
        );
    }

    private HeroTypeStatsDTO getHeroTypeStats(CourseMember member) throws EntityNotFoundException {
        log.info("getHeroTypeStats");
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

    private GeneralStats getGeneralStats(User student, Course course, CourseMember member) {
        log.info("getGeneralStats");

        Double avgGraphTask = getAvgGraphTask(member);
        Double avgFileTask = getAvgFileTask(member);
        Long surveysNumber = getSurveysNumber(member);
        Double graphTaskPoints = getGraphTaskPoints(member);
        Double fileTaskPoints = getFileTaskPoints(member);
        Double userPoints = member.getPoints();
        Double maxPoints = getMaxPoints(student, course);

        return new GeneralStats(
                avgGraphTask,
                avgFileTask,
                surveysNumber,
                graphTaskPoints,
                fileTaskPoints,
                userPoints,
                maxPoints
        );
    }

    private Double getAvgGraphTask(CourseMember member) {
        OptionalDouble avg = graphTaskResultRepository.findAllByMember(member)
                .stream()
                .filter(GraphTaskResult::isEvaluated)
                .mapToDouble(result -> 100 * result.getPoints() / result.getGraphTask().getMaxPoints())
                .average();
        return avg.isPresent() ? PointsCalculator.round(avg.getAsDouble(), 2) : null;
    }

    private Double getAvgFileTask(CourseMember member) {
        OptionalDouble avg = fileTaskResultRepository.findAllByMember(member)
                .stream()
                .filter(FileTaskResult::isEvaluated)
                .mapToDouble(result -> 100 * result.getPoints() / result.getFileTask().getMaxPoints())
                .average();
        return avg.isPresent() ? PointsCalculator.round(avg.getAsDouble(), 2) : null;
    }

    private Long getSurveysNumber(CourseMember member) {
        return surveyResultRepository.countAllByMember(member);
    }

    private Double getGraphTaskPoints(CourseMember member) {
        return getTaskPoints(graphTaskResultRepository.findAllByMember(member));
    }

    private Double getFileTaskPoints(CourseMember member) {
        return getTaskPoints(fileTaskResultRepository.findAllByMember(member));
    }

    private Double getAdditionalPoints(CourseMember member) {
        return getTaskPoints(additionalPointsRepository.findAllByMember(member));
    }

    private Double getSurveyPoints(CourseMember member) {
        return getTaskPoints(surveyResultRepository.findAllByMember(member));
    }

    private Double getTaskPoints(List<? extends ActivityResult> taskResults) {
        return taskResults
                .stream()
                .filter(ActivityResult::isEvaluated)
                .mapToDouble(ActivityResult::getPoints)
                .sum();
    }

    private Double getMaxPoints(User student, Course course) {
        Double maxPointsGraphTask = graphTaskResultRepository.findAllByUserAndCourse(student, course)
                .stream()
                .filter(GraphTaskResult::isEvaluated)
                .mapToDouble(result -> result.getGraphTask().getMaxPoints())
                .sum();
        Double maxPointsFileTask = fileTaskResultRepository.findAllByMember_UserAndMember_Course(student, course)
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
