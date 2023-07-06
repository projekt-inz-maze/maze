package com.example.api.activity.repository.feedback;

import com.example.api.activity.feedback.model.ProfessorFeedback;
import com.example.api.activity.result.model.FileTaskResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("resultProfessorFeedbackRepository")
public interface ProfessorFeedbackRepository extends JpaRepository<ProfessorFeedback, Long> {
    ProfessorFeedback findProfessorFeedbackByFileTaskResult(FileTaskResult result);
    ProfessorFeedback findProfessorFeedbackById(Long id);
}
