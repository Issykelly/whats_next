package com.example.whatsnext.countdownHandling;

import java.io.Serializable;

public class CountdownModel implements Serializable {

    private String[] countdown;  // Hold the countdown value
    private final int eventColour;
    private final String eventNo;
    private final String eventImage;
    private String eventDate;
    private String eventName;

    // Constructor
    public CountdownModel(String eventNo, String eventName, String eventDate, String eventColour, String eventImage) {
        this.eventNo = eventNo;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventImage = eventImage;
        this.eventColour = Integer.parseInt(eventColour);
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


