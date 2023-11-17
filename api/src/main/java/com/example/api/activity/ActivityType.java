package com.example.api.activity;


public enum ActivityType {
    EXPEDITION("EXPEDITION"),
    TASK("TASK"),
    INFO("INFORMATION"),
    SURVEY("SURVEY"),
    AUCTION("AUCTION"),
    ADDITIONAL("ADDITIONAL");

    private final String activityType;

    ActivityType(String activityType){
        this.activityType = activityType;
    }

    public String getActivityType() {
        return activityType;
    }

}
