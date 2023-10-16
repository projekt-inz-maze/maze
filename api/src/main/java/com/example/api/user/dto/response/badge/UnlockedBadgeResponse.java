package com.example.api.user.dto.response.badge;

import com.example.api.user.model.badge.UnlockedBadge;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UnlockedBadgeResponse {
    private BadgeResponse badge;
    private Long unlockedDateMillis;

    public UnlockedBadgeResponse(UnlockedBadge unlockedBadge) {
        this.badge = new BadgeResponse(unlockedBadge.getBadge());
        this.unlockedDateMillis = unlockedBadge.getUnlockedDateMillis();
    }
}
