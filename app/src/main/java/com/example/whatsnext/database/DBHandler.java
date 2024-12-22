package com.example.whatsnext.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DBHandler extends SQLiteOpenHelper {
    private static final String DB_EVENTS_NAME = "events";
    private static final int DB_VERSION = 1;
    private static final String TABLE_EVENTS_NAME = "myevents";
    private static final String TABLE_HABITS_NAME = "myhabits";
    private static final String TABLE_HABITS_TRACKING_NAME = "myhabitstracking";
    private static final String EVENTS_ID_COL = "id";
    private static final String HABITS_ID_COL = "habitsid";
    private static final String TRACKING_ID_COL = "trackingid";
    private static final String NAME_COL = "Name";
    private static final String DESC_COL = "description";
    private static final String GOAL_COL = "goal";
    private static final String PROGRESS_COL = "progress";
    private static final String UNIT_COL = "unit";
    private static final String FREQ_COL = "frequency";
    private static final String INITIAL_DATE_COL = "initialdate";
    private static final String DATE_COL = "date";
    private static final String COLOUR_COL = "colour";
    private static final String FUTURE_COL = "inFuture";
    private static final String IMAGE_COL = "image";
    eventsDBQueries events = new eventsDBQueries();
    habitsDBQueries habits = new habitsDBQueries();

    public DBHandler(Context context) {

        super(context, DB_EVENTS_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_EVENTS_NAME + " (" +
                EVENTS_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NAME_COL + " TEXT," +
                DATE_COL + " DATE," +
                COLOUR_COL + " TEXT," +
                IMAGE_COL + " TEXT," +
                FUTURE_COL + " INTEGER)";

        db.execSQL(query);

        query = "CREATE TABLE " + TABLE_HABITS_NAME + " (" +
                HABITS_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NAME_COL + " TEXT, " +
                DESC_COL + " TEXT, " +
                COLOUR_COL + " TEXT, " +
                IMAGE_COL + " TEXT, " +
                GOAL_COL + " INTEGER, " +
                UNIT_COL + " TEXT, " +
                FREQ_COL + " TEXT, " +
                INITIAL_DATE_COL + " DATE)";

        db.execSQL(query);

        query = "CREATE TABLE " + TABLE_HABITS_TRACKING_NAME + " (" +
                TRACKING_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                HABITS_ID_COL + " INTEGER, " +
                DATE_COL + " DATE, " +
                PROGRESS_COL + " INTEGER, " +
                "FOREIGN KEY(" + HABITS_ID_COL + ") REFERENCES " + TABLE_HABITS_NAME + "(" + HABITS_ID_COL + "))";

        db.execSQL(query);
        defaultValues(db);
    }

    private void defaultValues(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(NAME_COL, "graduation");
        values.put(DATE_COL, "2027-06-30");
        values.put(COLOUR_COL, "-6190938");
        values.put(IMAGE_COL, "graduation_cap");
        values.put(FUTURE_COL, 1);

        ContentValues values2 = new ContentValues();
        values2.put(NAME_COL, "18th Birthday");
        values2.put(DATE_COL, "2023-07-29");
        values2.put(COLOUR_COL, "-6190938");
        values2.put(IMAGE_COL, "mirrorball");
        values2.put(FUTURE_COL, 0);

        db.insert(TABLE_EVENTS_NAME, null, values);
        db.insert(TABLE_EVENTS_NAME, null, values2);

        ContentValues values3 = new ContentValues();
        values3.put(NAME_COL, "brush teeth");
        values3.put(DESC_COL, "a simple daily goal as an example");
        values3.put(COLOUR_COL, "-6190938");
        values3.put(IMAGE_COL, "graduation_cap");
        values3.put(GOAL_COL, 2);
        values3.put(UNIT_COL, "times");
        values3.put(FREQ_COL, "daily");
        values3.put(INITIAL_DATE_COL, "2024-07-29");
        long habit1Id = db.insert(TABLE_HABITS_NAME, null, values3);

        ContentValues values4 = new ContentValues();
        values4.put(NAME_COL, "homework");
        values4.put(DESC_COL, "a simple daily goal as an example");
        values4.put(COLOUR_COL, "-6190938");
        values4.put(IMAGE_COL, "graduation_cap");
        values4.put(GOAL_COL, 30);
        values4.put(UNIT_COL, "minutes");
        values4.put(FREQ_COL, "weekly");
        values4.put(INITIAL_DATE_COL, "2024-10-19");
        long habit2Id = db.insert(TABLE_HABITS_NAME, null, values4);

        Calendar startDate = Calendar.getInstance();
        startDate.set(2024, Calendar.JULY, 29);
        Calendar endDate = Calendar.getInstance();
        endDate.set(2025, Calendar.JULY, 29);

        while (!startDate.after(endDate)) {
            ContentValues tracking1 = new ContentValues();
            tracking1.put(HABITS_ID_COL, habit1Id);
            tracking1.put(DATE_COL, new SimpleDateFormat("yyyy-MM-dd", Locale.UK).format(startDate.getTime()));
            tracking1.put(PROGRESS_COL, 1);
            db.insert(TABLE_HABITS_TRACKING_NAME, null, tracking1);
            startDate.add(Calendar.DATE, 1);
        }

        startDate.set(2024, Calendar.OCTOBER, 19);
        endDate.set(2025, Calendar.OCTOBER, 19);

        while (!startDate.after(endDate)) {
            ContentValues tracking2 = new ContentValues();
            tracking2.put(HABITS_ID_COL, habit2Id);
            tracking2.put(DATE_COL, new SimpleDateFormat("yyyy-MM-dd", Locale.UK).format(startDate.getTime()));
            tracking2.put(PROGRESS_COL, 0);
            db.insert(TABLE_HABITS_TRACKING_NAME, null, tracking2);
            startDate.add(Calendar.WEEK_OF_YEAR, 1);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this will run if i add a new column to the table to
        // avoid any errors!
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HABITS_NAME);
        onCreate(db);
    }

    //events

    public ArrayList<String[][]> onLoadEvents() {
        SQLiteDatabase db = this.getWritableDatabase();
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_HABITS_TRACKING_NAME);
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_HABITS_NAME);
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS_NAME);
        //onCreate(db);

        ArrayList<String[][]> eventsList = events.onLoadEvents(db);
        db.close();
        return eventsList;
    }

    public void addNewEvent(String eventName, String eventDate, String eventColour, int inFuture, String image) {
        SQLiteDatabase db = this.getWritableDatabase();
        events.addNewEvent(db, eventName, eventDate, eventColour, inFuture, image);
        db.close();
    }

    public void editEvent(String eventNo, String eventName, String eventDate, String eventColour, int inFuture, String image) {
        SQLiteDatabase db = this.getWritableDatabase();
        events.editEvent(db, eventNo, eventName, eventDate, eventColour, inFuture, image);
        db.close();
    }

    public void deleteEvent(String eventNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        events.deleteEvent(db, eventNo);
        db.close();
    }

    //habits
    public String[][] onLoadHabits(Date date) {
        SQLiteDatabase db = this.getWritableDatabase();
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_HABITS_TRACKING_NAME);
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_HABITS_NAME);
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS_NAME);
        //onCreate(db);

        String[][] habitsList = habits.onLoadHabits(db, date);
        db.close();
        return habitsList;
    }

    public void addNewHabit(String habitName, String habitDesc, String habitDate, String habitColour, String image, int goal, String unit, String freq) {
        SQLiteDatabase db = this.getWritableDatabase();
        habits.addNewHabit(db, habitName, habitDesc, habitDate, habitColour, image, goal, unit, freq);
        db.close();
    }

    public void addHabitProgress(int progress, int goal, String ID){
        SQLiteDatabase db = this.getWritableDatabase();
        habits.addHabitProgress(db, progress, goal, ID);
        db.close();
    }

    public void minusHabitProgress(int progress, String ID){
        SQLiteDatabase db = this.getWritableDatabase();
        habits.minusHabitProgress(db, progress, ID);
        db.close();
    }

    public void editHabit(int id, String habitName, String habitDesc, String habitDate, String habitColour, String image, int goal, String unit, String freq){
        SQLiteDatabase db = this.getWritableDatabase();
        habits.editHabit(db, String.valueOf(id), habitName, habitDesc, habitDate, habitColour, image, goal, unit, freq);
        db.close();
    }

    public void deletehabitInstance(int habitTrackingNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        habits.deleteHabitInstance(db, String.valueOf(habitTrackingNo));
        db.close();
    }

    public void deleteWholeHabit(int habitNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        habits.deleteWholeHabit(db, String.valueOf(habitNo));
        db.close();
    }
}

