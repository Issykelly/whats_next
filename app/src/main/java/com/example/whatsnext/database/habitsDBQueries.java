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
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(date);

        String query = "SELECT " +
                TABLE_HABITS_NAME + "." + NAME_COL + ", " +
                TABLE_HABITS_NAME + "." + DESC_COL + ", " +
                TABLE_HABITS_NAME + "." + COLOUR_COL + ", " +
                TABLE_HABITS_NAME + "." + HABITS_ID_COL + ", " +
                TABLE_HABITS_NAME + "." + IMAGE_COL + ", " +
                TABLE_HABITS_NAME + "." + GOAL_COL + ", " +
                TABLE_HABITS_TRACKING_NAME + "." + PROGRESS_COL + ", " +
                TABLE_HABITS_NAME + "." + UNIT_COL + " " +
                "FROM " + TABLE_HABITS_NAME +
                " JOIN " + TABLE_HABITS_TRACKING_NAME +
                " ON " + TABLE_HABITS_NAME + "." + HABITS_ID_COL + " = " + TABLE_HABITS_TRACKING_NAME + "." + HABITS_ID_COL +
                " WHERE " + TABLE_HABITS_TRACKING_NAME + "." + DATE_COL + " = ?";

        Cursor cursorEvents = null;
        try {
            cursorEvents = db.rawQuery(query, new String[]{formattedDate});

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
        } finally {
            if (cursorEvents != null) {
                cursorEvents.close();
            }
        }
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

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = dateFormat.parse(habitDate);

            values = new ContentValues();
            values.put(HABITS_ID_COL, habit1Id);
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
                    values.put(HABITS_ID_COL, habit1Id);
                    values.put(PROGRESS_COL, 0);
                    db.insert(TABLE_HABITS_TRACKING_NAME, null, values);
                }
            } else if (Objects.equals(freq, "weekly")) {
                for (int i = 0; i < 52; i++) {
                    calendar.add(Calendar.WEEK_OF_YEAR, 1);
                    values = new ContentValues();
                    values.put(DATE_COL, dateFormat.format(calendar.getTime()));
                    values.put(HABITS_ID_COL, habit1Id);
                    values.put(PROGRESS_COL, 0);
                    db.insert(TABLE_HABITS_TRACKING_NAME, null, values);
                }
            } else if (Objects.equals(freq, "monthly")) {
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                for (int i = 0; i < 12; i++) {
                    calendar.add(Calendar.MONTH, 1);
                    int maxDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    if (dayOfMonth > maxDayOfMonth) {
                        calendar.set(Calendar.DAY_OF_MONTH, maxDayOfMonth);
                    } else {
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    }

                    values = new ContentValues();
                    values.put(DATE_COL, dateFormat.format(calendar.getTime()));
                    values.put(HABITS_ID_COL, habit1Id);
                    values.put(PROGRESS_COL, 0);
                    db.insert(TABLE_HABITS_TRACKING_NAME, null, values);
                }
            }

            db.close();

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


}
