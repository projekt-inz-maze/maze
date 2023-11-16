package com.example.api.activity.auction.bid;

import com.example.api.activity.Activity;
import com.example.api.activity.auction.Auction;
import com.example.api.activity.result.model.TaskResult;
import com.example.api.course.coursemember.CourseMember;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Slf4j
public class Bid extends TaskResult {

    public Bid(CourseMember member, Activity auction, Double points) {
        super(points, Instant.now().toEpochMilli(), auction.getCourse(), member);
        this.activity = auction;
    }

    @Override
    public boolean isEvaluated() {
        return getAuction().isResolved();
    }

    public Auction getAuction() {
        return (Auction) activity;
    }

    @Override
    public void setPoints(Double points) {
        if (this.points == null) {
            member.decreasePoints(points);
            this.points = points;
        } else {
            member.decreasePoints(points - this.points);
            this.points = points;
        }
    }
}
