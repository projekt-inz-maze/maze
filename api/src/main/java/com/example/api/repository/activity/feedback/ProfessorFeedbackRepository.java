package com.example.api.repository.activity.feedback;

import com.example.api.model.activity.feedback.ProfessorFeedback;
import com.example.api.model.activity.result.FileTaskResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfessorFeedbackRepository extends JpaRepository<ProfessorFeedback, Long> {
    ProfessorFeedback findProfessorFeedbackByFileTaskResult(FileTaskResult result);
    ProfessorFeedback findProfessorFeedbackById(Long id);
}
