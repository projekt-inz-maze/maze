package com.example.api.user.repository;

import com.example.api.user.model.badge.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BadgeRepository extends JpaRepository<Badge, Long> {
    Badge findBadgeById(Long id);
}
