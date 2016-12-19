package com.hanyang.bpreader;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        manager = new BPManager(this, "code.db", null, 1);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mData = manager.getDatas();
        recyclerView = (FixedRecyclerView) findViewById(R.id.codesRecyclerView);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new CodesRecyclerViewAdapter(this, mData);
        recyclerView.setAdapter(mAdapter);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.mainSwipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestRefresh();
                Log.i("MainActivity", "Refresh Requested.");
            }
        });

        registerForContextMenu(recyclerView);
    }

    void requestRefresh() {
        mData.clear();
        mData.addAll(manager.getDatas());
        swipeContainer.setRefreshing(false);
        mAdapter.notifyDataSetChanged();
        manager = new DBManager(getApplicationContext(), "airport.db", null, 1);
        airline_manager = new AirlineManager(getApplicationContext(), "airline.db", null, 1);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, 1, 0, "삭제");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final FixedRecyclerView.RecyclerContextMenuInfo info = (FixedRecyclerView.RecyclerContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case 1:
                new AlertDialog.Builder(this)
                        .setMessage("정말 삭제하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                manager.deleteRecord(MainActivity.this.mData.get(info.position).getId());
                                requestRefresh();
                            }
                        }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();
                break;
        }
        return super.onContextItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Intent intent;
        switch (item.getItemId()) {
            case R.id.nav_camera:
                IntentIntegrator integrator = new IntentIntegrator(this);
                integrator.setBeepEnabled(true);
                integrator.initiateScan();
                break;
            case R.id.nav_text:
                final EditText text = new EditText(this);
                text.setHint("Barcode text");
                new AlertDialog.Builder(this)
                        .setMessage("Input barcode text")
                        .setView(text)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                                intent.putExtra("data", text.getText().toString());
                                startActivity(intent);
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
                break;
            case R.id.nav_create:
                final EditText create_text = new EditText(this);
                create_text.setHint("Barcode text");
                new AlertDialog.Builder(this)
                        .setMessage("Input barcode text")
                        .setView(create_text)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                 Intent intent = new Intent(MainActivity.this, CreateActivity.class);
                                intent.putExtra("data", create_text.getText().toString());
                                startActivity(intent);
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
                break;
            case R.id.nav_alarm:
                break;
            case R.id.nav_share:
                break;
            case R.id.action_update_db:
                new UpdateDBTask(this).execute(0);
                break;
            case R.id.action_update_airline:
                new UpdateDBTask(this).execute(1);
                break;

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null && result.getContents() != null) {
            Intent intent = new Intent(this, ScanActivity.class);
            intent.putExtra("data", result.getContents());
            startActivity(intent);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
