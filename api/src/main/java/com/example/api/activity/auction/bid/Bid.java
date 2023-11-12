package com.example.api.activity.auction.bid;

import com.example.api.activity.Activity;
import com.example.api.activity.auction.Auction;
import com.example.api.activity.result.model.TaskResult;
import com.example.api.course.coursemember.CourseMember;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.MissingAttributeException;
import com.example.api.error.exception.WrongUserTypeException;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Bid extends TaskResult {
    public Bid(CourseMember member, Auction auction, Double points) {
        super(points, Instant.now().toEpochMilli(), auction.getCourse(), member);
        this.activity = auction;
    }

    @Override
    public boolean isEvaluated() {
        return true;
    }

    public Auction getAuction() {
        return (Auction) activity;
    }
}
