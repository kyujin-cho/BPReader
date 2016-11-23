package com.hanyang.bpreader;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by hp on 9/6/2016.
 */
public class AirlineManager extends DBManager {
    public AirlineManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creates new Database
    }

    @Override
    public String getName(String iataCode) {
        // Retrieves full name of the airline
        return null;
    }

    public void updateDatabase(String name, String country, String iata) {
        // Updates database with files downloaded from server
    }
}
