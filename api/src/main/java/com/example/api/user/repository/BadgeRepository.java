package com.example.api.user.repository;

import com.example.api.course.model.Course;
import com.example.api.user.model.badge.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BadgeRepository extends JpaRepository<Badge, Long> {
    Badge findBadgeById(Long id);

    List<Badge> findBadgesByCourse(Course course);
}
