package com.example.api.activity.result.service;

import com.example.api.activity.Activity;
import com.example.api.activity.ActivityRepository;
import com.example.api.activity.result.repository.ActivityResultRepository;
import com.example.api.activity.result.service.util.GroupActivityStatisticsCreator;
import com.example.api.activity.result.service.util.ScaleActivityStatisticsCreator;
import com.example.api.activity.survey.Survey;
import com.example.api.activity.result.dto.GetCSVForm;
import com.example.api.activity.task.dto.response.result.ActivityStatisticsResponse;
import com.example.api.activity.task.dto.response.result.TaskPointsStatisticsResponse;
import com.example.api.activity.task.filetask.FileTask;
import com.example.api.activity.task.graphtask.GraphTask;
import com.example.api.course.Course;
import com.example.api.course.CourseService;
import com.example.api.activity.ActivityType;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.activity.feedback.Feedback;
import com.example.api.activity.result.model.FileTaskResult;
import com.example.api.activity.result.model.GraphTaskResult;
import com.example.api.activity.result.model.SurveyResult;
import com.example.api.activity.result.model.ActivityResult;
import com.example.api.group.Group;
import com.example.api.user.model.AccountType;
import com.example.api.user.model.User;
import com.example.api.activity.feedback.ProfessorFeedbackRepository;
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
import java.util.stream.Collectors;
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
    private final CourseService courseService;
    private final ActivityResultRepository activityResultRepository;
    private final ActivityRepository activityRepository;

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

        List<Activity> activities = activityRepository.findAllById(activityIds);
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
        if (!activities.stream().allMatch(activity -> activity.getCourse().equals(activities.get(0).getCourse()))) {
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
            default -> throw new IllegalStateException();
        }
    }

    public List<? extends ActivityResult> getAllResultsForStudent(User student, Course course) {
        List<SurveyResult> surveyResults = surveyResultRepository.findAllByUserAndCourse(student, course);
        return Stream.of(getGraphAndFileResultsForStudent(student, course), surveyResults)
                .flatMap(Collection::stream)
                .toList();
    }

    public List<? extends ActivityResult> getGraphAndFileResultsForStudent(User student, Course course) {
        List<GraphTaskResult> graphTaskResults = graphTaskResultRepository.findAllByUserAndCourse(student, course);
        List<FileTaskResult> fileTaskResults = fileTaskResultRepository.findAllByMember_UserAndMember_Course(student, course);
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
        return activityResultRepository.findAllByMember_CourseAndMember_User(course, student)
                .stream()
                .filter(result -> result.activity != null)
                .map(TaskPointsStatisticsResponse::new)
                .sorted(((o1, o2) -> Long.compare(o2.getDateMillis(), o1.getDateMillis())))
                .toList();
    }

    public ActivityStatisticsResponse getActivityStatistics(Long activityID) throws WrongUserTypeException, EntityNotFoundException {
        userService.getCurrentUserAndValidateProfessorAccount();

        Activity activity = activityRepository.findById(activityID)
                .orElseThrow(() -> new EntityNotFoundException("Activity not found"));

        return getActivityStatisticsForActivity(activity);
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

    private ActivityStatisticsResponse getActivityStatisticsForActivity(Activity activity) {
        log.info("Getting statistics for activity {}", activity.getId());

        ActivityStatisticsResponse.ActivityStatisticsResponseBuilder responseBuilder = ActivityStatisticsResponse.builder();

        List<ActivityResult> results = activityResultRepository.findAllByActivity(activity);
        int resultCount = results.size();

        responseBuilder.answersNumber(resultCount);
        responseBuilder.activity100(activity.getMaxPoints());

        log.info("Calculating point values for activity {}", activity.getId());

        if (!results.isEmpty()) {
            List<Double> points = results.stream()
                    .map(result -> {
                        if (result instanceof SurveyResult) {
                            return ((SurveyResult) result).getRate().doubleValue();
                        } else {
                            return result.getPoints();
                        }
                    })
                    .toList();

            if (points.stream().anyMatch(Objects::isNull)) {
                return null;
            }

            Double sumPoints = points.stream().reduce(Double::sum).get();

            responseBuilder.avgPoints(sumPoints / resultCount);
            responseBuilder.avgPercentageResult(100 * sumPoints / (activity.getMaxPoints() * resultCount));
            responseBuilder.bestScore(points.stream().reduce(Double::max).get());
            responseBuilder.worstScore(points.stream().reduce(Double::max).get());
        }

        log.info("Getting score statistics for activity {}", activity.getId());
        Map<Group, GroupActivityStatisticsCreator> avgScoreCreators = results
                .stream()
                .collect(Collectors.toMap(result -> result.getMember().getGroup(),
                        result -> new GroupActivityStatisticsCreator(activity, result),
                        (existing, duplicate) -> existing));

        responseBuilder.avgScores(avgScoreCreators.values()
                .stream()
                .map(GroupActivityStatisticsCreator::create)
                .toList());

        ScaleActivityStatisticsCreator scaleScores = new ScaleActivityStatisticsCreator(activity);
        scaleScores.addAll(results);
        responseBuilder.scaleScores(scaleScores.create());

        log.info("Finished calculating statistics for activity {}", activity.getId());
        return responseBuilder.build();
    }
}
