/**
 * BPManager
 * Application에서 Scan 한 Barcode Text들이 등록되어 있는 Database를 관리해줄 객체
 * @author Kyujin Cho
 * @contributor Hanhyeok Hwang
 * @version 1.0
 */

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

    /**
     * 기본 생성자
     * @param context
     * @param name
     * @param factory
     * @param version
     */
    public BPManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    /**
     * 입력된 table name에 상응하는 table이 존재하지 않을 시 Table을 생성해 주는 메소드
     * @param sqLiteDatabase Table이 등록될 Database
     */

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE CODES(_id INTEGER PRIMARY KEY AUTOINCREMENT, code TEXT);"); // 1씩 증가하는 id, TEXT 형식의 code 를 포함하는 CODES 테이블을 생성
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

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
     * 입력받은 바코드 텍스트를 db에 추가해 주는 메소드
     * @param code 추가할 바코드 텍스트
     */

    public void addString(String code) {
        query("INSERT INTO CODES(code) VALUES('" + code + "');"); // 전달받은 바코드 텍스트틀 DB에 추가하는 쿼리문을 작성 후 실행
    }

    /**
     * 입력받은 id의 레코드를 삭제해 주는 메소드
     * @param id 삭제할 레코드의 id
     */

    public void deleteRecord(String id) {
        String query = "DELETE FROM CODES WHERE _id=" + id + ";"; // 레코드를 삭제하는 쿼리문 작성
        query(query); // 작성한 쿼리문 실행
    }

    /**
     * Database에서 바코드 텍스트를 모두 읽어 들여 MainActivity에서 사용할 RecyclerView에 맞게 가공하여 데이터를 전달하는 메소드.
     * @return Database에서 가져온 데이터 집합체
     */

    public ArrayList<ListData> getDatas() {
        SQLiteDatabase db = getReadableDatabase(); // DB 열기
        Cursor cursor = db.rawQuery("SELECT * FROM CODES", null); // DB에서 모든 레코드 선택
        ArrayList<ListData> datas = new ArrayList<>(); // 데이터를 담을 ArrayList 준비
        while(cursor.moveToNext()) { // 데이터의 끝에 도달할 때 까지
            String id = cursor.getString(0); // 레코드의 id 가져오기
            String rawdata = cursor.getString(1); // 레코드의 텍스트 가져오기
            ArrayList<Data> d = ParseData.parse(rawdata, context); // 레코드의 바코드 텍스트 파싱
            datas.add(new ListData(id, rawdata, d.get(3).getContent(), d.get(4).getContent(), d.get(5).getContent(), d.get(6).getContent()));
            // 레코드의 id, 바코드 텍스트, 출/도착 도시, 일자, 항공사를 포항하는 데이터를 ArrayList에 추가
        }
        cursor.close();
        db.close(); // DB 닫기
        Collections.reverse(datas); // 데이터를 최신 순으로 보여주기 위해 ArrayList를 역순 정렬
        return datas; // 수집한 데이터를 넘겨주기
    }
}
