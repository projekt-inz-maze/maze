package com.example.api.activity.result.service;

import com.example.api.activity.Activity;
import com.example.api.activity.ActivityService;
import com.example.api.activity.result.service.util.GroupActivityStatisticsCreator;
import com.example.api.activity.result.service.util.ScaleActivityStatisticsCreator;
import com.example.api.activity.survey.Survey;
import com.example.api.activity.task.Task;
import com.example.api.activity.result.dto.GetCSVForm;
import com.example.api.activity.task.dto.response.result.ActivityStatisticsResponse;
import com.example.api.activity.task.dto.response.result.TaskPointsStatisticsResponse;
import com.example.api.activity.task.filetask.FileTask;
import com.example.api.activity.task.graphtask.GraphTask;
import com.example.api.course.model.Course;
import com.example.api.course.service.CourseService;
import com.example.api.activity.ActivityType;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.activity.feedback.model.Feedback;
import com.example.api.activity.result.model.FileTaskResult;
import com.example.api.activity.result.model.GraphTaskResult;
import com.example.api.activity.result.model.SurveyResult;
import com.example.api.activity.result.model.TaskResult;
import com.example.api.group.Group;
import com.example.api.user.model.AccountType;
import com.example.api.user.model.User;
import com.example.api.activity.feedback.repository.ProfessorFeedbackRepository;
import com.example.api.activity.result.repository.FileTaskResultRepository;
import com.example.api.activity.result.repository.GraphTaskResultRepository;
import com.example.api.activity.result.repository.SurveyResultRepository;
import com.example.api.activity.task.filetask.FileTaskRepository;
import com.example.api.activity.task.graphtask.GraphTaskRepository;
import com.example.api.activity.survey.SurveyRepository;
import com.example.api.user.repository.UserRepository;
import com.example.api.user.service.UserService;
import com.example.api.util.csv.CSVConverter;
import com.example.api.util.csv.CSVTaskResult;
import com.sun.jdi.request.InvalidRequestStateException;
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
        List<User> students = userRepository.findAllByIdIsInAndAccountTypeIs(studentIds, AccountType.STUDENT);



        Map<User, List<CSVTaskResult>> userToResultMap = new HashMap<>();

        List<String> firstRow = new LinkedList<>(
                List.of("Imię", "Nazwisko", "NumerId", "Instytucja", "Wydział", "E-mail"));
        Map<Long, GraphTask> formToGraphTaskMap = new HashMap<>();
        Map<Long, FileTask> formToFileTaskMap = new HashMap<>();
        Map<Long, Survey> formToSurveyMap = new HashMap<>();
        fillFirstRowAndAddTasksToMap(activityIds, formToGraphTaskMap, formToFileTaskMap, formToSurveyMap, firstRow);

        List<Activity> activities = getActivities(activityIds);
        validateSameCourse(activities);

        students.forEach(student -> {
            List<CSVTaskResult> csvTaskResults = activities.stream()
                    .map(activity -> getCSVTaskResultForActivity(student, activity, formToGraphTaskMap, formToFileTaskMap, formToSurveyMap))
                    .toList();

            userToResultMap.put(student, csvTaskResults);
        });
        return new ByteArrayResource(csvConverter.convertToByteArray(userToResultMap, firstRow, activities.get(0).getCourse()));
    }

    private void validateSameCourse(List<Activity> activities) {
        if (!activities.stream().allMatch(activity -> activity.getCourse().equals(activities.get(0)))) {
            String msg = "Cannot get csv for activities from different courses";
            log.error(msg);
            throw new InvalidRequestStateException(msg);
        }
    }

    private CSVTaskResult getCSVTaskResultForActivity(User student, Activity activity, Map<Long, GraphTask> formToGraphTaskMap, Map<Long, FileTask> formToFileTaskMap, Map<Long, Survey> formToSurveyMap) {
        ActivityType type = activity.getActivityType();
        switch (type) {
            case EXPEDITION -> {
                GraphTask graphTask = formToGraphTaskMap.get(activity.getId());
                GraphTaskResult graphTaskResult = graphTaskResultRepository
                        .findGraphTaskResultByGraphTaskAndUser(graphTask, student);
                return new CSVTaskResult(graphTaskResult);
            }
            case TASK -> {
                FileTask fileTask = formToFileTaskMap.get(activity.getId());
                FileTaskResult fileTaskResult = fileTaskResultRepository
                        .findFileTaskResultByFileTaskAndUser(fileTask, student);
                Feedback feedback = professorFeedbackRepository
                        .findProfessorFeedbackByFileTaskResult(fileTaskResult);
                return new CSVTaskResult(fileTaskResult, feedback);
            }
            case SURVEY -> {
                Survey survey = formToSurveyMap.get(activity.getId());
                SurveyResult surveyResult = surveyResultRepository.findSurveyResultBySurveyAndUser(survey,
                        student);
                return new CSVTaskResult(surveyResult);
            }
            default -> {
                throw new IllegalStateException();
            }
        }
    }

    public List<? extends TaskResult> getAllResultsForStudent(User student, Course course) {
        List<SurveyResult> surveyResults = surveyResultRepository.findAllByUserAndCourse(student, course);
        return Stream.of(getGraphAndFileResultsForStudent(student, course), surveyResults)
                .flatMap(Collection::stream)
                .toList();
    }

    public List<? extends TaskResult> getGraphAndFileResultsForStudent(User student, Course course) {
        List<GraphTaskResult> graphTaskResults = graphTaskResultRepository.findAllByUserAndCourse(student, course);
        List<FileTaskResult> fileTaskResults = fileTaskResultRepository.findAllByMember_UserAndCourse(student, course);
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
        List<TaskPointsStatisticsResponse> fileTaskResults = fileTaskResultRepository.findAllByMember_UserAndCourse(student, course)
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
                .map(this::getActivityStatisticsForActivity)
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

    private List<Activity> getActivities(List<Long> activityIds) {
        return (List<Activity>) Stream.of(graphTaskRepository.findGraphTaskByIdIn(activityIds),
                fileTaskRepository.findFileTaskByIdIn(activityIds),
                surveyRepository.findSurveyByIdIn(activityIds)
        )
                .flatMap(Collection::stream)
                .toList();
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
