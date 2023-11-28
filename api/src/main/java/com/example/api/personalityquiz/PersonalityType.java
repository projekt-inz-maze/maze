package com.example.api.personalityquiz;


public enum PersonalityType {
    SOCIALIZER("SOCIALIZER"),
    ACHIEVER("ACHIEVER"),
    EXPLORER("EXPLORER"),
    KILLER("KILLER");

    private final String personalityType;

    PersonalityType(String personalityType) {
        this.personalityType = personalityType;
    }

    public String toString() {
        return personalityType;
    }
}
