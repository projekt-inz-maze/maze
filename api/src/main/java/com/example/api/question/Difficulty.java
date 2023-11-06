package com.example.api.question;

public enum Difficulty {
    EASY("EASY"),
    MEDIUM("MEDIUM"),
    HARD("HARD");

    private final String difficulty;

    Difficulty(String difficulty){
        this.difficulty = difficulty;
    }

    public String getDifficulty(){
        return difficulty;
    }
}
