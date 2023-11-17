package com.example.api.chapter.requirement.model;

public enum RequirementValueType {
    DATE("date"),
    NUMBER("number"),
    MULTI_SELECT("multi_select");

    private final String type;

    RequirementValueType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
