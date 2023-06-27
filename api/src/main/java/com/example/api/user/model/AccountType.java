package com.example.api.user.model;

public enum AccountType {
    STUDENT("STUDENT"),
    PROFESSOR("PROFESSOR"),
    ADMIN("ADMIN");

    private final String name;

    AccountType(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
