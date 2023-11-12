package com.example.api.activity.auction.bid;

import com.example.api.activity.auction.Auction;
import com.example.api.course.coursemember.CourseMember;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

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

    Double points;

    @CreationTimestamp
    Instant creationTime;

    public Bid(CourseMember courseMember, Auction auction, Double points) {
        this.courseMember = courseMember;
        this.auction = auction;
        this.points = points;
    }
}
