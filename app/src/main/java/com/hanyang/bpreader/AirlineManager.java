/**
 * AirlineManager
 * 전세계 항공사의 IATA 코드 및 그에 대응하는 Full Name이 등록되어 있는 Database를 관리해줄 객체.
 * DBManager에서 기본 기능을 가져와 항공사용 DB에 맞게 변경.
 * @author Kyujin Cho
 * @contributor Hanhyeok Hwang
 * @version 1.0
 * @see com.hanyang.bpreader.DBManager
 */

package com.hanyang.bpreader;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AirlineManager extends DBManager {
    /**
     * 기본 생성자
     * @param context
     * @param name
     * @param factory
     * @param version
     */
    public AirlineManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /**
     * 입력된 table name에 상응하는 table이 존재하지 않을 시 Table을 생성해 주는 메소드
     * @param db Table이 등록될 Database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IATA_LIST( _id INTEGER PRIMARY KEY AUTOINCREMENT, country TEXT, name TEXT, code TEXT);");
        // 1씩 증가하는 id, TEXT 형식의 country, name, code 를 포함하는 IATA_LIST 테이블을 생성
    }

    /**
     * 전달받은 iata code에 상응하는 항공사의 이름을 리턴해 주는 메소드
     * @param iataCode 검색할 iata code
     * @return 검색된 항공사 이름
     */
    @Override
    public String getName(String iataCode) {
        String name = "";
        SQLiteDatabase db = getReadableDatabase(); // DB 열기
        Cursor cursor = db.rawQuery("SELECT * FROM IATA_LIST WHERE code='" + iataCode + "';", null); // DB에서 iata code가 전달받은 code인 항공사 선택
        while (cursor.moveToNext())
            name = cursor.getString(2); // 항공사 이름 가져오기
        if (name.equals("")) // 해당하는 항공사가 없으면
            name = iataCode; // 항공사 이름을 iata 코드로 대체
        return name; // 이름 리턴
    }

    /**
     * 입력받은 이름, 국가, IATA 코드를 데이터베이스에 추가
     * @param name 항공사 이름
     * @param country 소재 국가
     * @param iata IATA 코드
     */

    public void updateDatabase(String name, String country, String iata) {
        String mQuery = "INSERT INTO IATA_LIST(name, country, code)" +
                "SELECT '" + name + "','" + country + "','" + iata + "'" +
                "WHERE NOT EXISTS(SELECT '" + iata + "' FROM IATA_LIST WHERE code='" + iata + "');"; // 입력된 iata 코드가 존재하지 않을 시 새로운 레코드 추가
        query(mQuery);
    }
}
