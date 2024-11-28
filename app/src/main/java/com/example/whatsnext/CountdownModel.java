package com.example.whatsnext;

import android.graphics.Color;
import android.util.Log;

import java.io.Serializable;

public class CountdownModel implements Serializable {

    private String[] countdown;  // Hold the countdown value
    private String eventName;
    private String eventDate;
    private int eventColour;

    // Constructor
    public CountdownModel(String eventName, String eventDate, String eventColour) {
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventColour = Integer.valueOf(eventColour);
        this.countdown = new String[]{"0", "seconds"}; // Default value
    }

    // Getter methods
    public String getEventName() {
        return eventName;
    }

    public String getEventDate() {
        return eventDate;
    }
    public int getEventColour() {
        return eventColour;
    }

    public String[] getCountdown() {
        return countdown;
    }

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


