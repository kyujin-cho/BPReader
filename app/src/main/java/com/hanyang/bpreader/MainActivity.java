
/**
 * MainActivity
 * 저장된 보딩 패스 목록 및, 새 보딩 패스 스캔과 보딩 패스 바코드 만드는 Sidebar UI를 담당하는 메인 화면
 * 참고
 * https://github.com/zxing/zxing
 * @author Kyujin Cho
 * @contributor Jaehyeon Ahn
 * @version 1.0
 * @see com.hanyang.bpreader.CreateActivity
 * @see com.hanyang.bpreader.ScanActivity
 */

package com.hanyang.bpreader;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    BPManager manager;
    FixedRecyclerView recyclerView;
    FixedRecyclerView.Adapter mAdapter;
    FixedRecyclerView.LayoutManager mLayoutManager;
    ArrayList<ListData> mData;
    SwipeRefreshLayout swipeContainer;
    AirlineManager airline_manager;

    /**
     * Activity가 처음 생성될 때 실행되는 메소드. 초기 변수 및 UI Component의 전역 변수들을 정의.
     * 여기서는 리스트뷰 역할을 하는 recyclerView를 정의하고, DB에서 데이터를 가져와 RecyclerView에 넣어주는 역할을 한다.
     * 또한, swipeContainer를 정의하여, RecyclerView 목록을 잡아당기면 리스트가 새로고침 되도록 한다.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        manager = new BPManager(this, "code.db", null, 1); // Scan하여 저장된 바코드를 꺼내올 DB 객체
        airline_manager = new AirlineManager(getApplicationContext(), "airline.db", null, 1); 

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout); // Navigation Drawer를 사용하기 위하여 정의
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mData = manager.getDatas(); // Database에서 저장된 Code들을 모두 가져와 RecyclerView에서 사용할 리스트에 넣어놓음
        recyclerView = (FixedRecyclerView) findViewById(R.id.codesRecyclerView); // XML Layout에서 recyclerView를 찾아옴
        recyclerView.setHasFixedSize(true); 
        mLayoutManager = new LinearLayoutManager(this); // RecyclerView의 레이아웃 매니저 정의 (LinearLayout 사용)
        recyclerView.setLayoutManager(mLayoutManager); // recyclerView에 mLayoutManager 연결
        mAdapter = new CodesRecyclerViewAdapter(this, mData); // RecyclerViewAdapter 객체 정의
        recyclerView.setAdapter(mAdapter); // recyclerView에 Adapter 연결
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.mainSwipeContainer); // XML Layout에서 SwipeContainer를 찾아옴
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() { // SwipeContainer가 작동할 시 행동을 정의
            @Override
            public void onRefresh() {
                requestRefresh(); // RecyclerView 새로고침 요청
            }
        });

        registerForContextMenu(recyclerView); // ContextMenu에 RecyclerView를 등록함으로써 RecyclerView의 아이템을 길게 클릭할 시 지정된 ContextMenu가 뜨도록 함
    }

    /**
     *  RecyclerView의 내용물을 업데이트하는 메소드.
     * DB에 접속하여 데이터를 가져온 후, 가져온 데이터를 RecyclerView의 데이터를 담당하는 mData에 그대로 넣어준다.
     */
    void requestRefresh() {
        mData.clear(); // 기존 데이터를 모두 제거
        mData.addAll(manager.getDatas()); // DB에서 모든 데이터를 가져온 후 ArrayList에 가져온 데이터를 추가
        swipeContainer.setRefreshing(false); // 새로고침 액션을 종료
        mAdapter.notifyDataSetChanged(); // RecyclerView의 Adapter에 데이터 셋이 변경되었음을 알려 리스트가 업데이트 되게 함
    }

    /**
     * Context Menu(RecylcerView의 아이템을 길게 클릭할 시 나타나는 메뉴)를 정의하는 메소드.
     * 삭제 아이템을 추가하여 DB의 내용물을 삭제할 수 있도록 한다.
     * @param menu 아이템이 추가될 메뉴
     * @param v 메뉴의 view
     * @param menuInfo ContextMenu의 정보
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, 1, 0, "삭제"); // Context Menu에 삭제 메뉴 추가
    }


    /**
     * Context Menu의 Item이 선택되었을 때 행할 메소드.
     * 여기서는 삭제 버튼을 눌렀을 때, 확인 창을 띄워 이중 체크 후에 삭제를 진행한다.
     * @param item 선택 ContextItem
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final FixedRecyclerView.RecyclerContextMenuInfo info = (FixedRecyclerView.RecyclerContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) { // 삭제가 선택되었을 경우
            case 1:
                new AlertDialog.Builder(this) // 알림 다이얼로그 생성
                        .setMessage("정말 삭제하시겠습니까?") // 문구 지정
                        .setPositiveButton("예", new DialogInterface.OnClickListener() { // 예 버튼을 눌렀을 경우
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                manager.deleteRecord(MainActivity.this.mData.get(info.position).getId()); // 선택된 menu의 id를 가져와 mData에서 레코드의 id를 가져온 후, DB에서 해당 레코드 제거
                                requestRefresh(); // 리스트 내용물이 변경되었으므로 새로고침
                            }
                        }).setNegativeButton("아니오", new DialogInterface.OnClickListener() { // 아니오 버튼을 눌렀을 경우
                    @Override
                    public void onClick(DialogInterface dialog, int which) { // 아무것도 하지 않고 창이 닫힌다!

                    }
                }).create().show(); // 알림 다이얼로그 보여주기
                break;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * Sidebar UI의 Item이 선택되었을 때 행할 메소드.
     * item의 id로 switch문을 달아, 선택된 id 별로 각각 바코드 스캔/텍스트 입력/바코드 생성 과정으로 이동한다.
     * @param item 선택된 Navigation Bar 의 item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Intent intent;
        switch (item.getItemId()) {
            case R.id.nav_camera: // 스캔 항목이 선택되었을 때
                IntentIntegrator integrator = new IntentIntegrator(this); // 스캔을 위해 IntentIntegrator 생성
                integrator.setBeepEnabled(true); // 바코드를 찾았을 때 비프음이 나오게 함
                integrator.initiateScan(); // 스캔 작업 시작
                break;
            case R.id.nav_text: // 텍스트를 통한 직접 입력이 선택되었을 때
                final EditText text = new EditText(this); // 텍스트 입력을 위한 입력창 생성
                text.setHint("Barcode text"); // Hint(안내 문구) 설정
                new AlertDialog.Builder(this) // 텍스트 입력을 위한 다이얼로그 생성
                        .setMessage("Input barcode text") // 다이얼로그 안내 문구 설정
                        .setView(text) // 다이얼로그에 TextView 탑재
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() { // OK 버튼을 눌렀을 시
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(MainActivity.this, ScanActivity.class); // ScanActivity로 이동을 위한 Intent 생성
                                intent.putExtra("data", text.getText().toString()); // Intent 전환 시 data를 들고 이동
                                startActivity(intent); // ScanActivity 시작
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() { // Cancel 버튼을 눌렀을 시 
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) { 
                    // 아무것도 하지 않고 창이 닫힌다!
                    }
                }).show();
                break;
            case R.id.nav_create: // 텍스트를 통해 바코드 생성 항목이 선택되었을 때
                final EditText create_text = new EditText(this); // 텍스트 입력을 위한 입력창 생성
                create_text.setHint("Barcode text"); // Hint(안내 문구) 설정
                new AlertDialog.Builder(this) // 텍스트 입력을 위한 다이얼로그 생성
                        .setMessage("Input barcode text") // 다이얼로그 안내 문구 설정
                        .setView(create_text) // 다이얼로그에 TextView 탑재
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() { // OK 버튼을 눌렀을 시
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(MainActivity.this, CreateActivity.class); // CreateActivity로 이동을 위한 Intent 생성
                                intent.putExtra("data", create_text.getText().toString()); // Intent 전환 시 data를 들고 이동
                                startActivity(intent); // Activity 이동
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() { // Cancel 버튼을 눌렀을 시 
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) { 
                    // 아무것도 하지 않고 창이 닫힌다!
                    }
                }).show();
                break;
            case R.id.action_update_db: // 공항 Database를 업데이트 하는 항목이 선택되었을 때
                new UpdateDBTask(this).execute(0); // AsyncTask를 실행하여 DB 업데이트
                break;
            case R.id.action_update_airline: // 항공사 Database를 업데이트 하는 항목이 선택되었을 때
                new UpdateDBTask(this).execute(1); // AsyncTask를 실행하여 DB 업데이트
                break;

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout); // Drawer를 닫기 위해 Drawer 가져오기
        drawer.closeDrawer(GravityCompat.START); // Item이 선택되었으므로 Drawer를 닫음
        return true;
    }

    /**
     * MainActivity에서 Back 버튼을 눌렀을때 행할 메소드.
     * Navigation Bar가 열려 있으면 닫아주고, 그렇지 않으면 Activity를 종료한다.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout); // Drawer 상태를 알기 위해 Drawer 가져오기
        if (drawer.isDrawerOpen(GravityCompat.START)) { // Drawer가 열려있는지를 파악
            drawer.closeDrawer(GravityCompat.START); // 열려있으면 Drawer를 닫아줌
        } else {
            super.onBackPressed(); // 닫혀있으면 Activity를 종료
        }
    }

    /**
     * Action Bar에 3점 메뉴를 추가해주는 메소드.
     * @param menu 3점 메뉴에 추가할 메뉴
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * zxing 라이브러리에서 바코드를 스캔하였을 때 이동하는 메소드.
     * 스캔한 바코드에서 추출한 텍스트를 전달 받아, ScanActivity로 이동한다.
     * @param requestCode zxing을 통해 카메라로 스캔을 진행하라는 요청 코드
     * @param resultCode zxing을 통해 카메라로 스캔을 완료했음을 나타내는 완료 코드
     * @param data zxing을 통해 스캔한 바코드의 내용물이 들어있는 Intent
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data); // Scan의 결과를 가져옴
        if(result != null && result.getContents() != null) { // Scan 결과가 null이 아닐 시 => Scan이 성공적으로 이루어졌을 시
            Intent intent = new Intent(this, ScanActivity.class); // ScanActivity로 전환하기 위한 준비
            intent.putExtra("data", result.getContents()); // Intent 전환 시 data를 들고 이동
            startActivity(intent); // ScanActivity 시작
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
