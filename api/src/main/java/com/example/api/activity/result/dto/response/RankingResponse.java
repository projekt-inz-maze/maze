package com.example.api.activity.result.dto.response;

import com.example.api.course.coursemember.CourseMember;
import com.example.api.user.hero.HeroType;
import com.example.api.user.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

    public RankingResponse(String rankName, CourseMember courseMember, Double points){
        User user = courseMember.getUser();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.groupName = courseMember.getGroup().getName();
        this.heroType = courseMember.getHeroType();
        this.unblockedBadges = courseMember.getUnlockedBadges().size();
        this.rank = rankName;
        this.points = points;
    }
}
