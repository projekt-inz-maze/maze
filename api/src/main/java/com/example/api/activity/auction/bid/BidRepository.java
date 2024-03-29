package com.example.api.activity.auction.bid;

import com.example.api.activity.Activity;
import com.example.api.course.coursemember.CourseMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    Optional<Bid> findByActivityAndMember(Activity auction, CourseMember member);
    List<Bid> findAllByActivity(Activity auction);
}
