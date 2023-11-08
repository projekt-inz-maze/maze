package com.example.api.activity.auction;

import com.example.api.activity.auction.bid.Bid;
import com.example.api.activity.auction.bid.BidDTO;
import com.example.api.activity.auction.bid.BidRepository;
import com.example.api.activity.task.Task;
import com.example.api.activity.task.TaskRepository;
import com.example.api.chapter.requirement.RequirementService;
import com.example.api.course.model.CourseMember;
import com.example.api.course.validator.exception.StudentNotEnrolledException;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.map.ActivityMap;
import com.example.api.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuctionService {
    private final AuctionRepository repository;
    private final UserService userService;
    private final BidRepository bidRepository;
    private final RequirementService requirementService;
    private final TaskRepository taskRepository;

    public void createAuction(Task task, CreateAuctionDTO dto, ActivityMap map) {
        Auction auction = Auction.from(task)
                .minBidding(dto.getMinBidding())
                .resolutionDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(dto.getResolutionDate()), ZoneOffset.systemDefault()))
                .build();

        repository.save(auction);
        task.setAuction(auction);
        map.add(auction);
    }

    public void bidForAuction(BidDTO dto) throws TooLowBidException, WrongUserTypeException, StudentNotEnrolledException {
        Auction auction = repository.findById(dto.auctionId()).orElseThrow(EntityNotFoundException::new);
        CourseMember courseMember = userService.getCurrentUserAndValidateStudentAccount().getCourseMember(auction.getCourse(), true);

        if (auction.currentMinBiddingValue() >= dto.bidValue()) {
            throw new TooLowBidException(auction.currentMinBiddingValue());
        }


        Bid bid = new Bid(courseMember, auction, dto.bidValue());
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
        auction.setDescription(AuctionMessageGenerator.noBidsDescription());
        repository.save(auction);
    }

    private void resolveHighestBid(Auction auction) {
        CourseMember winner = auction.getHighestBid().map(Bid::getCourseMember).get();
        Task task = auction.getTask();
        auction.setDescription(AuctionMessageGenerator.winnerDescription(winner.getUser()));
        repository.save(auction);
        task.setIsBlocked(false);
        task.setRequirements(requirementService.requirementsForAuctionTask(auction, winner.getUser()));
        taskRepository.save(task);
    }
}
