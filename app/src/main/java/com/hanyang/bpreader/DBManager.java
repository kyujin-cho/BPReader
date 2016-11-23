package com.hanyang.bpreader;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hp on 9/6/2016.
 */
public class DBManager extends SQLiteOpenHelper {
    public DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creates new Database
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void query(String mQuery) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(mQuery);
        db.close();
    }

    public String getName(String iataCode) {
            // Retrieves full name of the airline
        return null;
    }

    public void updateDatabase(String name, String lat, String lon, String country, String iata) {
        // Updates database with files downloaded from server
    }


}
