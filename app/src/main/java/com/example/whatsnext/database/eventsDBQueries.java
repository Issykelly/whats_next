package com.example.whatsnext.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;
import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class eventsDBQueries {

    private static final String DB_NAME = "events";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "myevents";
    private static final String ID_COL = "id";
    private static final String NAME_COL = "Name";
    private static final String DATE_COL = "date";
    private static final String COLOUR_COL = "colour";
    private static final String FUTURE_COL = "inFuture";
    private static final String IMAGE_COL = "image";

    public ArrayList<String[][]> onLoadEvents(SQLiteDatabase db){
        // this has been commented out - i use it if i want to reset the database!

        try {
            //check if the event has passed
            checkFutureEventsPast(db);
        } catch (ParseException e) {
            //shouldn't pass an error as data is checked before adding
            //however this try catch statement is required
        }

        Cursor cursorEvents =
                db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + FUTURE_COL + " = 1 ORDER BY " + DATE_COL + " ASC", null);
        // this line grabs all data from the table

        int rowCount = cursorEvents.getCount();
        // how many enteries in the table?
        int columnCount = 5;
        // hardcoded as I know how many fields there are


        // Create a 2D array to store the results for events in the future
        String[][] eventsListF = new String[rowCount][columnCount];

        int rowIndex = 0;
        if (cursorEvents.moveToFirst()) {
            do {
                eventsListF[rowIndex][0] = cursorEvents.getString(1); // event name
                eventsListF[rowIndex][1] = cursorEvents.getString(2); // event date
                eventsListF[rowIndex][2] = cursorEvents.getString(3); // event colour
                eventsListF[rowIndex][3] = cursorEvents.getString(0); // event number
                eventsListF[rowIndex][4] = cursorEvents.getString(4); // event image
                rowIndex++;
            } while (cursorEvents.moveToNext());
            // moving our cursor to next.
        }

        cursorEvents =
                db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + FUTURE_COL + " = 0 ORDER BY " + DATE_COL + " DESC", null);
        rowCount = cursorEvents.getCount();

        // Create a 2D array to store the results for past events
        String[][] eventsListP = new String[rowCount][columnCount];
        rowIndex = 0;
        if (cursorEvents.moveToFirst()) {
            do {
                eventsListP[rowIndex][0] = cursorEvents.getString(1); // event name
                eventsListP[rowIndex][1] = cursorEvents.getString(2); // event date
                eventsListP[rowIndex][2] = cursorEvents.getString(3); // event colour
                eventsListP[rowIndex][3] = cursorEvents.getString(0); // event number
                eventsListP[rowIndex][4] = cursorEvents.getString(4); // event image
                rowIndex++;
            } while (cursorEvents.moveToNext());
            // moving our cursor to next.
        }

        ArrayList<String[][]> arrays = new ArrayList<>(2);
        arrays.add(eventsListF);
        arrays.add(eventsListP);

        cursorEvents.close();
        return arrays;
    }

    public void checkFutureEventsPast(SQLiteDatabase db) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date();

        Cursor cursorEvents =
                db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + FUTURE_COL + " = 1 ORDER BY " + DATE_COL + " DESC", null);

        int rowIndex = 0;
        if (cursorEvents.moveToFirst()) {
            do {
                Date eventDate = sdf.parse(cursorEvents.getString(2));
                if (eventDate.before(currentDate)) {
                    // if the event has passed, update it so the event is in the past
                    String eventNo = cursorEvents.getString(0);
                    ContentValues values = new ContentValues();
                    values.put(FUTURE_COL, 0);
                    db.update(TABLE_NAME, values, ID_COL + " = ?", new String[]{eventNo});
                }
                rowIndex++;
            } while (cursorEvents.moveToNext());
            // moving our cursor to next.
        }
    }

    public void addNewEvent(SQLiteDatabase db, String eventName, String eventDate, String eventColour, int inFuture, String image) {
        ContentValues values = new ContentValues();

        values.put(NAME_COL, eventName);
        values.put(DATE_COL, eventDate);
        values.put(COLOUR_COL, eventColour);
        values.put(IMAGE_COL, image);
        values.put(FUTURE_COL, inFuture);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void editEvent(SQLiteDatabase db, String eventNo, String eventName, String eventDate, String eventColour, int inFuture, String image){
        ContentValues values = new ContentValues();

        values.put(NAME_COL, eventNo);
        values.put(NAME_COL, eventName);
        values.put(DATE_COL, eventDate);
        values.put(COLOUR_COL, eventColour);
        values.put(IMAGE_COL, image);
        values.put(FUTURE_COL, inFuture);

        db.update(TABLE_NAME, values, ID_COL + " = ?", new String[]{eventNo});
    }

    public void deleteEvent(SQLiteDatabase db, String eventNo){
        db.delete(TABLE_NAME, ID_COL + " = ?", new String[]{eventNo});
    }
}
