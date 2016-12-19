package com.hanyang.bpreader;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by kyujin on 13/12/2016.
 */

public class BPManager extends SQLiteOpenHelper {
    Context context;
    public BPManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE CODES(_id INTEGER PRIMARY KEY AUTOINCREMENT, code TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void query(String query) {
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL(query);
        db.close();
    }

    public void addString(String code) {
        query("INSERT INTO CODES(code) VALUES('" + code + "');");
    }

    public void deleteRecord(String id) {
        String query = "DELETE FROM CODES WHERE _id=" + id + ";";
        query(query);
    }

    public ArrayList<ListData> getDatas() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM CODES", null);
        ArrayList<ListData> datas = new ArrayList<>();
        while(cursor.moveToNext()) {
            String id = cursor.getString(0);
            String rawdata = cursor.getString(1);
            ArrayList<Data> d = new ParseData(rawdata, context).mData;
            datas.add(new ListData(id, rawdata, d.get(3).getContent(), d.get(4).getContent(), d.get(5).getContent(), d.get(6).getContent()));
        }
        cursor.close();
        db.close();
        Collections.reverse(datas);
        return datas;
    }
}
