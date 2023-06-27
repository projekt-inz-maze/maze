package com.example.api.util.model;

public enum ImageType {
    CHAPTER("CHAPTER"),
    RANK("RANK"),
    BADGE("BADGE");

    private final String type;

    ImageType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
