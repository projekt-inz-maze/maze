package com.example.api.activity.repository.task;

import com.example.api.activity.task.model.GraphTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GraphTaskRepository extends JpaRepository<GraphTask, Long> {
    GraphTask findGraphTaskById(Long id);
    GraphTask findGraphTaskByTitle(String title);
    boolean existsById(long id);
}
