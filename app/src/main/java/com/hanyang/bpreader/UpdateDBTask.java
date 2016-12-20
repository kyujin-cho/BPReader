/** UpdateDBTask
 * 웹 서버에서 DB 파일을 받아와 업데이트
 * @author Kyujin Cho
 * @version 1.0
 * @see com.hanyang.bpreader.MainActivity
 */

package com.hanyang.bpreader;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Arrays;

class UpdateDBTask extends AsyncTask<Integer, Integer, String> {
    Context context;
    ProgressDialog progressDialog;
    DBManager manager;
    AirlineManager airline_manager;

    /**
     * 생성자 메소드. Progress Dialog 생성, Toast 알림 출력, DB 접근을 위해 Context를 받아둠
     * @param context Activity Context
     */
    public UpdateDBTask(Context context) {
        this.context = context;
        progressDialog = new ProgressDialog(context); // 업데이트 진행도를 보여줄 Progress Dialog 생성
        manager = new DBManager(context, "airport.db", null, 1); // 공항 DB 접근
        airline_manager = new AirlineManager(context, "airline.db", null, 1); // 항공사 DB 접근
    }

    @Override
    protected void onPreExecute() {
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); // 진행도 바 생성
        progressDialog.setMessage("DB를 교체하는 중입니다..."); // 메세지 생성
        progressDialog.show(); // 알림 보여주기
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Integer... params) {
        String result = "";
        try {
            String url = "";
            if(params[0] == 0) // 0번 작업을 실행하라고 할 경우 - 공항명 업데이트
                url = "http://thy2134.duckdns.org/airports.dat";
            else if(params[0] == 1) // 1번 작업을 실행하라고 할 경우 - 항공사명 업데이트
                url = "http://thy2134.duckdns.org/airlines.dat";
            Document doc = Jsoup.connect(url)
                    .ignoreContentType(true)
                    .get(); // 서버에 접속하여 파일을 받아옴
//                Log.i("MainActivity", doc.text());
            result = doc.text();
            Log.i("UpdateDB", "Total Size: " + result.length());

            String[] datas = result.split("!"); // 파일을 구분자 ! 단위로 나눔
            Log.i("UpdateDB", "datas: " + Arrays.toString(datas));
            int size = datas.length;
            int i = 0;
            for(String d : datas) { // 나눈 데이터 하나에 대하여
                d = d.replace("'", "''");
                String[] row = d.split("\\^");
                if(params[0] == 0) // 공항 DB를 업데이트할 경우
                    manager.updateDatabase(row[1].replace("NAME#", ""), row[2].replace("LAT#", ""),
                            row[3].replace("LON#", ""), row[4].replace("COUT#", ""), row[5].replace("IATA#", "")); // 공항 DB 데이터를 잘라와서 업데이트
                else if(params[0] == 1) // 항공사 DB를 업데이트할 경우
                    airline_manager.updateDatabase(row[1].replace("NAME#", ""), row[2].replace("#COUT", ""), row[3].replace("IATA#", "")); // 항공사 DB 데이터를 잘라와서 업데이트
                i++;
                publishProgress((int)(((float)i/size)*100)); // 진행도 증가
            }
            result = "COMPLETE"; // 작업이 성공적으로 끝났으므로 완료되었다는 알림 보내줌
        } catch (IOException e) {
            e.printStackTrace();
            result = "NO_DATA";
        }
        return result;
    }

    /**
     * 진행도를 업데이트 해주는 메소드
     * @param values 진행도 절대 값
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        progressDialog.setProgress(values[0]);
    }

    /**
     * 업데이트 작업이 끝난 이후 성공 여부를 전달해주는 메소드
     * @param s 성공/실패를 나타내주는 문자열
     */
    @Override
    protected void onPostExecute(String s) {
        progressDialog.dismiss();
        if(!s.equals("NO_DATA"))
            Toast.makeText(context, "Successfully updated airport Database.", Toast.LENGTH_SHORT).show(); // 성공했을 경우 성공했다는 알림 출력
        else
            Toast.makeText(context, "Failed to fetch DB.", Toast.LENGTH_SHORT).show(); // 실패했을 경우 실패했다는 알림 출력

    }
}