package com.example.api.activity.task.repository;

import com.example.api.activity.task.model.GraphTask;
import com.example.api.course.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GraphTaskRepository extends JpaRepository<GraphTask, Long> {
    GraphTask findGraphTaskById(Long id);
    GraphTask findGraphTaskByTitle(String title);
    List<GraphTask> findAllByCourse(Course course);
}
