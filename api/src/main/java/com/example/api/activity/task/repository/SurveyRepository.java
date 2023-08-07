package com.example.api.activity.task.repository;

import com.example.api.activity.task.model.Survey;
import com.example.api.course.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long> {
    Survey findSurveyById(Long id);
    List<Survey> findAllByCourse(Course course);
    List<Survey> findAllByCourseAndIsBlockedFalse(Course course);
}
