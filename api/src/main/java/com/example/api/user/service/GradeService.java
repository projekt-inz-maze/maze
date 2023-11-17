package com.example.api.user.service;

import com.example.api.activity.result.model.*;
import com.example.api.course.Course;
import com.example.api.course.CourseService;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.user.dto.response.BasicUser;
import com.example.api.user.dto.response.grade.GradeResponse;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.user.model.User;
import com.example.api.activity.result.repository.AdditionalPointsRepository;
import com.example.api.activity.result.repository.FileTaskResultRepository;
import com.example.api.activity.result.repository.GraphTaskResultRepository;
import com.example.api.activity.result.repository.SurveyResultRepository;
import com.example.api.security.LoggedInUserService;
import com.example.api.validator.UserValidator;
import com.example.api.util.csv.PointsToGradeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GradeService {
    private final UserValidator userValidator;
    private final LoggedInUserService authService;
    private final GraphTaskResultRepository graphTaskResultRepository;
    private final FileTaskResultRepository fileTaskResultRepository;
    private final SurveyResultRepository surveyResultRepository;
    private final AdditionalPointsRepository additionalPointsRepository;
    private final CourseService courseService;

    public List<GradeResponse> getAllGrades(Long courseId) throws WrongUserTypeException, EntityNotFoundException {
        User professor = authService.getCurrentUser();
        userValidator.validateProfessorAccount(professor);
        Course course = courseService.getCourse(courseId);

        return course
                .getAllStudents()
                .stream()
                .map(student -> getStudentFinalGrade(student, course))
                .sorted(Comparator.comparing(entry -> entry.getStudent().getLastName().toLowerCase() + entry.getStudent().getFirstName().toLowerCase()))
                .toList();
    }

    public GradeResponse getStudentFinalGrade(User student, Course course) {
        List<GraphTaskResult> graphTaskResults = graphTaskResultRepository.findAllByUserAndCourse(student, course);
        List<FileTaskResult> fileTaskResults = fileTaskResultRepository.findAllByMember_UserAndCourse(student, course);
        List<SurveyResult> surveyResults = surveyResultRepository.findAllByUserAndCourse(student, course);
        List<? extends ActivityResult> results = Stream.of(graphTaskResults, fileTaskResults, surveyResults)
                .flatMap(Collection::stream)
                .filter(ActivityResult::isEvaluated)
                .toList();

        Double pointsReceived = results.stream()
                .mapToDouble(ActivityResult::getPoints)
                .sum();

        Double additionalPoints = additionalPointsRepository.findAllByUserAndCourse(student, course).stream()
                .mapToDouble(AdditionalPoints::getPoints)
                .sum();

        Double pointsPossibleToGet = results.stream()
                .mapToDouble(this::getMaxPointsFromTaskResult)
                .sum();

        pointsReceived = pointsReceived + additionalPoints;
        PointsToGradeMapper pointsToGradeMapper = new PointsToGradeMapper();
        Double finalGrade = pointsToGradeMapper.getGrade(pointsReceived, pointsPossibleToGet);
        return new GradeResponse(new BasicUser(student), finalGrade);
    }

    private Double getMaxPointsFromTaskResult(ActivityResult activityResult) {
        switch (activityResult.getActivity().getActivityType()) {
            case TASK -> {
                return ((FileTaskResult) activityResult).getFileTask().getMaxPoints();
            }
            case EXPEDITION -> {
                return ((GraphTaskResult) activityResult).getGraphTask().getMaxPoints();
            }
            case SURVEY -> {
                return ((SurveyResult) activityResult).getSurvey().getMaxPoints();
            }
        }
        return 0.0;
    }
}
