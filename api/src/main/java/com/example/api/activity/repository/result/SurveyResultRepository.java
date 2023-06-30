package com.example.api.activity.repository.result;

import com.example.api.activity.result.model.SurveyResult;
import com.example.api.activity.task.model.Survey;
import com.example.api.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyResultRepository extends JpaRepository<SurveyResult, Long> {
    List<SurveyResult> findAllByUser(User user);
    SurveyResult findSurveyResultById(Long id);
    SurveyResult findSurveyResultBySurveyAndUser(Survey survey, User user);
    List<SurveyResult> findAllBySurvey(Survey survey);
}
