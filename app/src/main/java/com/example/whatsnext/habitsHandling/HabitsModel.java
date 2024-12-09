package com.example.whatsnext.habitsHandling;

import java.io.Serializable;

public class HabitsModel implements Serializable {
    private int habitColour, habitNo, habitTrackingNo, habitGoal, habitProgress;
    private String habitImage, habitDesc, habitName, habitUnit, habitFreq, habitDate;

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
        this.habitColour = Integer.valueOf(habitColour);
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
