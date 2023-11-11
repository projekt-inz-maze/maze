package com.example.api.course;

import com.example.api.course.Course;
import com.example.api.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    boolean existsCourseByName(String name);
    boolean existsCourseByIdIsAndOwnerIs(Long courseId, User user);
}
