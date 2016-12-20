/**
 * ParseData
 * IATA의 BCBP Implementation Guide를 참고하여 제작한 Boarding Pass Barcode Text 파싱 메소드
 * 참고
 * https://www.iata.org/whatwedo/stb/Documents/BCBP-Implementation-Guide-5th-Edition-June-2016.pdf
 * @author Kyujin Cho
 * @version 1.0
 * @see com.hanyang.bpreader.ParseDataEnum
 */

package com.hanyang.bpreader;
import android.content.Context;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ParseData {
    /**
     * 입력받은 바코드 텍스트를 사람이 읽을 수 있는 데이터 셋으로 파싱하여 리턴해 주는 메소드
     * @param data 바코드 텍스트
     * @param mContext Activity Context
     * @return Data Set
     */
    static ArrayList<Data> parse(String data, Context mContext) {
        DBManager manager = new DBManager(mContext, "airport.db", null, 1); // 공항 정보가 저장된 DB 불러옴
        AirlineManager airline_manager = new AirlineManager(mContext, "airline.db", null, 1); // 항공사 정보가 저장된 DB 불러옴
        ArrayList<String> values = new ArrayList<>(); // 잘라놓은 스트링이 저장될 ArrayList
        ArrayList<Data> mData = new ArrayList<>(); // Data의 집합체
        int cursor = 0; // String에서 현재 가리키고 있는 위치를 나타냄
        if(data.length() < 131) // Data의 길이가 131자 미만일 경우
            while (data.length() != 131)
                data += " "; // 강제로 131자까지 늘림
        for(int c : ParseDataEnum.CURSOR) { // ParseDataEnum의 위치에 맞춰 자르기 위해 ForEach문 실행
            String p = data.substring(cursor, cursor + c).replace(" ", ""); // 위치에 맞춰서 스트링 자르기
            values.add(p); // 자른 스트링을 values에 넣기
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
        // 이름이 MR/MS로 끝나는 경우 앞에 Mr./Ms.를 붙여줌

        values.set(5, manager.getName(values.get(5)));
        // 공항 이름이 저장된 데이터베이스에서 IATA 코드로 이름을 검색하여 저장해줌
        values.set(6, manager.getName(values.get(6)));
        // 공항 이름이 저장된 데이터베이스에서 IATA 코드로 이름을 검색하여 저장해줌
        values.set(7, airline_manager.getName(values.get(7)));
        // 항공사 이름이 저장된 데이터베이스에서 IATA 코드로 이름을 검색하여 저장해줌
        if(values.get(8).startsWith("0")) {
            String flight_code = values.get(8);
            while(flight_code.startsWith("0"))
                flight_code = flight_code.substring(1);
            values.set(8, flight_code);
        }
        // 편명 이쁘게 보이게 하기
        try {
            String input = Integer.toString(Calendar.getInstance().get(Calendar.YEAR)) + values.get(9);
            DateFormat fmt1 = new SimpleDateFormat("yyyyDDD");
            Date date = fmt1.parse(input);
            DateFormat fmt2 = new SimpleDateFormat("MM/dd/yyyy"); // 율리우스 날짜를 입력 받아 그레고리언 날짜로 저장
            values.set(9, fmt2.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String seat = "";
        switch (values.get(10)) { // 항공권 클래스를 가져와서 풀네임으로 변형해주는 Switch 문
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
        return mData;
    }
}
