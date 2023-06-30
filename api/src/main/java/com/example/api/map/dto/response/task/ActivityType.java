package com.example.api.map.dto.response.task;


public enum ActivityType {
    EXPEDITION("EXPEDITION"),
    TASK("TASK"),
    INFO("INFORMATION"),
    SURVEY("SURVEY"),
    ADDITIONAL("ADDITIONAL");

    private final String activityType;

    ActivityType(String activityType){
        this.activityType = activityType;
    }

    public String getActivityType() {
        return activityType;
    }

}
