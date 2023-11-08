package com.example.api.activity.auction;

import com.example.api.user.model.User;
import org.springframework.stereotype.Component;

@Component
public class AuctionMessageGenerator {

    public static String noBidsDescription() {
        return "Nikt nie wziął udziału w aukcji.";
    }
    public static String winnerDescription(User winner) {
        return String.format("Licytację wygrał/a %s %s", winner.getFirstName(), winner.getLastName());
    }
}
