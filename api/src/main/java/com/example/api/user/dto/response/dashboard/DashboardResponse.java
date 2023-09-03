package com.example.api.user.dto.response.dashboard;

import com.example.api.user.hero.HeroStatsDTO;
import com.example.api.user.hero.HeroTypeStatsDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@AllArgsConstructor
@Getter
@Setter
public class DashboardResponse {
    private HeroTypeStatsDTO heroTypeStatsDTO;
    private GeneralStats generalStats;
    private List<LastAddedActivity> lastAddedActivities;
    private HeroStatsDTO heroStatsDTO;

}
