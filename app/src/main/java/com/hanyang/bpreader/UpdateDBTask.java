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

/**
 * Created by kyujin on 14/12/2016.
 */

class UpdateDBTask extends AsyncTask<Integer, Integer, String> {
    Context context;
    ProgressDialog progressDialog;
    DBManager manager;
    AirlineManager airline_manager;
    public UpdateDBTask(Context context) {
        this.context = context;
        progressDialog = new ProgressDialog(context);
        manager = new DBManager(context, "airport.db", null, 1);
        airline_manager = new AirlineManager(context, "airline.db", null, 1);
    }

    @Override
    protected void onPreExecute() {
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("DB를 교체하는 중입니다...");
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Integer... params) {
        String result = "";
        try {
            String url = "";
            if(params[0] == 0)
                url = "http://thy2134.duckdns.org/airports.dat";
            else if(params[0] == 1)
                url = "http://thy2134.duckdns.org/airlines.dat";
            Document doc = Jsoup.connect(url)
                    .ignoreContentType(true)
                    .get();
//                Log.i("MainActivity", doc.text());
            result = doc.text();
            Log.i("MainActivity", "Total Size: " + result.length());

            String[] datas = result.split("!");
            Log.i("UpdateDBTask", "datas: " + Arrays.toString(datas));
            int size = datas.length;
            int i = 0;
            for(String d : datas) {
                d = d.replace("'", "''");
                Log.i("UpdateDBTask", "Row: " + d);
                String[] row = d.split("\\^");
                Log.i("UpdateDBTask", Arrays.toString(row));
                if(params[0] == 0)
                    manager.updateDatabase(row[1].replace("NAME#", ""), row[2].replace("LAT#", ""),
                            row[3].replace("LON#", ""), row[4].replace("COUT#", ""), row[5].replace("IATA#", ""));
                else if(params[0] == 1)
                    airline_manager.updateDatabase(row[1].replace("NAME#", ""), row[2].replace("#COUT", ""), row[3].replace("IATA#", ""));
                i++;
                Log.i("UpdateDBTask", "i: " + i);
                publishProgress((int)(((float)i/size)*100));
            }
            result = "COMPLETE";
        } catch (IOException e) {
            e.printStackTrace();
            result = "NO_DATA";
        }
        return result;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        Log.i("UpdateDBTask", "Progress: " + values[0]);
        progressDialog.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        progressDialog.dismiss();
        if(!s.equals("NO_DATA"))
            Toast.makeText(context, "Successfully updated airport Database.", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context, "Failed to fetch DB.", Toast.LENGTH_SHORT).show();

    }
}