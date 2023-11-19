package com.example.api.activity.task.submittask;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmitTaskRepository extends JpaRepository<SubmitTask, Long> {
    boolean existsByTitle(String title);
}
