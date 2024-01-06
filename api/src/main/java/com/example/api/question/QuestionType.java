package com.example.api.question;

public enum QuestionType {
    OPENED("OPENED"),
    SINGLE_CHOICE("SINGLE_CHOICE"),
    MULTIPLE_CHOICE("MULTIPLE_CHOICE");

    private final String type;

    QuestionType(String type) {
        this.type = type;
    }

    public String getType(){
        return type;
    }
}
