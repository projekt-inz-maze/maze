package com.example.api.user.dto.response.rank;

import com.example.api.user.model.HeroType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RanksForHeroTypeResponse {
    private final HeroType heroType;
    private final List<RankResponse> ranks;
}
