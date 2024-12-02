package com.example.whatsnext;

import android.graphics.Color;
import android.util.Log;

import java.io.Serializable;

public class CountdownModel implements Serializable {

    private String[] countdown;  // Hold the countdown value
    private int eventColour;
    private String eventNo, eventImage, eventDate, eventName;

    // Constructor
    public CountdownModel(String eventNo, String eventName, String eventDate, String eventColour, String eventImage) {
        this.eventNo = eventNo;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventImage = eventImage;
        this.eventColour = Integer.valueOf(eventColour);
        this.countdown = new String[]{"0", "seconds"}; // Default value
    }

    // Getter methods
    public String getEventNumber() {
        return eventNo;
    }
    public String getEventName() {
        return eventName;
    }

    public String getEventDate() {
        return eventDate;
    }
    public int getEventColour() {
        return eventColour;
    }

    public String getEventImage(){
        return eventImage;
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


