package com.example.api.activity.result.repository;

import com.example.api.activity.result.model.TaskResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskResultRepository extends JpaRepository<TaskResult, Long> {
}
