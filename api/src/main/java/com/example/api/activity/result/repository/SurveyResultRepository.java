package com.example.api.activity.result.repository;

import com.example.api.activity.Activity;
import com.example.api.activity.result.model.SurveyResult;
import com.example.api.activity.survey.Survey;
import com.example.api.course.Course;
import com.example.api.course.coursemember.CourseMember;
import com.example.api.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyResultRepository extends JpaRepository<SurveyResult, Long> {
    List<SurveyResult> findAllByMember(CourseMember member);

    @Query("SELECT sr FROM SurveyResult sr WHERE sr.member.user = ?1 AND sr.course = ?2")
    List<SurveyResult> findAllByUserAndCourse(User user, Course course);
    @Query("SELECT sr FROM SurveyResult sr WHERE sr.activity = ?1 AND sr.member.user = ?2")
    SurveyResult findSurveyResultBySurveyAndUser(Survey survey, User user);
    List<SurveyResult> findAllByActivity(Activity survey);

    @Query("SELECT sr FROM SurveyResult sr WHERE sr.activity.id = ?1")
    List<SurveyResult> findAllBySurveyId(Long surveyId);

    long countAllByMember_UserAndCourse(User user, Course course);
    long countAllByMember(CourseMember member);
}
