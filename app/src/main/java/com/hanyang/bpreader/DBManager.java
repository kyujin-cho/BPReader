/**
 * DBManager
 * 전세계 공항의 IATA 코드 및 그에 대응하는 Full Name이 등록되어 있는 Database를 관리해줄 객체
 * @author Kyujin Cho
 * @contributor Hanhyeok Hwang
 * @version 1.0
 */

package com.hanyang.bpreader;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBManager extends SQLiteOpenHelper {
    public DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    /**
     * 입력된 table name에 상응하는 table이 존재하지 않을 시 Table을 생성해 주는 메소드
     * @param db Table이 등록될 Database
     */

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IATA_LIST( _id INTEGER PRIMARY KEY AUTOINCREMENT, lat TEXT, lon TEXT, country TEXT, name TEXT, code TEXT);");
        // 1씩 증가하는 Record별로 고유한 id, 위/경도, 소재국가, 이름, iata 코드를 포함하는 IATA_LIST table을 생성
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * 전달받은 쿼리문을 쿼리해주는 메소드
     * @param query 실행할 쿼리문
     */
    public void query(String query) {
        SQLiteDatabase db = getReadableDatabase(); // DB 열기
        db.execSQL(query); // DB에 전달받은 쿼리문 보내기
        db.close(); // DB 닫기
    }

    /**
     * 전달받은 iata code에 상응하는 항공사의 이름을 리턴해 주는 메소드
     * @param iataCode 검색할 iata code
     * @return 검색된 항공사 이름
     */
    public String getName(String iataCode) {
        String name = "";
        SQLiteDatabase db = getReadableDatabase(); // db 열기
        Cursor cursor = db.rawQuery("SELECT * FROM IATA_LIST WHERE code='" + iataCode + "';", null); // DB 에서 iata code가 전달받은 code인 공항 선택
        while(cursor.moveToNext())
            name = cursor.getString(4); // 공항 이름 가져오기
        if (name.equals("")) // 입력받은 코드로 공항이름을 찾을 수 없을 시
            name = iataCode; // 공항 이름을 iata 코드로 대체
        return name; // 이름 리턴
    }

    /**
     * 입력받은 위/경도, 이름, 국가, IATA 코드를 데이터베이스에 추가
     * @param name 공항 이름
     * @param lat 위도
     * @param lon 경도
     * @param country 소재국가
     * @param iata 공항의 IATA 코드
     */
    public void updateDatabase(String name, String lat, String lon, String country, String iata) {
        String mQuery = "INSERT INTO IATA_LIST(name, lat, lon, country, code)" +
                "SELECT '" + name + "','" + lat + "','" + lon + "','" + country + "','" + iata + "'" +
                "WHERE NOT EXISTS(SELECT '" + iata + "' FROM IATA_LIST WHERE code='" + iata + "');"; // 입력된 iata 코드가 존재하지 않을 시 새로운 레코드를 DB에 추가
        query(mQuery);
    }
}
