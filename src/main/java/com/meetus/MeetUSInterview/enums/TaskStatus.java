package com.meetus.MeetUSInterview.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


public enum TaskStatus {
    OPEN("open"),
    DONE("done");

    private final String stringValue;

    TaskStatus(String stringValue) {
        this.stringValue = stringValue;
    }

    @JsonValue
    public String getStringValue() {
        return stringValue;
    }

    @JsonCreator
    public static TaskStatus fromString(String stringValue) {
        if (stringValue == null) {
            throw new IllegalArgumentException("Task status cannot be null");
        }
        
        String normalizedValue = stringValue.trim().toLowerCase();
        switch (normalizedValue) {
            case "open":
                return OPEN;
            case "done":
                return DONE;
            default:
                throw new IllegalArgumentException("Invalid task status: " + stringValue + ". Must be 'open' or 'done'");
        }
    }
}
