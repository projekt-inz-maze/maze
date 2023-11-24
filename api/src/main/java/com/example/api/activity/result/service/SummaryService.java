package com.example.api.activity.result.service;

import com.example.api.activity.result.repository.ActivityResultRepository;
import com.example.api.activity.task.dto.response.result.summary.*;
import com.example.api.activity.task.dto.response.result.summary.util.AverageGradeForChapterCreator;
import com.example.api.activity.task.dto.response.result.summary.util.ScoreCreator;
import com.example.api.course.Course;
import com.example.api.course.CourseService;
import com.example.api.course.CourseValidator;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.activity.ActivityType;
import com.example.api.activity.result.model.ActivityResult;
import com.example.api.activity.Activity;
import com.example.api.activity.task.filetask.FileTask;
import com.example.api.activity.task.graphtask.GraphTask;
import com.example.api.activity.survey.Survey;
import com.example.api.group.Group;
import com.example.api.chapter.Chapter;
import com.example.api.chapter.ChapterService;
import com.example.api.security.LoggedInUserService;
import com.example.api.user.model.User;
import com.example.api.activity.result.repository.FileTaskResultRepository;
import com.example.api.activity.result.repository.GraphTaskResultRepository;
import com.example.api.activity.result.repository.SurveyResultRepository;
import com.example.api.activity.task.filetask.FileTaskRepository;
import com.example.api.activity.task.graphtask.GraphTaskRepository;
import com.example.api.activity.survey.SurveyRepository;
import com.example.api.group.GroupRepository;
import com.example.api.chapter.ChapterRepository;
import com.example.api.user.service.UserService;
import com.example.api.util.calculator.GradesCalculator;
import com.example.api.util.csv.PointsToGradeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SummaryService {
    private final GroupRepository groupRepository;
    private final GraphTaskRepository graphTaskRepository;
    private final FileTaskRepository fileTaskRepository;
    private final SurveyRepository surveyRepository;
    private final ChapterRepository chapterRepository;
    private final PointsToGradeMapper pointsToGradeMapper;
    private final CourseService courseService;
    private final CourseValidator courseValidator;
    private final ChapterService chapterService;
    private final LoggedInUserService authService;
    private final ActivityResultRepository activityResultRepository;

    public SummaryResponse getSummary(Long courseId) throws RequestValidationException {
        User professor = authService.getCurrentUser();
        Course course = courseService.getCourse(courseId);
        courseValidator.validateCourseOwner(course, professor);

        log.info("Fetching summary for professor {}", professor.getEmail());

        List<AverageGrade> avgGradesList = getAvgGradesList(professor, course);

        List<AverageActivityScore> avgActivitiesScore = getAvgActivitiesScore(professor, course);

        List<NotAssessedActivity> notAssessedActivitiesTable = getNotAssessedActivitiesTable(professor, course);

        Double avgGrade = getAvgGrade(avgGradesList);

        Double medianGrade = getMedianGrade(professor, course);
        String bestScoreActivityName = getBestScoreActivityName(avgActivitiesScore);
        String worstScoreActivityName = getWorstScoreActivityName(avgActivitiesScore);

        Integer assessedActivitiesCounter = getAssessedActivitiesCounter(professor, notAssessedActivitiesTable);
        Integer notAssessedActivityCounter = getNotAssessedActivitiesCounter(notAssessedActivitiesTable);
        Integer waitingAnswersNumber = getWaitingAnswersNumber(notAssessedActivitiesTable);

        SummaryResponse summaryResponse = new SummaryResponse();
        summaryResponse.setAvgGrade(avgGrade);
        summaryResponse.setMedianGrade(medianGrade);
        summaryResponse.setBestScoreActivityName(bestScoreActivityName);
        summaryResponse.setWorstScoreActivityName(worstScoreActivityName);
        summaryResponse.setAssessedActivityCounter(assessedActivitiesCounter);
        summaryResponse.setNotAssessedActivityCounter(notAssessedActivityCounter);
        summaryResponse.setWaitingAnswersNumber(waitingAnswersNumber);
        summaryResponse.setAvgGradesList(avgGradesList);
        summaryResponse.setAvgActivitiesScore(avgActivitiesScore);
        summaryResponse.setNotAssessedActivitiesTable(notAssessedActivitiesTable);
        return summaryResponse;

    }

    public DoubleStream flatMapAvgGradesList(List<AverageGrade> avgGradesList) {
        return avgGradesList
                .stream()
                .map(AverageGrade::getAvgGradesForChapter)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .map(AverageGradeForChapter::getAvgGrade)
                .filter(Objects::nonNull)
                .mapToDouble(d -> d);
    }

    public Double getAvgGrade(List<AverageGrade> avgGradesList) {
        if (avgGradesList.isEmpty()) return null;
        OptionalDouble grade = flatMapAvgGradesList(avgGradesList)
                .filter(Objects::nonNull)
                .average();
        return grade.isPresent() ? GradesCalculator.roundGrade(grade.getAsDouble()) : null;
    }

    public Double getMedianGrade(User professor, Course course) {
        List<Double> grades = getAllProfessorGrades(professor, course);
        Double medianGrade = GradesCalculator.getMedian(grades);
        if (medianGrade == null) return medianGrade;
        return GradesCalculator.roundGrade(medianGrade);
    }

    public List<ActivityScore> flatMapAvgActivitiesScore(List<AverageActivityScore> avgActivitiesScore) {
        return avgActivitiesScore
                .stream()
                .map(AverageActivityScore::getActivitiesScore)
                .flatMap(Collection::stream)
                .toList();
    }

    public String getBestScoreActivityName(List<AverageActivityScore> avgActivitiesScore) {
        Optional<ActivityScore> result = flatMapAvgActivitiesScore(avgActivitiesScore)
                .stream()
                .filter(Objects::nonNull)
                .reduce(((activityScore1, activityScore2) ->
                        activityScore1.getAvgScore() > activityScore2.getAvgScore() ? activityScore1 : activityScore2
                ));
        return result.map(ActivityScore::getActivityName).orElse(null);
    }

    public String getWorstScoreActivityName(List<AverageActivityScore> avgActivitiesScore) {
        Optional<ActivityScore> result = flatMapAvgActivitiesScore(avgActivitiesScore)
                .stream()
                .filter(Objects::nonNull)
                .reduce(((activityScore1, activityScore2) ->
                        activityScore1.getAvgScore() < activityScore2.getAvgScore() ? activityScore1 : activityScore2
                ));
        return result.map(ActivityScore::getActivityName).orElse(null);
    }

    public Integer getAssessedActivitiesCounter(User professor, List<NotAssessedActivity> notAssessedActivitiesTable) {
        return getAllProfessorActivitiesToAssess(professor).size() - notAssessedActivitiesTable.size();
    }


    public Integer getNotAssessedActivitiesCounter(List<NotAssessedActivity> notAssessedActivitiesTable) {
        return notAssessedActivitiesTable.size();
    }

    public Integer getWaitingAnswersNumber(List<NotAssessedActivity> notAssessedActivitiesTable) {
        if (notAssessedActivitiesTable.isEmpty()) return 0;
        return notAssessedActivitiesTable
                .stream()
                .mapToInt(NotAssessedActivity::getWaitingAnswersNumber)
                .sum();
    }

    private List<AverageGrade> getAvgGradesList(User professor, Course course) {
        return chapterRepository.findAllByCourse(course)
                .stream()
                .map(chapter -> toAverageGrade(chapter, professor))
                .toList();
    }

    private AverageGrade toAverageGrade(Chapter chapter, User professor) {
        return new AverageGrade(chapter.getName(), calculateAverageGradeForChapter(chapter, professor));
    }

    private List<AverageGradeForChapter> calculateAverageGradeForChapter(Chapter chapter, User professor) {
        return groupRepository.findAllByCourse(chapter.getCourse())
                .stream()
                .map(group -> toAvgGradeForChapter(chapter, group, professor))
                .toList();
    }

    private AverageGradeForChapter toAvgGradeForChapter(Chapter chapter, Group group, User professor) {
        List<Double> grades = getAllProfessorChapterActivitiesResult(chapter, professor)
                .stream()
                .filter(ActivityResult::isEvaluated)
                .filter(taskResult -> taskResult.getMember().getGroup().equals(group))
                .map(pointsToGradeMapper::getGrade)
                .toList();

        return new AverageGradeForChapterCreator(group.getName(), grades).create();
    }

    private List<AverageActivityScore> getAvgActivitiesScore(User professor, Course course) {
        return chapterRepository.findAllByCourse(course)
                .stream()
                .map(chapter -> toAvgActivityScore(chapter, professor))
                .toList();
    }

    private AverageActivityScore toAvgActivityScore(Chapter chapter, User professor) {
        AverageActivityScore avgActivityScore = new AverageActivityScore();
        avgActivityScore.setChapterName(chapter.getName());
        avgActivityScore.setActivitiesScore(getActivitiesScore(chapter, professor));
        return avgActivityScore;
    }

    private List<ActivityScore> getActivitiesScore(Chapter chapter, User professor) {
        return getAllProfessorChapterActivities(chapter, professor)
                .stream()
                .map(this::toActivityScore)
                .filter(Objects::nonNull)
                .toList();
    }

    private ActivityScore toActivityScore(Activity activity) {
        ActivityScore activityScore = new ActivityScore();
        activityScore.setActivityName(activity.getTitle());
        activityScore.setScores(getScores(activity));

        if (activityScore.getScores().isEmpty()) return null;

        Double avgScore = activityScore.getScores()
                .stream()
                .mapToDouble(Score::getScore)
                .average()
                .getAsDouble();

        activityScore.setAvgScore(GradesCalculator.roundGrade(avgScore));
        return activityScore;
    }

    private List<Score> getScores(Activity activity) {
        return groupRepository.findAllByCourse(activity.getCourse())
                .stream()
                .flatMap(group -> toScore(activity, group).stream())
                .toList();
    }

    private Optional<Score> toScore(Activity activity, Group group) {
        ScoreCreator scoreCreator = new ScoreCreator(group.getName(), activity.getMaxPoints());
        AtomicReference<ScoreCreator> scoreRef = new AtomicReference<>(scoreCreator);
        getAllResultsForActivity(activity)
                .stream()
                .filter(ActivityResult::isEvaluated)
                .filter(taskResult -> taskResult.getMember().getGroup().equals(group))
                .forEach(taskResult -> scoreRef.get().add(taskResult));
        return scoreRef.get().create();
    }

    private List<NotAssessedActivity> getNotAssessedActivitiesTable(User professor, Course course) {
        return getAllProfessorActivities(professor, course)
                .stream()
                .map(this::toNotAssessedActivity)
                .filter(notAssessedActivity -> notAssessedActivity.getWaitingAnswersNumber() > 0) // only activities with left answers
                .toList();

    }

    private NotAssessedActivity toNotAssessedActivity(Activity activity) {
        NotAssessedActivity notAssessedActivity = new NotAssessedActivity(activity);
        int waitingAnswersNumber = getAllResultsForActivity(activity)
                .stream()
                .filter(task -> !task.isEvaluated())
                .toList()
                .size();
        notAssessedActivity.setWaitingAnswersNumber(waitingAnswersNumber);
        return notAssessedActivity;
    }

    private List<? extends Activity> getAllActivities(Course course) {
        List<GraphTask> graphTasks = graphTaskRepository.findAllByCourse(course);
        List<FileTask> fileTasks = fileTaskRepository.findAllByCourse(course);
        List<Survey> surveys = surveyRepository.findAllByCourse(course);

        return Stream.of(graphTasks, fileTasks, surveys)
                .flatMap(Collection::stream)
                .toList();
    }

    private List<? extends Activity> getAllProfessorActivities(User professor, Course course) {
        List<GraphTask> graphTasks = graphTaskRepository.findAllByCourseAndProfessor(course, professor);
        List<FileTask> fileTasks = fileTaskRepository.findAllByCourseAndProfessor(course, professor);
        List<Survey> surveys = surveyRepository.findAllByCourseAndProfessor(course, professor);

        return Stream.of(graphTasks, fileTasks, surveys)
                .flatMap(Collection::stream)
                .toList();
    }

    private List<? extends Activity> getAllProfessorActivitiesToAssess(User professor) { // without Info
        return fileTaskRepository.findAll().stream().filter(activity -> isProfessorActivity(activity, professor)).toList();
    }


    private List<? extends Activity> getAllProfessorChapterActivities(Chapter chapter, User professor) { // without Info
        return chapterService.getAllActivitiesForChapter(chapter)
                .stream()
                .filter(activity -> isProfessorActivity(activity, professor))
                .toList();
    }

    private List<? extends ActivityResult> getAllResultsForActivity(Activity activity) {
        return activityResultRepository.findAllByActivity(activity);
    }

    private List<? extends ActivityResult> getAllProfessorChapterActivitiesResult(Chapter chapter, User professor) {
        return getAllProfessorChapterActivities(chapter, professor)
                .stream()
                .map(this::getAllResultsForActivity)
                .flatMap(Collection::stream)
                .toList();
    }

    private List<Double> getAllProfessorGrades(User professor, Course course) {
        return getAllProfessorActivities(professor, course)
                .stream()
                .map(this::getAllResultsForActivity)
                .flatMap(Collection::stream)
                .filter(ActivityResult::isEvaluated)
                .map(pointsToGradeMapper::getGrade)
                .toList();
    }

    private boolean isProfessorActivity(Activity activity, User professor) {
        return activity.getProfessor() != null && activity.getProfessor().equals(professor);

    }

}
