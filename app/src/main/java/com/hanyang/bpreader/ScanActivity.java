/**
 * ScanActivity
 * zxing 의 Barcode Scan / Manual Text Input / 저장된 Text를 가져오기 등을 통해서 입력된 Barcode Text를 파싱해서 사람이 볼 수 있는 데이터 집합체로 만들어 주는 Activity
 * @author Kyujin Cho
 * @version 1.0
 * @contributor Jaehyeon Ahn
 * @see com.hanyang.bpreader.MainActivity
 * @see com.hanyang.bpreader.ParseData
 */

package com.hanyang.bpreader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ScanActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    ArrayList<Data> mData;
    DBManager manager;
    AirlineManager airline_manager;
    BPManager bpmanager;
    boolean data_loaded = false;
    TextView dateView;
    TextView departureTextView;
    TextView arrivalTextView;
    boolean existing_data = false;

    /**
     * Activity가 처음 생성될 때 실행되는 메소드. 초기 변수 및 UI Component의 전역 변수들을 정의.
     * 여기서는 파싱된 데이터가 표시될 RecyclerView에 관련된 변수를 초기화 및 제어하고, 데이터를 가져온다.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        mData = new ArrayList<>(); // RecyclerView에서 사용될 Data가 저장될 ArrayList

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView); // XML Layout에서 recyclerView를 찾아옴
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);  // RecyclerView의 레이아웃 매니저 정의 (LinearLayout 사용)
        recyclerView.setLayoutManager(mLayoutManager); // recyclerView에 mLayoutManager 연결
        mAdapter = new RecyclerViewAdapter(this, mData); // RecyclerViewAdapter 객체 정의
        recyclerView.setAdapter(mAdapter); // RecyclerView에 Adapter 연결
        manager = new DBManager(this, "airport.db", null, 1); // 입력된 Text를 저장할 Database 오픈
        airline_manager = new AirlineManager(this, "airline.db", null, 1); // Airline의 Full Name이 저장되어있는 Database 오픈
        bpmanager = new BPManager(this, "code.db", null, 1); // 공항의 FUll Name이 저장되어 있는 Database 오픈
        Intent intent = getIntent(); // 액티비티가 실행될 때 전달된 값을 가져오기 위해 Intent 가져옴
        String data = intent.getStringExtra("data"); // 가져온 Intent에서 "data"라는 이름의 String 가져옴
        existing_data = intent.getBooleanExtra("prev", false); // 입력된 String이 이전에 존재했던 String인지 아닌지를 판단하는 변수 가져옴
        dateView = (TextView) findViewById(R.id.dateView); // XML Layout에서 날짜를 표시할 TextView 가져옴
        departureTextView = (TextView) findViewById(R.id.departureTextView); // XML Layout에서 출발 공항을 표시할 TextView 가져옴
        arrivalTextView = (TextView) findViewById(R.id.arrivalTextView); // XML Layout에서 출발 공항을 표시할 TextView 가져옴
        if(data != null) { // 가져온 Data가 NULL이 아니면
            parse(data); // Data를 파싱
        } else {
            finish();
        }
    }

    /**
     * 입력된 Raw Text를 사람이 읽을 수 있는 형식의 데이터 뭉치로 변경하여 RecyclerView에 추가해 주는 메소드
     * @param data Barcode에서 추출할 Raw Text
     */

    public void parse(String data) {
        mData.clear();
        mData.addAll(ParseData.parse(data, this)); // ParseData 객체를 생성하여 데이터를 파싱해온 후, 파싱된 데이터를 RecyclerView의 Data에 모두 추가
        mAdapter.notifyDataSetChanged(); // Adapter에 data가 추가되었음을 알림
        data_loaded = true;
        if(!existing_data) // 입력된 String이 Barcode Scan이나 Text Input을 통해 새로 입력된 Text이면
            bpmanager.addString(data); // Database에 Text 추가
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd"); // 입력된 일자를 가져오기
            Date d = sdf.parse(mData.get(7).getContent().substring(0, 5));
            SimpleDateFormat format = new SimpleDateFormat("MMM d", Locale.US); // 일자를 보기 편하게 변경
            dateView.setText(format.format(d)); // 변경된 일자를 화면에 표시
        } catch (ParseException e) {
            e.printStackTrace();
            dateView.setText("NONE");
        }
        departureTextView.setText(mData.get(3).getContent()); // 출발 공항을 화면에 표시
        arrivalTextView.setText(mData.get(4).getContent()); // 도착 공항을 화면에 표시
    }
}
