package com.example.api.activity.auction.bid;

import com.example.api.activity.Activity;
import com.example.api.activity.auction.Auction;
import com.example.api.activity.result.model.ActivityResult;
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
public class Bid extends ActivityResult {

    public Bid(CourseMember member, Activity auction, Double points) {
        super(points, Instant.now().toEpochMilli(), member);
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

    public void returnPoints(Double taskResult) {
        if (getAuction().getMinScoreToGetPoints() <= taskResult) {
            member.changePoints(points);
            points = 0D;
        }
    }
}
