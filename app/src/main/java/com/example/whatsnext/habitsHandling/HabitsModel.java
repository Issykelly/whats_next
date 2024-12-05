package com.example.whatsnext.habitsHandling;

import java.io.Serializable;

public class HabitsModel implements Serializable {
    private int habitColour, habitNo;
    private String habitImage, habitDesc, habitName, habitGoal, habitProgress, habitUnit;

    // Constructor
    public HabitsModel(int habitNo, String habitName, String habitDesc, String habitGoal, String habitProgress, String habitUnit, int habitColour, String habitImage) {
        this.habitNo = habitNo;
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

    public String getHabitProgress(){
        return habitProgress;
    }

    public String getHabitGoal(){
        return habitGoal;
    }

    public String getHabitUnit(){
        return habitUnit;
    }

}
