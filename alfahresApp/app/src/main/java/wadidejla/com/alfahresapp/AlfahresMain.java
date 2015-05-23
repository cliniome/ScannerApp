package wadidejla.com.alfahresapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.degla.restful.models.RestfulFile;
import com.degla.restful.models.http.HttpResponse;
import com.google.gson.reflect.TypeToken;
import com.wadidejla.barcode.IntentIntegrator;
import com.wadidejla.barcode.IntentResult;
import com.wadidejla.network.AlfahresConnection;
import com.wadidejla.preferences.AlfahresPreferenceManager;
import com.wadidejla.screens.FilesArrayAdapter;
import com.wadidejla.screens.MainFilesScreenFragment;
import com.wadidejla.screens.ScreenRouter;
import com.wadidejla.screens.SectionsPagerAdapter;
import com.wadidejla.settings.SystemSettingsManager;

import org.apache.http.protocol.HTTP;


public class AlfahresMain extends ActionBarActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alfahres_main);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        this.init();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);

        if(result != null)
        {
            Toast.makeText(this,String.format("Format:%s , BarCode : %s",result.getFormatName(),result.getContents())
            ,Toast.LENGTH_LONG).show();

        }
    }

    private void init()
    {
        List<Fragment> fragList = ScreenRouter.getFragments(this);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),fragList);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        this.initMainScreen();
    }

    private void initMainScreen() {

        //set the Settings Manager listener - On Shared Preference Change Listener
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        AlfahresPreferenceManager manager = new AlfahresPreferenceManager(this);
        prefs.registerOnSharedPreferenceChangeListener(manager);

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        ScreenRouter.getPersonalizedMenu(this,getMenuInflater(),menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this,SettingsActivity.class);
            startActivity(settingsIntent);

            return true;
        }else if (id == R.id.btn_logout_main)
        {
            //TODO : add your logic to logout in here
            SystemSettingsManager.createInstance(this).logOut();
            //then go to the login screen
            Intent logoutIntent = new Intent(this,LoginScreen.class);
            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            this.finish();
            startActivity(logoutIntent);
        }else if (id == R.id.scan_settings)
        {
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.initiateScan();
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */




  /*  *//**
     * A placeholder fragment containing a simple view.
     *//*
    public static class PlaceholderFragment extends Fragment {
        *//**
         * The fragment argument representing the section number for this
         * fragment.
         *//*
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final int MAIN_FRAGMENT = 1;
        private static final int TEMPORARY_FRAGMENT=2;



        *//**
         * Returns a new instance of this fragment for the given section
         * number.
         *//*
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
           *//* View rootView = inflater.inflate(R.layout.fragment_alfahres_main, container, false);
            TextView view = (TextView)rootView.findViewById(R.id.section_label);
            String sectionNumber = this.getArguments().get(ARG_SECTION_NUMBER).toString();
            view.setText(String.format("%s %s","Section",sectionNumber));*//*

            int sectionNumber = Integer.parseInt(this.getArguments().get(ARG_SECTION_NUMBER).toString());
            View rootView = null;

            switch (sectionNumber)
            {
                case MAIN_FRAGMENT:
                    rootView = inflater.inflate(R.layout.main_files_layout,container,false);

                    break;
                case TEMPORARY_FRAGMENT:
                    //TODO : Implement the temporary fragment in here
                    break;
            }

            return rootView;
        }


    }*/

}
