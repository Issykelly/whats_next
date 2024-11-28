package com.example.whatsnext;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {
    private static final String DB_NAME = "events";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "myevents";
    private static final String ID_COL = "id";
    private static final String NAME_COL = "Name";
    private static final String DATE_COL = "date";
    private static final String COLOUR_COL = "colour";
    private static final String FUTURE_COL = "inFuture";

    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " (" +
                ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NAME_COL + " TEXT," +
                DATE_COL + " DATE," +
                COLOUR_COL + " TEXT," +
                FUTURE_COL + " INTEGER)";

        db.execSQL(query);
        defaultValues(db);
    }

    private void defaultValues(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(NAME_COL, "example event");
        values.put(DATE_COL, "2025-11-21");
        values.put(COLOUR_COL, "-6190938");
        values.put(FUTURE_COL, 1);

        ContentValues values2 = new ContentValues();
        values2.put(NAME_COL, "example event2");
        values2.put(DATE_COL, "2023-11-21");
        values2.put(COLOUR_COL, "-6190938");
        values2.put(FUTURE_COL, 0);

        db.insert(TABLE_NAME, null, values);
        db.insert(TABLE_NAME, null, values2);
    }
    public ArrayList<String[][]> onLoad(){
        SQLiteDatabase db = this.getWritableDatabase();

        //String dropTableQuery = "DROP TABLE IF EXISTS " + TABLE_NAME;
        //db.execSQL(dropTableQuery);
        //onCreate(db);
        // this has been commented out - i use it if i want to reset the database!

        Cursor cursorEvents =
                db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + FUTURE_COL + " = 1 ORDER BY " + DATE_COL + " DESC", null);
        // this line grabs all data from the table

        int rowCount = cursorEvents.getCount();
        // how many enteries in the table?
        int columnCount = 4;
        // hardcoded as I know how many fields there are


        // Create a 2D array to store the results for events in the future
        String[][] eventsListF = new String[rowCount][columnCount];

        int rowIndex = 0;
        if (cursorEvents.moveToFirst()) {
            do {
                eventsListF[rowIndex][0] = cursorEvents.getString(1); // event name
                eventsListF[rowIndex][1] = cursorEvents.getString(2); // event date
                eventsListF[rowIndex][2] = cursorEvents.getString(3); // event colour
                eventsListF[rowIndex][3] = cursorEvents.getString(0); // event colour
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

    public void addNewEvent(String eventName, String eventDate, String eventColour, int inFuture) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(NAME_COL, eventName);
        values.put(DATE_COL, eventDate);
        values.put(COLOUR_COL, eventColour);
        values.put(FUTURE_COL, inFuture);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this will run if i add a new column to the table to
        // avoid any errors!
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}

