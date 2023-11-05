package com.example.api.activity.auction;

import com.example.api.activity.auction.bid.Bid;
import com.example.api.activity.auction.bid.BidDTO;
import com.example.api.activity.auction.bid.BidRepository;
import com.example.api.activity.task.Task;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.map.ActivityMap;
import com.example.api.user.model.User;
import com.example.api.user.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.Instant;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class AuctionService {
    AuctionRepository repository;
    UserService userService;
    BidRepository bidRepository;
    public void createAuction(Task task, CreateAuctionDTO dto, ActivityMap map) {
        Auction auction = Auction.from(task)
                .minBidding(dto.getMinBidding())
                .resolutionDate(Instant.ofEpochMilli(dto.getResolutionDate()))
                .build();

        repository.save(auction);
        task.setAuction(auction);
        map.add(auction);
    }

    public void bidForAuction(BidDTO dto) throws TooLowBidException, WrongUserTypeException {
        Auction auction = repository.findById(dto.auctionId()).orElseThrow(EntityNotFoundException::new);

        if (auction.currentMinBiddingValue() >= dto.bidValue()) {
            throw new TooLowBidException(auction.currentMinBiddingValue());
        }

        User student = userService.getCurrentUserAndValidateStudentAccount();

        Bid bid = new Bid(student, auction, dto.bidValue(), Instant.now());
        bidRepository.save(bid);
        auction.setHighestBid(bid);
    }
}
