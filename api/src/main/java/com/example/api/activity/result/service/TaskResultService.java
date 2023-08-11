package com.example.api.activity.result.service;

import com.example.api.activity.ActivityService;
import com.example.api.activity.result.service.util.GroupActivityStatisticsCreator;
import com.example.api.activity.result.service.util.ScaleActivityStatisticsCreator;
import com.example.api.activity.task.dto.request.GetCSVForm;
import com.example.api.activity.task.dto.response.result.ActivityStatisticsResponse;
import com.example.api.activity.task.dto.response.result.TaskPointsStatisticsResponse;
import com.example.api.activity.task.model.*;
import com.example.api.course.model.Course;
import com.example.api.course.service.CourseService;
import com.example.api.map.dto.response.task.ActivityType;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.activity.feedback.model.Feedback;
import com.example.api.activity.result.model.FileTaskResult;
import com.example.api.activity.result.model.GraphTaskResult;
import com.example.api.activity.result.model.SurveyResult;
import com.example.api.activity.result.model.TaskResult;
import com.example.api.group.model.Group;
import com.example.api.user.model.AccountType;
import com.example.api.user.model.User;
import com.example.api.activity.feedback.repository.ProfessorFeedbackRepository;
import com.example.api.activity.result.repository.FileTaskResultRepository;
import com.example.api.activity.result.repository.GraphTaskResultRepository;
import com.example.api.activity.result.repository.SurveyResultRepository;
import com.example.api.activity.task.repository.FileTaskRepository;
import com.example.api.activity.task.repository.GraphTaskRepository;
import com.example.api.activity.task.repository.SurveyRepository;
import com.example.api.user.repository.UserRepository;
import com.example.api.user.service.UserService;
import com.example.api.activity.validator.ActivityValidator;
import com.example.api.util.csv.CSVConverter;
import com.example.api.util.csv.CSVTaskResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TaskResultService {
    private final UserRepository userRepository;
    private final GraphTaskResultRepository graphTaskResultRepository;
    private final GraphTaskRepository graphTaskRepository;
    private final FileTaskResultRepository fileTaskResultRepository;
    private final FileTaskRepository fileTaskRepository;
    private final SurveyResultRepository surveyResultRepository;
    private final SurveyRepository surveyRepository;
    private final CSVConverter csvConverter;
    private final ProfessorFeedbackRepository professorFeedbackRepository;
    private final UserService userService;
    private final ActivityService activityService;
    private final CourseService courseService;

    public ByteArrayResource getCSVFile(GetCSVForm csvForm) {
        log.info("Fetching csv files for students");
        List<Long> studentIds = csvForm.getStudentIds();
        List<Long> activityIds = csvForm.getActivityIds();
        List<User> students = userRepository.findAll()
                .stream()
                .filter(user -> studentIds.contains(user.getId()))
                .filter(user -> user.getAccountType() == AccountType.STUDENT)
                .toList();
        Map<User, List<CSVTaskResult>> userToResultMap = new HashMap<>();
        List<String> firstRow = new LinkedList<>(
                List.of("Imię", "Nazwisko", "NumerId", "Instytucja", "Wydział", "E-mail"));
        Map<Long, GraphTask> formToGraphTaskMap = new HashMap<>();
        Map<Long, FileTask> formToFileTaskMap = new HashMap<>();
        Map<Long, Survey> formToSurveyMap = new HashMap<>();
        fillFirstRowAndAddTasksToMap(activityIds, formToGraphTaskMap, formToFileTaskMap, formToSurveyMap, firstRow);
        students.forEach(student -> {
            List<CSVTaskResult> csvTaskResults = new LinkedList<>();
            activityIds.forEach(activityId -> {
                Activity activity = getActivity(activityId);
                if (activity != null) {
                    ActivityType type = activity.getActivityType();
                    switch (type) {
                        case EXPEDITION -> {
                            GraphTask graphTask = formToGraphTaskMap.get(activityId);
                            GraphTaskResult graphTaskResult = graphTaskResultRepository
                                    .findGraphTaskResultByGraphTaskAndUser(graphTask, student);
                            csvTaskResults.add(new CSVTaskResult(graphTaskResult));
                        }
                        case TASK -> {
                            FileTask fileTask = formToFileTaskMap.get(activityId);
                            FileTaskResult fileTaskResult = fileTaskResultRepository.findFileTaskResultByFileTaskAndUser(
                                    fileTask,
                                    student);
                            Feedback feedback = professorFeedbackRepository
                                    .findProfessorFeedbackByFileTaskResult(fileTaskResult);
                            csvTaskResults.add(new CSVTaskResult(fileTaskResult, feedback));
                        }
                        case SURVEY -> {
                            Survey survey = formToSurveyMap.get(activityId);
                            SurveyResult surveyResult = surveyResultRepository.findSurveyResultBySurveyAndUser(survey,
                                    student);
                            csvTaskResults.add(new CSVTaskResult(surveyResult));
                        }
                    }
                }

            });
            userToResultMap.put(student, csvTaskResults);
        });
        return new ByteArrayResource(csvConverter.convertToByteArray(userToResultMap, firstRow));
    }

    public List<? extends TaskResult> getAllResultsForStudent(User student) {
        List<SurveyResult> surveyResults = surveyResultRepository.findAllByUser(student);
        return Stream.of(getGraphAndFileResultsForStudent(student), surveyResults)
                .flatMap(Collection::stream)
                .toList();
    }

    public List<? extends TaskResult> getGraphAndFileResultsForStudent(User student) {
        List<GraphTaskResult> graphTaskResults = graphTaskResultRepository.findAllByUser(student);
        List<FileTaskResult> fileTaskResults = fileTaskResultRepository.findAllByUser(student);
        return Stream.of(graphTaskResults, fileTaskResults)
                .flatMap(Collection::stream)
                .toList();
    }

    public List<TaskPointsStatisticsResponse> getUserPointsStatistics(Long courseId) throws WrongUserTypeException, EntityNotFoundException {
        User user = userService.getCurrentUserAndValidateStudentAccount();
        Course course = courseService.getCourse(courseId);
        return getUserPointsStatistics(user, course);
    }

    public List<TaskPointsStatisticsResponse> getUserPointsStatistics(User student, Course course) {

        List<TaskPointsStatisticsResponse> graphTaskStatistics = graphTaskResultRepository.findAllByUserAndCourse(student, course)
                .stream()
                .filter(graphTaskResult -> graphTaskResult.getSendDateMillis() != null)
                .map(TaskPointsStatisticsResponse::new)
                .toList();
        List<TaskPointsStatisticsResponse> fileTaskResults = fileTaskResultRepository.findAllByUserAndCourse(student, course)
                .stream()
                .filter(FileTaskResult::isEvaluated)
                .map(TaskPointsStatisticsResponse::new)
                .toList();
        List<TaskPointsStatisticsResponse> surveyResults = surveyResultRepository.findAllByUserAndCourse(student, course)
                .stream()
                .map(TaskPointsStatisticsResponse::new)
                .toList();
        return Stream.of(graphTaskStatistics, fileTaskResults, surveyResults)
                .flatMap(Collection::stream)
                .sorted(((o1, o2) -> Long.compare(o2.getDateMillis(), o1.getDateMillis())))
                .toList();
    }

    public ActivityStatisticsResponse getActivityStatistics(Long activityID) throws WrongUserTypeException, EntityNotFoundException {
        userService.getCurrentUserAndValidateProfessorAccount();

        return activityService.getGradedActivity(activityID)
                .map(activity -> getActivityStatisticsForActivity(activity))
                .orElseThrow(() -> new EntityNotFoundException("Activity not found"));
    }

    private void fillFirstRowAndAddTasksToMap(List<Long> activityIds,
            Map<Long, GraphTask> formToGraphTaskMap,
            Map<Long, FileTask> formToFileTaskMap,
            Map<Long, Survey> formToSurveyMap,
            List<String> firstRow) {
        activityIds.forEach(activityId -> {
            Activity activity = getActivity(activityId);
            if (activity != null) {
                ActivityType type = activity.getActivityType();
                switch (type) {
                    case EXPEDITION -> {
                        firstRow.addAll(List.of("Zadanie:" + activity.getTitle() + " (Punkty)",
                                "Zadanie:" + activity.getTitle() + " (Informacja zwrotna)"));
                        formToGraphTaskMap.put(activity.getId(), (GraphTask) activity);

                    }
                    case TASK -> {
                        firstRow.addAll(List.of("Zadanie:" + activity.getTitle() + " (Punkty)",
                                "Zadanie:" + activity.getTitle() + " (Informacja zwrotna)"));
                        formToFileTaskMap.put(activity.getId(), (FileTask) activity);

                    }
                    case SURVEY -> {
                        firstRow.addAll(List.of("Zadanie:" + activity.getTitle() + " (Punkty)",
                                "Zadanie:" + activity.getTitle() + " (Informacja zwrotna)"));
                        formToSurveyMap.put(activity.getId(), (Survey) activity);
                    }
                }
            }
        });
        firstRow.add("Zadanie:Proponowane oceny");
    }

    private Activity getActivity(Long activityID) {
        GraphTask graphTask = graphTaskRepository.findGraphTaskById(activityID);
        if (graphTask != null) {
            return graphTask;
        }
        FileTask fileTask = fileTaskRepository.findFileTaskById(activityID);
        if (fileTask != null) {
            return fileTask;
        }
        return surveyRepository.findSurveyById(activityID);
    }

    private List<? extends TaskResult> getResultsForTask(Task task) {
        if (task.getActivityType().equals(ActivityType.EXPEDITION)) {
            return graphTaskResultRepository.findAllByGraphTask((GraphTask) task)
                    .stream()
                    .filter(result -> result.getPointsReceived() != null)
                    .toList();
        } else if (task.getActivityType().equals(ActivityType.TASK)) {
            return fileTaskResultRepository.findAllByFileTask((FileTask) task)
                    .stream()
                    .filter(FileTaskResult::isEvaluated)
                    .toList();
        }
        return List.of();
    }

    private ActivityStatisticsResponse getActivityStatisticsForActivity(Activity activity) {
        ActivityStatisticsResponse response = new ActivityStatisticsResponse();

        boolean activityIsSurvey = activity.getActivityType().equals(ActivityType.SURVEY);

        response.setActivity100(activity.getMaxPoints());


        AtomicInteger answersNumber = new AtomicInteger(0);
        AtomicReference<Double> sumPoints = new AtomicReference<>(0D);
        AtomicReference<Double> bestScore = new AtomicReference<>(null);
        AtomicReference<Double> worstScore = new AtomicReference<>(null);
        AtomicReference<HashMap<Group, GroupActivityStatisticsCreator>> avgScoreCreators = new AtomicReference<>(new HashMap<>());
        AtomicReference<ScaleActivityStatisticsCreator> scaleScores = new AtomicReference<>(
                new ScaleActivityStatisticsCreator(activity));

        List<? extends TaskResult> results = getActivityResults(activity);
        results.forEach(
                result -> {
                    Double points = result.getPointsReceived();
                    if (activityIsSurvey) {
                        Integer rate = ((SurveyResult) result).getRate();
                        if (rate == null)
                            return;
                        points = Double.valueOf(rate);
                    }
                    answersNumber.incrementAndGet();
                    sumPoints.set(sumPoints.get() + points);
                    if (bestScore.get() == null)
                        bestScore.set(points);
                    else
                        bestScore.set(Math.max(bestScore.get(), points));
                    if (worstScore.get() == null)
                        worstScore.set(points);
                    else
                        worstScore.set(Math.min(worstScore.get(), points));

                    GroupActivityStatisticsCreator creator = avgScoreCreators.get().get(result.getMember().getGroup());
                    if (creator == null)
                        avgScoreCreators.get().put(result.getMember().getGroup(),
                                new GroupActivityStatisticsCreator(activity, result));
                    else
                        creator.add(result);

                    scaleScores.get().add(result);
                });
        response.setAnswersNumber(answersNumber.get());
        if (answersNumber.get() > 0) {
            response.setAvgPoints(sumPoints.get() / answersNumber.get());
            if (!activityIsSurvey) {
                response.setAvgPercentageResult(
                        100 * sumPoints.get() / (activity.getMaxPoints() * answersNumber.get()));
            }
        }
        response.setBestScore(bestScore.get());
        response.setWorstScore(worstScore.get());
        response.setAvgScores(avgScoreCreators.get().values()
                .stream()
                .map(GroupActivityStatisticsCreator::create)
                .toList());
        response.setScaleScores(scaleScores.get().create());
        return response;

    }

    private List<? extends TaskResult> getActivityResults(Activity activity) {
        if (activity.getActivityType().equals(ActivityType.SURVEY)) {
            return surveyResultRepository.findAllBySurvey((Survey) activity);
        }
        return getResultsForTask((Task) activity);
    }
}
