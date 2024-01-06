package com.example.api.activity.task.filetask;

import com.example.api.course.Course;
import com.example.api.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileTaskRepository extends JpaRepository<FileTask, Long> {
    FileTask findFileTaskById(Long id);
    FileTask findFileTaskByTitle(String title);
    List<FileTask> findFileTasksByCourse(Course course);
    List<FileTask> findAllByProfessor(User user);
    List<FileTask> findAllByCourse(Course course);
    List<FileTask> findAllByCourseAndProfessor(Course course, User user);
    List<FileTask> findAllByCourseAndIsBlockedFalse(Course course);
}
