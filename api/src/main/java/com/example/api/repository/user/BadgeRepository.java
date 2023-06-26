package com.example.api.repository.user;

import com.example.api.model.user.badge.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BadgeRepository extends JpaRepository<Badge, Long> {
    Badge findBadgeById(Long id);
}
