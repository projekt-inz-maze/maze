package com.example.api.user.badge;

import com.example.api.course.Course;
import com.example.api.user.badge.types.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BadgeRepository extends JpaRepository<Badge, Long> {
    Badge findBadgeById(Long id);

    List<Badge> findBadgesByCourse(Course course);
}
