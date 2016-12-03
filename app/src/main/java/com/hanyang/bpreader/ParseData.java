package com.hanyang.bpreader;
import android.content.Context;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by kyujin on 2016-08-18.
 */
public class ParseData {
    ArrayList<Data> mData;
    ArrayList<String> values;
    DBManager manager;
    AirlineManager airline_manager;

    public ParseData(String data, Context mContext) {
        manager = new DBManager(mContext, "airport.db", null, 1);
        airline_manager = new AirlineManager(mContext, "airline.db", null, 1);
        values = new ArrayList<>();
        mData = new ArrayList<>();
        int cursor = 0;
        Log.i("ParseData", "RESULT: " + data + " END");
        Log.i("ParseData", "Data Length: " + data.length());
        if(data.length() < 131)
            while (data.length() != 131)
                data += " ";
        Log.i("ParseData", "Extended data length to : " + data.length());
        for(int c : ParseDataEnum.CURSOR) {
            Log.i("ParseData", "Now Parsing: " + Integer.toString(c));
            String p = data.substring(cursor, cursor + c).replace(" ", "");
            Log.d("ParseData", "Parsed Data: " + p + " END");
            values.add(p);
            cursor += c;
        }
    }
}
