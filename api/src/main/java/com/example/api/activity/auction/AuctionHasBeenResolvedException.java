package com.example.api.activity.auction;

import java.time.LocalDateTime;

public class AuctionHasBeenResolvedException extends Exception {
    LocalDateTime resolutionDate;
    private final static String template = "Auction has ended %s";


    public AuctionHasBeenResolvedException(LocalDateTime resolutionDate) {
        super(String.format(template, resolutionDate.toString()));
        this.resolutionDate = resolutionDate;
    }
}
