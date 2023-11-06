package com.example.api.activity.auction;

import com.example.api.activity.auction.bid.BidDTO;
import com.example.api.error.exception.WrongUserTypeException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auction")
@SecurityRequirement(name = "JWT_AUTH")
public class AuctionController {
    private final AuctionService auctionService;
    @PostMapping
    public ResponseEntity<?> bidForAuction(@RequestBody BidDTO bid) throws TooLowBidException, WrongUserTypeException {
        auctionService.bidForAuction(bid);
        return ResponseEntity.ok().body(null);
    }
}
