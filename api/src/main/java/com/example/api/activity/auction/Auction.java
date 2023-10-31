package com.example.api.activity.auction;

import com.example.api.activity.Activity;
import com.example.api.activity.task.Task;
import com.example.api.map.dto.response.task.ActivityType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.time.Instant;

/*
1. dodawanie Auction podczas dodawania taska
1.1 schedule auction resolve
1.2 zablokowanie odblokowania Tasku, który ma Auction
2. dodanie Biddingu
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Auction extends Activity {
    private final ActivityType activityType = ActivityType.AUCTION;
    @OneToOne
    private Task task;
    private Double minBidding;
    private Instant resolutionDate;

    @Override
    public Double getMaxPoints() {
        return 0D;
    }
}
