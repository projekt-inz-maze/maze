package com.example.api.activity.auction;

import com.example.api.activity.auction.bid.Bid;
import com.example.api.activity.auction.bid.BidDTO;
import com.example.api.activity.auction.bid.BidRepository;
import com.example.api.activity.task.Task;
import com.example.api.chapter.requirement.model.StudentsRequirement;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.map.ActivityMap;
import com.example.api.user.model.User;
import com.example.api.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuctionService {
    private final AuctionRepository repository;
    private final UserService userService;
    private final BidRepository bidRepository;
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

    public void resolveAuction(Auction auction) {

        if (auction.getHighestBid().isPresent()) {
            resolveHighestBid(auction);
        } else {
            resolveNoBids(auction);
        }

        auction.setResolved(true);
    }

    private void resolveNoBids(Auction auction) {
        auction.setDescription("Nikt nie wziął udziału w aukcji.");
    }

    private void resolveHighestBid(Auction auction) {
        User auctionWinner = auction.getHighestBid().map(Bid::getUser).get();
        Task task = auction.getTask();

        task.setIsBlocked(false);
        task.getRequirements().add(new StudentsRequirement(auction.getTitle(), true, List.of(auctionWinner)));
    }
}
