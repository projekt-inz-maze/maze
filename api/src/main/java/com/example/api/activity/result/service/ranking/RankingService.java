package com.example.api.activity.result.service.ranking;

import com.example.api.activity.ActivityType;
import com.example.api.activity.result.dto.response.RankingResponse;
import com.example.api.activity.result.dto.response.SurveyAnswerResponse;
import com.example.api.activity.result.model.SurveyResult;
import com.example.api.activity.result.repository.AdditionalPointsRepository;
import com.example.api.activity.result.repository.FileTaskResultRepository;
import com.example.api.activity.result.repository.GraphTaskResultRepository;
import com.example.api.activity.result.repository.SurveyResultRepository;
import com.example.api.activity.result.service.ActivityResultService;
import com.example.api.course.Course;
import com.example.api.course.CourseService;
import com.example.api.course.CourseValidator;
import com.example.api.course.StudentNotEnrolledException;
import com.example.api.course.coursemember.CourseMember;
import com.example.api.course.coursemember.CourseMemberService;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.group.Group;
import com.example.api.security.LoggedInUserService;
import com.example.api.user.model.Rank;
import com.example.api.user.model.User;
import com.example.api.user.service.RankService;
import com.example.api.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.DoubleStream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RankingService {
    private final GraphTaskResultRepository graphTaskResultRepository;
    private final FileTaskResultRepository fileTaskResultRepository;
    private final SurveyResultRepository surveyResultRepository;
    private final AdditionalPointsRepository additionalPointsRepository;
    private final UserService userService;
    private final RankService rankService;
    private final CourseService courseService;
    private final CourseValidator courseValidator;
    private final ActivityResultService activityResultService;
    private final CourseMemberService courseMemberService;
    private final LoggedInUserService authService;

    public List<RankingResponse> getRanking(Long courseId) {
        List<RankingResponse> rankingList = courseMemberService.getAll(courseId)
                .stream()
                .map(member -> {
                    try {
                        return studentToRankingEntry(member);
                    } catch (EntityNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .sorted(Comparator.comparingDouble(RankingResponse::getPoints).reversed())
                .toList();

        addPositionToRankingList(rankingList);
        return rankingList;
    }

    public List<RankingResponse> getRankingForLoggedStudentGroup(Long courseId) throws StudentNotEnrolledException {
        CourseMember member = authService.getCurrentUser().getCourseMember(courseId).orElseThrow();
        Group group = userService.getCurrentUserGroup(courseId);
        List<RankingResponse> rankingList = group.getMembers()
                .stream()
                .map(student -> {
                    try {
                        return studentToRankingEntry(student);
                    } catch (EntityNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .sorted(Comparator.comparingDouble(RankingResponse::getPoints).reversed())
                .toList();

        addPositionToRankingList(rankingList);
        return rankingList;
    }

    public List<RankingResponse> getSearchedRanking(Long courseId, String search) throws EntityNotFoundException {
        String searchLower = search.toLowerCase().replaceAll("\\s","");

        Course course = courseService.getCourse(courseId);
        List<RankingResponse> rankingList = course
                .getAllMembers()
                .stream()
                .filter(member -> member.getUser().getFirstName().concat(member.getUser().getLastName()).toLowerCase().replaceAll("\\s","").contains(searchLower)
                        || member.getUser().getLastName().concat(member.getUser().getFirstName()).toLowerCase().replaceAll("\\s","").contains(searchLower)
                        || member.getHeroType().getPolishTypeName().toLowerCase().contains(searchLower)
                        || member.getGroup().getName().toLowerCase().contains(searchLower))
                .map(member -> {
                    try {
                        return studentToRankingEntry(member);
                    } catch (EntityNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .sorted(Comparator.comparingDouble(RankingResponse::getPoints).reversed())
                .toList();

        addPositionToRankingList(rankingList);
        return rankingList;
    }

    public List<RankingResponse> getActivityRanking(Long activityID) throws WrongUserTypeException {
        userService.getCurrentUserAndValidateProfessorAccount();

        List<RankingResponse> rankingList = activityResultService.getResultsForActivity(activityID)
                .stream()
                .map(result -> {
                    RankingResponse response = new RankingResponse(
                            rankService.getCurrentRank(result.getMember()).getName(),
                            result.getMember(),
                            result.getPoints());

                    if (result.getActivity().getActivityType().equals(ActivityType.SURVEY)) {
                        response.setStudentAnswer(new SurveyAnswerResponse((SurveyResult) result));
                    }
                    return response;
                })
                .sorted(Comparator.comparing(RankingResponse::getPoints).reversed())
                .toList();

        addPositionToRankingList(rankingList);
        return rankingList;
    }

    public List<RankingResponse> getActivityRankingSearch(Long activityID, String search) throws WrongUserTypeException {
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

    private void addPositionToRankingList(List<RankingResponse> rankingResponses){
        AtomicInteger position = new AtomicInteger(1);
        for (RankingResponse item : rankingResponses) {
            item.setPosition(position.getAndIncrement());
        }
    }

    public Integer getRankingPosition(Long courseId) throws WrongUserTypeException, UsernameNotFoundException {
        User student = userService.getCurrentUserAndValidateStudentAccount();
        return getPositionFromRanking(getRanking(courseId), student.getEmail());
    }

    public Integer getGroupRankingPosition(Long courseId) throws WrongUserTypeException, UsernameNotFoundException, EntityNotFoundException {
        User student = userService.getCurrentUserAndValidateStudentAccount();
        courseValidator.validateStudentCanAccess(student, courseId);

        return getPositionFromRanking(getRankingForLoggedStudentGroup(courseId), student.getEmail());
    }

    private Integer getPositionFromRanking(List<RankingResponse> ranking, String email) throws UsernameNotFoundException {
        return ranking
                .stream()
                .filter(rankingResponse -> rankingResponse.getEmail().equals(email))
                .findAny()
                .map(RankingResponse::getPosition)
                .orElseThrow(() -> new UsernameNotFoundException("User" + email + " not found in database"));
    }

    private RankingResponse studentToRankingEntry(CourseMember member) throws EntityNotFoundException {
        String rankName = Optional.ofNullable(rankService.getCurrentRank(member)).map(Rank::getName).orElse(null);
        return new RankingResponse(rankName, member, getStudentPoints(member));
    }

    private Double getGraphTaskPoints(CourseMember member) {
        return graphTaskResultRepository.findAllByMember(member)
                .stream()
                .flatMap(task -> Optional.ofNullable(task.getPoints()).stream())
                .mapToDouble(d -> d)
                .sum();
    }

    private Double getFileTaskPoints(CourseMember member) {
        return fileTaskResultRepository.findAllByMember(member)
                .stream()
                .flatMap(task -> Optional.ofNullable(task.getPoints()).stream())
                .mapToDouble(d -> d)
                .sum();
    }

    private Double getAdditionalPoints(CourseMember member) {
        return additionalPointsRepository.findAllByMember(member)
                .stream()
                .flatMap(task -> Optional.ofNullable(task.getPoints()).stream())
                .mapToDouble(d -> d)
                .sum();
    }

    private Double getSurveyPoints(CourseMember member) {
        return surveyResultRepository.findAllByMember(member)
                .stream()
                .flatMap(task -> Optional.ofNullable(task.getPoints()).stream())
                .mapToDouble(d -> d)
                .sum();
    }

    private Double getStudentPoints(CourseMember student) {
        Double graphTaskPoints = getGraphTaskPoints(student);
        Double fileTaskPoints = getFileTaskPoints(student);
        Double additionalPoints = getAdditionalPoints(student);
        Double surveyPoints = getSurveyPoints(student);
        return DoubleStream.of(graphTaskPoints, fileTaskPoints, additionalPoints, surveyPoints).sum();
    }

}
