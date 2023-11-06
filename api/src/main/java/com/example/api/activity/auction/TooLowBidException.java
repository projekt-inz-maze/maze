package com.example.api.activity.auction;

public class TooLowBidException extends Exception {
    Double lowestBidValue;

    private final static String template = "min required bid is %f";

    public TooLowBidException(Double lowestBidValue) {
        super(String.format(template, lowestBidValue));
        this.lowestBidValue = lowestBidValue;
    }

}