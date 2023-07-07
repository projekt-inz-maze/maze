package com.example.api.course.repository;

import com.example.api.course.model.Course;
import com.example.api.group.model.Group;
import com.example.api.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    boolean existsCourseByName(String name);
}
