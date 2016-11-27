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
        db.execSQL("CREATE TABLE IATA_LIST( _id INTEGER PRIMARY KEY AUTOINCREMENT, lat TEXT, lon TEXT, country TEXT, name TEXT, code TEXT);");
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
        String name = "";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM IATA_LIST WHERE code='" + iataCode + "';", null);
        while(cursor.moveToNext())
            name = cursor.getString(4);
        if (name.equals(""))
            name = iataCode;
        return name;
    }

    public void updateDatabase(String name, String lat, String lon, String country, String iata) {
        String mQuery = "INSERT INTO IATA_LIST(name, lat, lon, country, code)" +
                "SELECT '" + name + "','" + lat + "','" + lon + "','" + country + "','" + iata + "'" +
                "WHERE NOT EXISTS(SELECT '" + iata + "' FROM IATA_LIST WHERE code='" + iata + "');";
        query(mQuery);
    }
}
