package com.example.api.activity.result.repository;

import com.example.api.activity.result.model.SurveyResult;
import com.example.api.activity.task.model.Survey;
import com.example.api.course.model.Course;
import com.example.api.course.model.CourseMember;
import com.example.api.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyResultRepository extends JpaRepository<SurveyResult, Long> {
    List<SurveyResult> findAllByUser(User user);
    List<SurveyResult> findAllByMember(CourseMember member);
    List<SurveyResult> findAllByUserAndCourse(User user, Course course);
    SurveyResult findSurveyResultBySurveyAndUser(Survey survey, User user);
    List<SurveyResult> findAllBySurvey(Survey survey);

    @Query("SELECT sr FROM SurveyResult sr WHERE sr.survey.id = ?1")
    List<SurveyResult> findAllBySurveyId(Long surveyId);

    long countAllByUserAndCourse(User user, Course course);

    long countAllByMember(CourseMember member);
}
