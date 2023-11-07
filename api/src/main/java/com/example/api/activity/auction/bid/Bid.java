package com.example.api.activity.auction.bid;

import com.example.api.activity.auction.Auction;
import com.example.api.course.model.CourseMember;
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
    CourseMember courseMember;

    @ManyToOne
    Auction auction;

    Double value;

    Instant creationTime;

    public Bid(CourseMember courseMember, Auction auction, Double value, Instant creationTime) {
        this.courseMember = courseMember;
        this.auction = auction;
        this.value = value;
        this.creationTime = creationTime;
    }
}
