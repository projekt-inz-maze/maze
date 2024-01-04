package com.example.api.activity.auction;

import com.example.api.user.model.User;
import org.springframework.stereotype.Component;

@Component
public class AuctionMessageGenerator {

    public static String noBidsDescription() {
        return "Nikt nie wziął udziału w licytacji.";
    }
    public static String winnerDescription(User winner) {
        return String.format("Licytacja została wygrana przez użytkownika %s %s",
                winner.getFirstName(), winner.getLastName());
    }

    public static String auctionTitle(String taskTitle) {
        return "Licytacja - " + taskTitle;
    }
}
