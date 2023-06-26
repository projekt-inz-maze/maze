package com.example.api.repository.activity.task;

import com.example.api.model.activity.task.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long> {
    Survey findSurveyById(Long id);
    boolean existsById(long id);
}
