package com.example.whatsnext;

import java.io.Serializable;

public class CountdownModel implements Serializable {

    private String[] countdown;  // Hold the countdown value
    private String eventName;
    private String eventDate;

    // Constructor
    public CountdownModel(String eventName, String eventDate) {
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.countdown = new String[]{"0", "seconds"}; // Default value
    }

    // Getter methods
    public String getEventName() {
        return eventName;
    }

    public String getEventDate() {
        return eventDate;
    }

    public String[] getCountdown() {
        return countdown;
    }

    // Setter methods
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public void setCountdown(String[] countdown) {
        this.countdown = countdown;  // Update countdown value
    }
}


