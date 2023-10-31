package com.example.api.activity.auction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuctionRepository  extends JpaRepository<Auction, Long> {
}
