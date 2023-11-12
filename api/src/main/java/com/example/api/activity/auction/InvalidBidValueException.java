package com.example.api.activity.auction;

public class InvalidBidValueException extends Exception {
    Auction auction;

    private final static String template = "min required bid is %f, max is %f";

    public InvalidBidValueException(Auction auction) {
        super(String.format(template, auction.currentMinBiddingValue(), auction.getMaxBidding()));
        this.auction = auction;
    }

}
