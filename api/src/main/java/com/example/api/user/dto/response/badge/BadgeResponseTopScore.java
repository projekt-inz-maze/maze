package com.example.api.user.dto.response.badge;

import com.example.api.user.model.badge.TopScoreBadge;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BadgeResponseTopScore extends BadgeResponse<Double>{
    private Boolean forGroup;

    public BadgeResponseTopScore(TopScoreBadge badge) {
        super(badge);
        this.forGroup = badge.getForGroup();
    }
}
