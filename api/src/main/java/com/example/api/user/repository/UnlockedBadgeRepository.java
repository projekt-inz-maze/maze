package com.example.api.user.repository;

import com.example.api.user.model.badge.UnlockedBadge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnlockedBadgeRepository extends JpaRepository<UnlockedBadge, Long> {
}
