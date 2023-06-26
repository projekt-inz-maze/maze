package com.example.api.repository.user;

import com.example.api.model.user.badge.UnlockedBadge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnlockedBadgeRepository extends JpaRepository<UnlockedBadge, Long> {
}
