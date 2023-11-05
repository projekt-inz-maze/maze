package com.example.api.activity.auction.bid;

import com.example.api.activity.auction.Auction;
import com.example.api.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    User user;

    @ManyToOne
    Auction auction;

    Double value;

    Instant creationTime;

    public Bid(User user, Auction auction, Double value, Instant creationTime) {
        this.user = user;
        this.auction = auction;
        this.value = value;
        this.creationTime = creationTime;
    }
}
