package com.example.api.repository.activity.task;

import com.example.api.model.activity.task.GraphTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GraphTaskRepository extends JpaRepository<GraphTask, Long> {
    GraphTask findGraphTaskById(Long id);
    GraphTask findGraphTaskByTitle(String title);
    boolean existsById(long id);
}
