package com.mobileappclass.assignment3;

/**
 * Created by Aneesh on 11/6/2016.
 */
import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Aneesh on 11/2/2016.
 */
public class MyDBHandler extends SQLiteOpenHelper{

    // Database version and name
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "GPSData13.db";

    // table name
    public static final String GPS_TABLE_NAME = "GPSData";

    // columns in the table - schema
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATE_TIME = "_dateTime";
    public static final String COLUMN_LAT = "_lat";
    public static final String COLUMN_LONG = "_long";

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE IF NOT EXISTS " + GPS_TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY," +
                COLUMN_DATE_TIME + " TEXT," +
                COLUMN_LAT + " TEXT," +
                COLUMN_LONG + " TEXT);";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + GPS_TABLE_NAME);
        onCreate(db);
    }

    // Adding a new roe to the table
    public void addEntry(GPSDataDb entry){
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE_TIME, entry.get_dateTime());
        values.put(COLUMN_LAT, entry.get_lat());
        values.put(COLUMN_LONG, entry.get_long());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(GPS_TABLE_NAME, null, values);
        db.close();
    }

    // Read from database and make a string
    public ArrayList<String> readDB(){
        String query;
        String resultString = "";
        ArrayList<String> resultList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        query = "SELECT * FROM " + GPS_TABLE_NAME + " WHERE 1 ORDER BY " + COLUMN_DATE_TIME + " DESC;";
        Log.i("DBRead", "after query");
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        int i = 0;
        while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex(COLUMN_DATE_TIME)) != null &&
                    c.getString(c.getColumnIndex(COLUMN_LAT)) != null &&
                    c.getString(c.getColumnIndex(COLUMN_LONG)) != null){

                resultString += c.getString(c.getColumnIndex(COLUMN_DATE_TIME));
                resultString += "\t";

                resultString += c.getString(c.getColumnIndex(COLUMN_LAT));
                resultString += "\t";

                resultString += c.getString(c.getColumnIndex(COLUMN_LONG));
                resultList.add(resultString);

                resultString = "";
                c.moveToNext();
                i += 1;
            }
        }
        db.close();

        return resultList;
    }

}
