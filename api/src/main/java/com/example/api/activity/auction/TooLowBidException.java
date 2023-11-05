package com.example.api.activity.auction;

public class TooLowBidException extends Exception {
    Double lowestBidValue;

    private static String template = "min required bid is {0}";

    public TooLowBidException(Double lowestBidValue) {
        super(String.format(template, lowestBidValue));
        this.lowestBidValue = lowestBidValue;
    }

}
