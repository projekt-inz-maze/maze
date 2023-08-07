package com.example.api.activity.task.repository;

import com.example.api.activity.task.model.Info;
import com.example.api.course.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InfoRepository extends JpaRepository<Info, Long> {
    Info findInfoById(Long id);
    List<Info> findAllByCourseAndIsBlockedFalse(Course course);
}
