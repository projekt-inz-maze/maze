package com.example.api.activity.task.repository;

import com.example.api.activity.task.model.FileTask;
import com.example.api.course.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileTaskRepository extends JpaRepository<FileTask, Long> {
    FileTask findFileTaskById(Long id);
    FileTask findFileTaskByTitle(String title);
    List<FileTask> findFileTasksByCourse(Course course);
    List<FileTask> findAllByCourse(Course course);
    List<FileTask> findAllByCourseAndIsBlockedFalse(Course course);
}
