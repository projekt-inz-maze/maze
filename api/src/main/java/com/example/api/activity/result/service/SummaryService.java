package com.example.api.activity.result.service;

import com.example.api.activity.task.dto.response.result.summary.*;
import com.example.api.activity.task.dto.response.result.summary.util.AverageGradeForChapterCreator;
import com.example.api.activity.task.dto.response.result.summary.util.ScoreCreator;
import com.example.api.map.dto.response.task.ActivityType;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.activity.result.model.TaskResult;
import com.example.api.activity.task.model.Activity;
import com.example.api.activity.task.model.FileTask;
import com.example.api.activity.task.model.GraphTask;
import com.example.api.activity.task.model.Survey;
import com.example.api.group.model.Group;
import com.example.api.map.model.Chapter;
import com.example.api.user.model.User;
import com.example.api.activity.result.repository.FileTaskResultRepository;
import com.example.api.activity.result.repository.GraphTaskResultRepository;
import com.example.api.activity.result.repository.SurveyResultRepository;
import com.example.api.activity.task.repository.FileTaskRepository;
import com.example.api.activity.task.repository.GraphTaskRepository;
import com.example.api.activity.task.repository.SurveyRepository;
import com.example.api.group.repository.GroupRepository;
import com.example.api.map.repository.ChapterRepository;
import com.example.api.user.repository.UserRepository;
import com.example.api.security.AuthenticationService;
import com.example.api.validator.UserValidator;
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
    private final AuthenticationService authService;
    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final GroupRepository groupRepository;
    private final GraphTaskRepository graphTaskRepository;
    private final GraphTaskResultRepository graphTaskResultRepository;
    private final FileTaskRepository fileTaskRepository;
    private final FileTaskResultRepository fileTaskResultRepository;
    private final SurveyRepository surveyRepository;
    private final SurveyResultRepository surveyResultRepository;
    private final ChapterRepository chapterRepository;
    private final PointsToGradeMapper pointsToGradeMapper;

    public SummaryResponse getSummary() throws WrongUserTypeException {
        String professorEmail = authService.getAuthentication().getName();
        User professor = userRepository.findUserByEmail(professorEmail);
        userValidator.validateProfessorAccount(professor, professorEmail);
        log.info("Fetching summary for professor {}", professorEmail);

        List<AverageGrade> avgGradesList = getAvgGradesList(professor);
        List<AverageActivityScore> avgActivitiesScore = getAvgActivitiesScore(professor);
        List<NotAssessedActivity> notAssessedActivitiesTable = getNotAssessedActivitiesTable(professor);

        Double avgGrade = getAvgGrade(avgGradesList);
        Double medianGrade = getMedianGrade(professor);
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

    public Double getMedianGrade(User professor) {
        List<Double> grades = getAllProfessorGrades(professor);
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
        if (result.isEmpty()) return null;
        return result.get().getActivityName();
    }

    public String getWorstScoreActivityName(List<AverageActivityScore> avgActivitiesScore) {
        Optional<ActivityScore> result = flatMapAvgActivitiesScore(avgActivitiesScore)
                .stream()
                .filter(Objects::nonNull)
                .reduce(((activityScore1, activityScore2) ->
                        activityScore1.getAvgScore() < activityScore2.getAvgScore() ? activityScore1 : activityScore2
                ));
        if (result.isEmpty()) return null;
        return result.get().getActivityName();
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

    /////////////////////////////
    // avgGradesList
    /////////////////////////////
    private List<AverageGrade> getAvgGradesList(User professor) {
        return chapterRepository.findAll()
                .stream()
                .map(chapter -> toAverageGrade(chapter, professor))
                .toList();
    }

    private AverageGrade toAverageGrade(Chapter chapter, User professor) {
        AverageGrade averageGrade = new AverageGrade(chapter);
        averageGrade.setAvgGradesForChapter(getAvgGradesForChapter(chapter, professor));
        return averageGrade;
    }

    private List<AverageGradeForChapter> getAvgGradesForChapter(Chapter chapter, User professor) {
        return groupRepository.findAll()
                .stream()
                .map(group -> toAvgGradeForChapter(chapter, group, professor))
                .toList();
    }

    private AverageGradeForChapter toAvgGradeForChapter(Chapter chapter, Group group, User professor) {
        AverageGradeForChapterCreator averageGradeForChapterCreator = new AverageGradeForChapterCreator(group);
        AtomicReference<AverageGradeForChapterCreator> avgGradeForChapterCreator =
                new AtomicReference<AverageGradeForChapterCreator>(averageGradeForChapterCreator);
        getAllProfessorChapterActivitiesResult(chapter, professor)
                .stream()
                .filter(TaskResult::isEvaluated)
                .filter(taskResult -> taskResult.getUser().getGroup().equals(group))
                .forEach(taskResult -> avgGradeForChapterCreator.get().add(pointsToGradeMapper.getGrade(taskResult)));
        return avgGradeForChapterCreator.get().create(); // it will return entities with no results from group
    }

    /////////////////////////////
    // avgActivitiesScore
    /////////////////////////////
    private List<AverageActivityScore> getAvgActivitiesScore(User professor) {
        return chapterRepository.findAll()
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
                .average().getAsDouble();

        activityScore.setAvgScore(GradesCalculator.roundGrade(avgScore));
        return activityScore;
    }

    private List<Score> getScores(Activity activity) {
        return groupRepository.findAll()
                .stream()
                .map(group -> toScore(activity, group))
                .filter(Objects::nonNull)
                .toList();
    }

    private Score toScore(Activity activity, Group group) {
        ScoreCreator scoreCreator = new ScoreCreator(group.getName(), activity.getMaxPoints());
        AtomicReference<ScoreCreator> scoreRef = new AtomicReference<>(scoreCreator);
        getAllResultsForActivity(activity)
                .stream()
                .filter(TaskResult::isEvaluated)
                .filter(taskResult -> taskResult.getUser().getGroup().equals(group))
                .forEach(taskResult -> scoreRef.get().add(taskResult));
        return scoreRef.get().getNumberOfScores() > 0 ? scoreRef.get().create() : null;
    }



    /////////////////////////////
    // notAssessedActivitiesTable
    /////////////////////////////
    private List<NotAssessedActivity> getNotAssessedActivitiesTable(User professor) {
        return getAllProfessorActivities(professor)
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


    /////////////////////////////
    // help methods
    /////////////////////////////
    private List<? extends Activity> getAllActivities() { // without Info
        List<GraphTask> graphTasks = graphTaskRepository.findAll();
        List<FileTask> fileTasks = fileTaskRepository.findAll();
        List<Survey> surveys = surveyRepository.findAll();


        return Stream.of(graphTasks, fileTasks, surveys)
                .flatMap(Collection::stream)
                .toList();
    }

    private List<? extends Activity> getAllProfessorActivities(User professor) { // without Info
        return getAllActivities()
                .stream()
                .filter(activity -> isProfessorActivity(activity, professor))
                .toList();
    }

    private List<? extends Activity> getAllProfessorActivitiesToAssess(User professor) { // without Info
        return fileTaskRepository.findAll().stream().filter(activity -> isProfessorActivity(activity, professor)).toList();
    }


    private List<? extends Activity> getAllProfessorChapterActivities(Chapter chapter, User professor) { // without Info
        return getAllActivities()
                .stream()
                .filter(activity -> chapter.getActivityMap().hasActivity(activity))
                .filter(activity -> isProfessorActivity(activity, professor))
                .toList();
    }

    private List<? extends TaskResult> getAllResultsForActivity(Activity activity) {
        if (activity.getActivityType().equals(ActivityType.EXPEDITION)) {
            return graphTaskResultRepository.findAllByGraphTask((GraphTask) activity);
        }
        else if (activity.getActivityType().equals(ActivityType.TASK)) {
            return fileTaskResultRepository.findAllByFileTask((FileTask) activity);
        }
        else if (activity.getActivityType().equals(ActivityType.SURVEY)) {
            return surveyResultRepository.findAllBySurvey((Survey) activity);
        }
        return List.of();
    }

    private List<? extends TaskResult> getAllProfessorChapterActivitiesResult(Chapter chapter, User professor) {
        return getAllProfessorChapterActivities(chapter, professor)
                .stream()
                .map(this::getAllResultsForActivity)
                .flatMap(Collection::stream)
                .toList();
    }

    private List<Double> getAllProfessorGrades(User professor) {
        return getAllProfessorActivities(professor)
                .stream()
                .map(this::getAllResultsForActivity)
                .flatMap(Collection::stream)
                .filter(TaskResult::isEvaluated)
                .map(taskResult -> pointsToGradeMapper.getGrade(taskResult))
                .toList();
    }



    private boolean isProfessorActivity(Activity activity, User professor) {
        return activity.getProfessor() != null && activity.getProfessor().equals(professor);

    }

}
