package com.example.api.activity.task.graphtask;

import com.example.api.course.model.Course;
import com.example.api.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GraphTaskRepository extends JpaRepository<GraphTask, Long> {
    GraphTask findGraphTaskById(Long id);
    List<GraphTask> findGraphTaskByIdIn(List<Long> id);
    GraphTask findGraphTaskByTitle(String title);
    List<GraphTask> findAllByCourse(Course course);
    List<GraphTask> findAllByCourseAndProfessor(Course course, User user);
    List<GraphTask> findAllByCourseAndIsBlockedFalse(Course course);
}
