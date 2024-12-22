package com.example.whatsnext.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.icu.text.SimpleDateFormat;
import android.util.Log;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class habitsDBQueries {
    private static final int DB_VERSION = 1;
    private static final String TABLE_HABITS_NAME = "myhabits";
    private static final String TABLE_HABITS_TRACKING_NAME = "myhabitstracking";
    private static final String HABITS_ID_COL = "habitsid";
    private static final String TRACKING_ID_COL = "trackingid";
    private static final String NAME_COL = "Name";
    private static final String DESC_COL = "description";
    private static final String GOAL_COL = "goal";
    private static final String PROGRESS_COL = "progress";
    private static final String UNIT_COL = "unit";
    private static final String COLOUR_COL = "colour";
    private static final String IMAGE_COL = "image";
    private static final String FREQ_COL = "frequency";
    private static final String INITIAL_DATE_COL = "initialdate";
    private static final String DATE_COL = "date";

    public String[][] onLoadHabits(SQLiteDatabase db, Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
        String formattedDate = dateFormat.format(date);

        String query = "SELECT " +
                TABLE_HABITS_NAME + "." + NAME_COL + ", " +
                TABLE_HABITS_NAME + "." + DESC_COL + ", " +
                TABLE_HABITS_NAME + "." + COLOUR_COL + ", " +
                TABLE_HABITS_NAME + "." + HABITS_ID_COL + ", " +
                TABLE_HABITS_NAME + "." + IMAGE_COL + ", " +
                TABLE_HABITS_NAME + "." + GOAL_COL + ", " +
                TABLE_HABITS_TRACKING_NAME + "." + PROGRESS_COL + ", " +
                TABLE_HABITS_NAME + "." + UNIT_COL + ", " +
                TABLE_HABITS_NAME + "." + FREQ_COL + ", " +
                TABLE_HABITS_TRACKING_NAME + "." + DATE_COL + ", " +
                TABLE_HABITS_TRACKING_NAME + "." + TRACKING_ID_COL +
                " FROM " + TABLE_HABITS_NAME +
                " JOIN " + TABLE_HABITS_TRACKING_NAME +
                " ON " + TABLE_HABITS_NAME + "." + HABITS_ID_COL + " = " + TABLE_HABITS_TRACKING_NAME + "." + HABITS_ID_COL +
                " WHERE " + TABLE_HABITS_TRACKING_NAME + "." + DATE_COL + " = ?";

        try (Cursor cursorEvents = db.rawQuery(query, new String[]{formattedDate})) {

            int rowCount = cursorEvents.getCount();
            int columnCount = cursorEvents.getColumnCount();

            String[][] habitsList = new String[rowCount][columnCount];

            int rowIndex = 0;
            while (cursorEvents.moveToNext()) {
                for (int colIndex = 0; colIndex < columnCount; colIndex++) {
                    habitsList[rowIndex][colIndex] = cursorEvents.isNull(colIndex) ? "" : cursorEvents.getString(colIndex);
                }
                rowIndex++;
            }
            return habitsList;
        } catch (SQLiteException e) {
            Log.e("onLoadHabits", "Error executing query: " + query, e);
            return new String[0][0]; // Return an empty array on failure
        }
    }

    public void addHabitProgress(SQLiteDatabase db, int progress, int goal, String ID){
        ContentValues values = new ContentValues();
        int newProgress = progress + 1;
        values.put(PROGRESS_COL, String.valueOf(newProgress));
        db.update(TABLE_HABITS_TRACKING_NAME, values, TRACKING_ID_COL + " = ?", new String[]{ID});
        }


    public void minusHabitProgress(SQLiteDatabase db, int progress, String ID) {
        ContentValues values = new ContentValues();
        int newProgress = progress - 1;
        values.put(PROGRESS_COL, String.valueOf(newProgress));
        db.update(TABLE_HABITS_TRACKING_NAME, values, TRACKING_ID_COL + " = ?", new String[]{ID});
    }

    public void editHabit(SQLiteDatabase db, String ID, String habitName, String habitdesc, String habitDate, String habitColour, String image, int goal, String unit, String freq){
        ContentValues values = new ContentValues();

        // update all infomation, as all infomation is passed to this
        // method weather changed or not
        values.put(NAME_COL, habitName);
        values.put(DESC_COL, habitdesc);
        values.put(COLOUR_COL, habitColour);
        values.put(IMAGE_COL, image);
        values.put(GOAL_COL, goal);
        values.put(UNIT_COL, unit);
        values.put(FREQ_COL, freq);
        values.put(INITIAL_DATE_COL, habitDate);

        // update habit table
        db.update(TABLE_HABITS_NAME, values, HABITS_ID_COL + " = ?", new String[]{ID});
        // delete from habit tracking table if after the selected date
        db.delete(
                TABLE_HABITS_TRACKING_NAME,
                HABITS_ID_COL + " = ? AND " + DATE_COL + " >= ?",
                new String[]{ID, habitDate}
        );

        // re add instances to habit tracking table
        addNewInstances(db, ID, habitDate, freq);
    }

    public void deleteHabitInstance(SQLiteDatabase db, String id){
        db.delete(TABLE_HABITS_TRACKING_NAME, TRACKING_ID_COL + " = ?", new String[]{id});
    }

    public void deleteWholeHabit(SQLiteDatabase db, String id){
        db.delete(TABLE_HABITS_NAME, HABITS_ID_COL + " = ?", new String[]{id});
        db.delete(TABLE_HABITS_TRACKING_NAME, HABITS_ID_COL + " = ?", new String[]{id});
    }

    public void addNewHabit(SQLiteDatabase db, String habitName, String habitdesc, String habitDate, String habitColour, String image, int goal, String unit, String freq) {
        ContentValues values = new ContentValues();

        values.put(NAME_COL, habitName);
        values.put(DESC_COL, habitdesc);
        values.put(COLOUR_COL, habitColour);
        values.put(IMAGE_COL, image);
        values.put(GOAL_COL, goal);
        values.put(UNIT_COL, unit);
        values.put(FREQ_COL, freq);
        values.put(INITIAL_DATE_COL, habitDate);

        long habit1Id = db.insert(TABLE_HABITS_NAME, null, values);
        addNewInstances(db, String.valueOf(habit1Id), habitDate, freq);
    }

    public void addNewInstances(SQLiteDatabase db, String ID, String habitDate, String freq){

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
        try {
            Date date = dateFormat.parse(habitDate);

            ContentValues values = new ContentValues();
            values.put(HABITS_ID_COL, ID);
            values.put(DATE_COL, habitDate);
            values.put(PROGRESS_COL, 0);
            db.insert(TABLE_HABITS_TRACKING_NAME, null, values);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            if (Objects.equals(freq, "daily")) {
                for (int i = 0; i < 364; i++) {
                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                    values = new ContentValues();
                    values.put(DATE_COL, dateFormat.format(calendar.getTime()));
                    values.put(HABITS_ID_COL, ID);
                    values.put(PROGRESS_COL, 0);
                    db.insert(TABLE_HABITS_TRACKING_NAME, null, values);
                }
            } else if (Objects.equals(freq, "weekly")) {
                for (int i = 0; i < 52; i++) {
                    calendar.add(Calendar.WEEK_OF_YEAR, 1);
                    values = new ContentValues();
                    values.put(DATE_COL, dateFormat.format(calendar.getTime()));
                    values.put(HABITS_ID_COL, ID);
                    values.put(PROGRESS_COL, 0);
                    db.insert(TABLE_HABITS_TRACKING_NAME, null, values);
                }
            } else if (Objects.equals(freq, "monthly")) {
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                for (int i = 0; i < 12; i++) {
                    calendar.add(Calendar.MONTH, 1);
                    int maxDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    calendar.set(Calendar.DAY_OF_MONTH, Math.min(dayOfMonth, maxDayOfMonth));

                    values = new ContentValues();
                    values.put(DATE_COL, dateFormat.format(calendar.getTime()));
                    values.put(HABITS_ID_COL, ID);
                    values.put(PROGRESS_COL, 0);
                    db.insert(TABLE_HABITS_TRACKING_NAME, null, values);
                }
            }

            db.close();

        } catch (ParseException e) {
            Log.e("habitsDBQueries", "error in database queries");
        }}}
