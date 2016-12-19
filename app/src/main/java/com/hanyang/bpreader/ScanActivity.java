package com.hanyang.bpreader;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        mData = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecyclerViewAdapter(this, mData);
        recyclerView.setAdapter(mAdapter);
        manager = new DBManager(this, "airport.db", null, 1);
        airline_manager = new AirlineManager(this, "airline.db", null, 1);
        bpmanager = new BPManager(this, "code.db", null, 1);
        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        existing_data = intent.getBooleanExtra("prev", false);
        dateView = (TextView) findViewById(R.id.dateView);
        departureTextView = (TextView) findViewById(R.id.departureTextView);
        arrivalTextView = (TextView) findViewById(R.id.arrivalTextView);
        if(data != null) {
            parse(data);
        } else {
            finish();
        }
    }

    public void parse(String data) {
        mData.clear();
        ParseData p = new ParseData(data, this);
        for(Data m : p.mData)
            mData.add(m);
        mAdapter.notifyDataSetChanged();
        data_loaded = true;
        if(!existing_data)
            bpmanager.addString(data);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
            Date d = sdf.parse(mData.get(7).getContent().substring(0, 5));
            SimpleDateFormat format = new SimpleDateFormat("MMM d", Locale.US);
            dateView.setText(format.format(d));
        } catch (ParseException e) {
            e.printStackTrace();
            dateView.setText("NONE");
        }
        departureTextView.setText(mData.get(3).getContent());
        arrivalTextView.setText(mData.get(4).getContent());
    }
}
