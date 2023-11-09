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
import java.time.ZoneId;
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
                .resolutionDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(dto.getResolutionDate()), ZoneOffset.systemDefault()))
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

        Optional<Bid> bid = bidRepository.findByAuctionAndCourseMember(auction, courseMember);

        return new AuctionDTO(auction.getId(),
                auction.getMinBidding(),
                courseMember.getPoints(),
                bid.map(Bid::getValue),
                auction.getResolutionDate().toEpochSecond((ZoneOffset) ZoneId.systemDefault()));
    }

    public void bidForAuction(BidDTO dto) throws TooLowBidException, WrongUserTypeException, StudentNotEnrolledException, AuctionHasBeenResolvedException {
        Auction auction = repository.findById(dto.auctionId()).orElseThrow(EntityNotFoundException::new);
        CourseMember courseMember = userService.getCurrentUserAndValidateStudentAccount().getCourseMember(auction.getCourse(), true);

        if (auction.currentMinBiddingValue() >= dto.bidValue()) {
            throw new TooLowBidException(auction.currentMinBiddingValue());
        }

        if (auction.isResolved()) {
            throw new AuctionHasBeenResolvedException(auction.getResolutionDate());
        }

        Bid bid = bidRepository.findByAuctionAndCourseMember(auction, courseMember)
                .orElseGet(() -> new Bid(courseMember, auction, 0D));

        courseMember.decreasePoints(dto.bidValue() - bid.getValue());
        bid.setValue(dto.bidValue());
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
        CourseMember winner = highestBid.getCourseMember();
        Task task = auction.getTask();
        auction.setDescription(AuctionMessageGenerator.winnerDescription(winner.getUser()));
        repository.save(auction);
        task.setIsBlocked(false);
        task.setRequirements(requirementService.requirementsForAuctionTask(auction, winner.getUser()));

        bidRepository.findAllByAuction(auction)
                .stream()
                .filter(bid -> !bid.getId().equals(highestBid.getId()))
                .forEach(bid -> bid.getCourseMember().changePoints(bid.getValue()));

        taskRepository.save(task);
    }
}
