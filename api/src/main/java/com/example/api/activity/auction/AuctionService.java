package com.example.api.activity.auction;

import com.example.api.activity.task.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuctionService {
    AuctionRepository repository;
    public void createAuction(Task task, CreateAuctionDTO dto) {
        Auction auction = new Auction(task, dto.getMinBidding(), Instant.ofEpochMilli(dto.getResolutionDate()));
        repository.save(auction);
        task.setAuction(auction);
    }
}
