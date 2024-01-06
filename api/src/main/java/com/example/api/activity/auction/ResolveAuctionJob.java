package com.example.api.activity.auction;

import com.example.api.activity.Activity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;


@Component
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ResolveAuctionJob {
    private final AuctionRepository auctionRepository;
    private final AuctionService auctionService;
    @Scheduled(cron = "0 0/5 * * * *")
    private void resolveAllAuctions() {
        List<Auction> auctions = auctionRepository.findAllByResolutionDateIsBeforeAndResolvedIsFalse(LocalDateTime.now());
        log.info("Running ResolveAuctionJob for auctions: {}", auctions.stream().map(Activity::getId).toList());
        auctions.forEach(auctionService::resolveAuction);
    }
}
