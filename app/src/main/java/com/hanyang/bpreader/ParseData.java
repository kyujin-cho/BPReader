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
        int length = values.get(2).length();
        String name = values.get(2);
        if (name.substring(length - 2).equals("MR"))
            name = "Mr. " + values.get(2).substring(0, length - 2);
        else if (name.substring(length - 1).equals("MS"))
            name = "Ms. " + values.get(2).substring(0, length - 2);
        String[] words = name.split("/");
        name = "";
        for(String word : words) {
            if(word.startsWith("Mr. ") || word.startsWith("Ms. "))
                name += word.substring(0, 4) + word.substring(4, 5) + word.substring(5).toLowerCase() + " ";
            else
                name += word.substring(0, 1) + word.substring(1).toLowerCase() + " ";
        }

        values.set(2, name);

        values.set(5, manager.getName(values.get(5)));
        values.set(6, manager.getName(values.get(6)));
        values.set(7, airline_manager.getName(values.get(7)));
        if(values.get(8).startsWith("0")) {
            String flight_code = values.get(8);
            while(flight_code.startsWith("0"))
                flight_code = flight_code.substring(1);
            values.set(8, flight_code);
        }

     try {
            String input = Integer.toString(Calendar.getInstance().get(Calendar.YEAR)) + values.get(9);
            DateFormat fmt1 = new SimpleDateFormat("yyyyDDD");
            Date date = fmt1.parse(input);
            DateFormat fmt2 = new SimpleDateFormat("MM/dd/yyyy");
            values.set(9, fmt2.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String seat = "";
        switch (values.get(10)) {
            case "Y" :
                seat = "Economy Class";
                break;
            case "J" :
                seat = "Business Class";
                break;
            case "F" :
                seat = "First Class";
                break;
            case "W" :
                seat = "Premium Economy";
                break;
            default :
                seat = values.get(10);
                break;
        }
        values.set(10, seat);

        if (values.get(11).charAt(0) == '0')
            values.set(11, values.get(11).substring(1));
        if (!values.get(21).equals("")) {

        }

        for(int i = 0; i < ParseDataEnum.DESC.length; i++) {
            if(ParseDataEnum.DESC[i].equals("PASS"))
                continue;
            if(!values.get(i).equals(""))
                mData.add(new Data(ParseDataEnum.DESC[i], values.get(i)));
        }
    }
}
