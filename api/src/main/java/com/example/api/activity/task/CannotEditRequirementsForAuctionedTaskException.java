package com.example.api.activity.task;

public class CannotEditRequirementsForAuctionedTaskException extends Exception {

    public final String message = "Cannot edit requirements for auctioned task. Requirements will update automatically after auction closes.";
    public CannotEditRequirementsForAuctionedTaskException() {
    }
}
