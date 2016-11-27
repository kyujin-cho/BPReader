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
        db.execSQL("CREATE TABLE IATA_LIST( _id INTEGER PRIMARY KEY AUTOINCREMENT, country TEXT, name TEXT, code TEXT);");
    }

    @Override
    public String getName(String iataCode) {
        String name = "";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM IATA_LIST WHERE code='" + iataCode + "';", null);
        while(cursor.moveToNext())
            name = cursor.getString(2);
        if (name.equals(""))
            name = iataCode;
        return name;
    }

    public void updateDatabase(String name, String country, String iata) {
        String mQuery = "INSERT INTO IATA_LIST(name, country, code)" +
                "SELECT '" + name + "','" + country + "','" + iata + "'" +
                "WHERE NOT EXISTS(SELECT '" + iata + "' FROM IATA_LIST WHERE code='" + iata + "');";
        query(mQuery);
    }
}
