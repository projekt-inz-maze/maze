package com.example.api.activity.auction;

import com.example.api.activity.auction.bid.Bid;
import com.example.api.activity.auction.bid.BidDTO;
import com.example.api.activity.auction.bid.BidRepository;
import com.example.api.activity.task.Task;
import com.example.api.activity.task.TaskRepository;
import com.example.api.chapter.requirement.RequirementService;
import com.example.api.course.coursemember.CourseMember;
import com.example.api.course.StudentNotEnrolledException;
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
import java.util.Optional;

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
                .maxBidding(dto.getMaxBidding())
                .resolutionDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(dto.getResolutionDate()), ZoneOffset.systemDefault()))
                .minScoreToGetPoints(dto.getMinScoreToGetPoints())
                .requirements(requirementService.getDefaultRequirements(true))
                .build();

        repository.save(auction);
        task.setAuction(auction);
        map.add(auction);
    }

    public AuctionDTO getAuction(Long id) throws WrongUserTypeException, StudentNotEnrolledException {
        Auction auction = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Activity not found."));

        CourseMember courseMember = userService
                .getCurrentUserAndValidateStudentAccount()
                .getCourseMember(auction.getCourse(), true);

        Optional<Bid> previousBid = bidRepository.findByAuctionAndCourseMember(auction, courseMember);

        return new AuctionDTO(auction.getId(),
                auction.getHighestBid().map(Bid::getPoints),
                previousBid.map(Bid::getPoints),
                auction.currentMinBiddingValue(),
                auction.getMaxBidding(),
                courseMember.getPoints() + previousBid.map(Bid::getPoints).orElse(0D),
                auction.getResolutionDate().toEpochSecond(ZoneOffset.UTC),
                auction.getMinScoreToGetPoints()
                );
    }

    public void bidForAuction(BidDTO dto) throws InvalidBidValueException, WrongUserTypeException, StudentNotEnrolledException, AuctionHasBeenResolvedException {
        Auction auction = repository.findById(dto.auctionId()).orElseThrow(EntityNotFoundException::new);
        CourseMember courseMember = userService.getCurrentUserAndValidateStudentAccount().getCourseMember(auction.getCourse(), true);

        if (auction.currentMinBiddingValue() >= dto.bidValue() || auction.getMaxBidding() < dto.bidValue()) {
            throw new InvalidBidValueException(auction);
        }

        if (auction.isResolved()) {
            throw new AuctionHasBeenResolvedException(auction.getResolutionDate());
        }

        Bid bid = bidRepository.findByAuctionAndCourseMember(auction, courseMember)
                .orElseGet(() -> new Bid(courseMember, auction, 0D));

        courseMember.decreasePoints(dto.bidValue() - bid.getPoints());
        bid.setPoints(dto.bidValue());
        bidRepository.save(bid);
        auction.setHighestBid(bid);
        repository.save(auction);
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
        Bid highestBid =  auction.getHighestBid().get();
        CourseMember winner = highestBid.getMember();
        Task task = auction.getTask();
        auction.setDescription(AuctionMessageGenerator.winnerDescription(winner.getUser()));
        repository.save(auction);
        task.setIsBlocked(false);
        task.setRequirements(requirementService.requirementsForAuctionTask(auction, winner.getUser()));

        bidRepository.findAllByAuction(auction)
                .stream()
                .filter(bid -> !bid.getId().equals(highestBid.getId()))
                .forEach(bid -> bid.getMember().changePoints(bid.getPoints()));

        taskRepository.save(task);
    }
}
