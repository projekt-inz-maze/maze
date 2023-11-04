package com.example.api.activity.auction;

import com.example.api.activity.task.Task;
import com.example.api.map.ActivityMap;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class AuctionService {
    AuctionRepository repository;
    public void createAuction(Task task, CreateAuctionDTO dto, ActivityMap map) {
        Auction auction = Auction.from(task)
                .minBidding(dto.getMinBidding())
                .resolutionDate(Instant.ofEpochMilli(dto.getResolutionDate()))
                .build();

        repository.save(auction);
        task.setAuction(auction);
        map.add(auction);
    }
}
