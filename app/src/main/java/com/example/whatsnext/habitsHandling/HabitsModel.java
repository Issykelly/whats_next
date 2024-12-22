package com.example.whatsnext.habitsHandling;

import java.io.Serializable;

public class HabitsModel implements Serializable {
    private final int habitColour;
    private final int habitNo;
    private final int habitTrackingNo;
    private final int habitGoal;
    private int habitProgress;
    private final String habitImage;
    private final String habitDesc;
    private final String habitName;
    private final String habitUnit;
    private final String habitFreq;
    private final String habitDate;

    // Constructor
    public HabitsModel(int habitNo, String habitName, String habitDesc, int habitGoal, int habitProgress, String habitUnit, int habitColour, String habitImage, int habitTrackingNo, String habitFreq, String habitDate) {
        this.habitNo = habitNo;
        this.habitDate = habitDate;
        this.habitFreq = habitFreq;
        this.habitTrackingNo = habitTrackingNo;
        this.habitName = habitName;
        this.habitDesc = habitDesc;
        this.habitGoal = habitGoal;
        this.habitProgress = habitProgress;
        this.habitUnit = habitUnit;
        this.habitImage = habitImage;
        this.habitColour = habitColour;
    }

    // Getter methods
    public int getHabitNumber() {
        return habitNo;
    }

    public String getHabitDate(){
        return habitDate;
    }

    public String getHabitFreq(){
        return habitFreq;
    }

    public int getHabitTrackingNo(){
        return habitTrackingNo;
    }
    public String getHabitName() {
        return habitName;
    }

    public String getHabitDesc() {
        return habitDesc;
    }
    public int getHabitColour() {
        return habitColour;
    }

    public String getHabitImage(){
        return habitImage;
    }

    public int getHabitProgress(){
        return habitProgress;
    }

    public int getHabitGoal(){
        return habitGoal;
    }

    public String getHabitUnit(){
        return habitUnit;
    }

    public void setHabitProgress(int progress){
        habitProgress = progress;
    }

}
