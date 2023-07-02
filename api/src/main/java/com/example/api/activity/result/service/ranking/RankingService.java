package com.example.api.activity.result.service.ranking;

import com.example.api.activity.result.dto.response.RankingResponse;
import com.example.api.activity.result.dto.response.SurveyAnswerResponse;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.MissingAttributeException;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.activity.result.model.FileTaskResult;
import com.example.api.activity.result.model.GraphTaskResult;
import com.example.api.activity.result.model.SurveyResult;
import com.example.api.activity.task.model.FileTask;
import com.example.api.activity.task.model.GraphTask;
import com.example.api.activity.task.model.Survey;
import com.example.api.user.model.AccountType;
import com.example.api.user.model.User;
import com.example.api.activity.result.repository.AdditionalPointsRepository;
import com.example.api.activity.result.repository.FileTaskResultRepository;
import com.example.api.activity.result.repository.GraphTaskResultRepository;
import com.example.api.activity.result.repository.SurveyResultRepository;
import com.example.api.activity.task.repository.FileTaskRepository;
import com.example.api.activity.task.repository.GraphTaskRepository;
import com.example.api.activity.task.repository.SurveyRepository;
import com.example.api.user.repository.UserRepository;
import com.example.api.security.AuthenticationService;
import com.example.api.user.service.RankService;
import com.example.api.user.service.UserService;
import com.example.api.validator.GroupValidator;
import com.example.api.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.DoubleStream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RankingService {
    private final UserRepository userRepository;
    private final GraphTaskResultRepository graphTaskResultRepository;
    private final GraphTaskRepository graphTaskRepository;
    private final FileTaskResultRepository fileTaskResultRepository;
    private final FileTaskRepository fileTaskRepository;
    private final SurveyResultRepository surveyResultRepository;
    private final SurveyRepository surveyRepository;
    private final AdditionalPointsRepository additionalPointsRepository;
    private final AuthenticationService authService;
    private final UserValidator userValidator;
    private final GroupValidator groupValidator;
    private final UserService userService;
    private final RankService rankService;


    public List<RankingResponse> getRanking() {
        List<RankingResponse> rankingList = userRepository.findAllByAccountTypeEquals(AccountType.STUDENT)
                .stream()
                .map(this::studentToRankingEntry)
                .sorted(Comparator.comparingDouble(RankingResponse::getPoints).reversed())
                .toList();

        addPositionToRankingList(rankingList);
        return rankingList;
    }

    public List<RankingResponse> getRankingForLoggedStudentGroup() {
        String groupName = userService.getUserGroup().getName();
        List<RankingResponse> rankingList = userRepository.findAllByAccountTypeEquals(AccountType.STUDENT)
                .stream()
                .filter(student -> Objects.equals(student.getGroup().getName(), groupName))
                .map(this::studentToRankingEntry)
                .sorted(Comparator.comparingDouble(RankingResponse::getPoints).reversed())
                .toList();

        addPositionToRankingList(rankingList);
        return rankingList;
    }

    public List<RankingResponse> getSearchedRanking(String search) {
        String searchLower = search.toLowerCase().replaceAll("\\s",""); // removing whitespaces
        List<RankingResponse> rankingList = userRepository.findAllByAccountTypeEquals(AccountType.STUDENT)
                .stream()
                .filter(student ->
                                student.getFirstName().concat(student.getLastName()).toLowerCase().replaceAll("\\s","").contains(searchLower) ||
                                student.getLastName().concat(student.getFirstName()).toLowerCase().replaceAll("\\s","").contains(searchLower) ||
                                student.getHeroType().getPolishTypeName().toLowerCase().contains(searchLower) ||
                                student.getGroup().getName().toLowerCase().contains(searchLower))
                .map(this::studentToRankingEntry)
                .sorted(Comparator.comparingDouble(RankingResponse::getPoints).reversed())
                .toList();

        addPositionToRankingList(rankingList);
        return rankingList;
    }

    public List<RankingResponse> getActivityRanking(Long activityID) throws WrongUserTypeException, EntityNotFoundException {
        String professorEmail = authService.getAuthentication().getName();
        User professor = userRepository.findUserByEmail(professorEmail);
        userValidator.validateProfessorAccount(professor, professorEmail);


        List<RankingResponse> rankingList =  userRepository.findAllByAccountTypeEquals(AccountType.STUDENT)
                        .stream()
                        .map(user -> {
                            SurveyAnswerResponse holder = new SurveyAnswerResponse();
                            Double points = getStudentPointsForActivity(activityID, user, holder);
                            return studentAndPointsToRankingEntry(user, points, holder);
                        })
                        .toList();
        addPositionToRankingList(rankingList);
        return rankingList;
    }

    public List<RankingResponse> getActivityRankingSearch(Long activityID, String search) throws WrongUserTypeException, EntityNotFoundException {
        String searchLower = search.toLowerCase().replaceAll("\\s",""); // removing whitespaces
        List<RankingResponse> rankingList = getActivityRanking(activityID)
                .stream()
                .filter(student ->
                            student.getFirstName().concat(student.getLastName()).toLowerCase().replaceAll("\\s","").contains(searchLower) ||
                            student.getLastName().concat(student.getFirstName()).toLowerCase().replaceAll("\\s","").contains(searchLower) ||
                            student.getHeroType().getPolishTypeName().toLowerCase().contains(searchLower) ||
                            student.getGroupName().toLowerCase().contains(searchLower)
                )
                .sorted(((o1, o2) -> {
                    if (o1.getPoints() == null && o2.getPoints() == null) {
                        return String.CASE_INSENSITIVE_ORDER.compare(o1.getLastName(), o2.getLastName());
                    }
                    else if (o2.getPoints() == null) return 1;
                    else if (o1.getPoints() == null) return -1;
                    else return Double.compare(o2.getPoints(), o1.getPoints());
                }))
                .toList();
        addPositionToRankingList(rankingList);
        return rankingList;

    }

    private Double getStudentPointsForActivity(Long activityID, User user, SurveyAnswerResponse surveyAnswerHolder) {
        GraphTask graphTask = graphTaskRepository.findGraphTaskById(activityID);
        if (graphTask != null) {
            GraphTaskResult result = graphTaskResultRepository.findGraphTaskResultByGraphTaskAndUser(graphTask, user);
            return result != null ? result.getPointsReceived() : null;
        }
        FileTask fileTask = fileTaskRepository.findFileTaskById(activityID);
        if (fileTask != null) {
            FileTaskResult result = fileTaskResultRepository.findFileTaskResultByFileTaskAndUser(fileTask, user);
            return result != null ? (result.isEvaluated() ? result.getPointsReceived() : null) : null;
        }
        Survey survey = surveyRepository.findSurveyById(activityID);
        if (survey != null) {
            SurveyResult result = surveyResultRepository.findSurveyResultBySurveyAndUser(survey, user);
            if (result == null) return null;
            surveyAnswerHolder.setAnswer(result.getFeedback());
            surveyAnswerHolder.setStudentPoints(result.getRate());
            return result.getPointsReceived();
        }
        return null;
    }

    private void addPositionToRankingList(List<RankingResponse> rankingResponses){
        AtomicInteger position = new AtomicInteger(1);
        rankingResponses.forEach(item -> item.setPosition(position.getAndIncrement()));
    }

    public Integer getRankingPosition() throws WrongUserTypeException, UsernameNotFoundException {
        String email = authService.getAuthentication().getName();
        User student = userRepository.findUserByEmail(email);
        userValidator.validateStudentAccount(student, email);
        return getPositionFromRanking(getRanking(), email);
    }

    public Integer getGroupRankingPosition() throws WrongUserTypeException, MissingAttributeException, UsernameNotFoundException, EntityNotFoundException {
        String email = authService.getAuthentication().getName();
        User student = userRepository.findUserByEmail(email);
        userValidator.validateStudentAccount(student, email);
        groupValidator.validateUserGroupIsNotNull(student);

        return getPositionFromRanking(getRankingForLoggedStudentGroup(), email);
    }

    private Integer getPositionFromRanking(List<RankingResponse> ranking, String email) throws UsernameNotFoundException {
        return ranking
                .stream()
                .filter(rankingResponse -> rankingResponse.getEmail().equals(email))
                .findAny()
                .map(RankingResponse::getPosition)
                .orElseThrow(() -> new UsernameNotFoundException("User" + email + " not found in database"));
    }

    private RankingResponse studentToRankingEntry(User student) {
        RankingResponse rankingResponse = new RankingResponse(student, rankService);
        rankingResponse.setPoints(getStudentPoints(student));
        return rankingResponse;
    }

    private RankingResponse studentAndPointsToRankingEntry(User student, Double points, SurveyAnswerResponse studentAnswer) {
        RankingResponse rankingResponse = new RankingResponse(student, rankService);
        rankingResponse.setPoints(points);
        if (studentAnswer.getStudentPoints() != null) {
            rankingResponse.setStudentAnswer(studentAnswer);
        }
        return rankingResponse;
    }

    private Double getGraphTaskPoints(User student) {
        return graphTaskResultRepository.findAllByUser(student)
                .stream()
                .mapToDouble(task -> {
                    try {
                        return task.getPointsReceived();
                    } catch (Exception e) {
                        log.info("GraphTaskResult with id {} not checked yet", task.getId());
                    }
                    return 0.0;
                })
                .sum();
    }

    private Double getFileTaskPoints(User student) {
        return fileTaskResultRepository.findAllByUser(student)
                .stream()
                .mapToDouble(task -> {
                    try {
                        return task.getPointsReceived();
                    } catch (Exception e) {
                        log.info("FileTaskResult with id {} not checked yet", task.getId());
                    }
                    return 0.0;
                }).sum();
    }

    private Double getAdditionalPoints(User student) {
        return additionalPointsRepository.findAllByUser(student)
                .stream()
                .mapToDouble(points -> {
                    try {
                        return points.getPointsReceived();
                    } catch (Exception e) {
                        log.info("AdditionalPoints with id {} has no points assigned", points.getId());
                    }
                    return 0.0;
                }).sum();
    }

    private Double getSurveyPoints(User student) {
        return surveyResultRepository.findAllByUser(student)
                .stream()
                .mapToDouble(survey -> {
                    try {
                        return survey.getPointsReceived();
                    } catch (Exception e) {
                        log.info("SurveyResult with id {} has no points assigned", survey.getId());
                    }
                    return 0.0;
                }).sum();
    }

    private Double getStudentPoints(User student) {
        Double graphTaskPoints = getGraphTaskPoints(student);
        Double fileTaskPoints = getFileTaskPoints(student);
        Double additionalPoints = getAdditionalPoints(student);
        Double surveyPoints = getSurveyPoints(student);
        return DoubleStream.of(graphTaskPoints, fileTaskPoints, additionalPoints, surveyPoints).sum();
    }

}
