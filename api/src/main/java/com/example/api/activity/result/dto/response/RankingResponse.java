package com.example.api.activity.result.dto.response;

import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.user.model.HeroType;
import com.example.api.user.model.Rank;
import com.example.api.user.model.User;
import com.example.api.user.service.RankService;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RankingResponse {
    @Schema(required = true) private String email;
    @Schema(required = true) private String firstName;
    @Schema(required = true) private String lastName;
    @Schema(required = true) private String groupName;
    @Schema(required = true) private HeroType heroType;
    @Schema(required = true) private Double points;
    @Schema(required = true) private Integer position;
    @Schema(required = true) private String rank;
    @Schema(required = true) private Integer unblockedBadges;
    @Schema(required = false) private SurveyAnswerResponse studentAnswer;

    public RankingResponse(User user, RankService rankService) throws EntityNotFoundException {
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.groupName = user.getGroup().getName();
        this.heroType = user.getHeroType();
        this.unblockedBadges = user.getUnlockedBadges().size();

        Rank rank = rankService.getCurrentRank(user);
        this.rank = rank != null ? rank.getName() : null;
    }
}
