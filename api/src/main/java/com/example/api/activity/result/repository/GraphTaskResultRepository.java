package com.example.api.activity.result.repository;

import com.example.api.activity.result.model.GraphTaskResult;
import com.example.api.activity.task.model.GraphTask;
import com.example.api.course.model.Course;
import com.example.api.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GraphTaskResultRepository extends JpaRepository<GraphTaskResult, Long> {
    GraphTaskResult findGraphTaskResultById(Long id);
    GraphTaskResult findGraphTaskResultByGraphTaskAndUser(GraphTask task, User user);
    List<GraphTaskResult> findAllByUser(User user);
    List<GraphTaskResult> findAllByUserAndCourse(User user, Course course);
    List<GraphTaskResult> findAllByGraphTask(GraphTask graphTask);
}
