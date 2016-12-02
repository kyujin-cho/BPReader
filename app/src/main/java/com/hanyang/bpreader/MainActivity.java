package com.hanyang.bpreader;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.hanyang.bpreader.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements CreateFragment.OnFragmentInteractionListener, ScanFragment.OnFragmentInteractionListener, TabLayout.OnTabSelectedListener, ViewPager.OnPageChangeListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    ActionBar actionBar;
    DBManager manager;
    AirlineManager airline_manager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText("SCAN"));
        tabLayout.addTab(tabLayout.newTab().setText("CREATE"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setOnTabSelectedListener(this);

        manager = new DBManager(getApplicationContext(), "airport.db", null, 1);
        airline_manager = new AirlineManager(getApplicationContext(), "airline.db", null, 1);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(item.getItemId()) {
            case R.id.action_settings:
                break;
            case R.id.action_update_db:
                new UpdateDBTask().execute(0);
                break;
            case R.id.action_update_airline:
                new UpdateDBTask().execute(1);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageSelected(int position) {
        tabLayout.getTabAt(position).select();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    protected class UpdateDBTask extends AsyncTask<Integer, Integer, String> {
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

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
                Toast.makeText(MainActivity.this, "Successfully updated airport Database.", Toast.LENGTH_SHORT).show();
             else
                Toast.makeText(MainActivity.this, "Failed to fetch DB.", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new ScanFragment();
                case 1:
                    return new CreateFragment();
                default:
                    return null;
            }
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SCAN";
                case 1:
                    return "CREATE";
            }
            return null;
        }
    }
}
