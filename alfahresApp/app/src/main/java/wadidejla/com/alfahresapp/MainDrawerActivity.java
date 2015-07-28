package wadidejla.com.alfahresapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.wadidejla.newscreens.IFragment;
import com.wadidejla.newscreens.NewOutgoingFilesFragment;
import com.wadidejla.newscreens.ScreenUtils;
import com.wadidejla.newscreens.adapters.TabDetailsArrayAdapter;
import com.wadidejla.newscreens.utils.TabDetails;
import com.wadidejla.settings.SystemSettingsManager;

import java.util.List;


public class MainDrawerActivity extends ActionBarActivity {


    private static final String BARCODE_ACTION = "com.barcode.sendBroadcast";
    private static final String BARCODE_PARAM = "BARCODE";

    private DrawerLayout  drawerLayout;
    private ListView mainListView;
    private Fragment currentFragment;
    private List<Fragment> fragments;
    private List<TabDetails> tabDetailsList;
    private TabDetailsArrayAdapter detailsArrayAdapter;
    private int currentPosition;
    private ActionBarDrawerToggle drawerToggle;
    private boolean mslideState = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);
        this.initView();
    }

    /**
     * This method will initialize the views of the current Drawer activity
     */
    private void initView() {

        try
        {
            //Initialize the Drawer Layout
            drawerLayout = (DrawerLayout)this.findViewById(R.id.drawer_layout);
            mainListView = (ListView) this.findViewById(R.id.drawer_list);
            //Then get the fragments associated with the current logged in user
            fragments = ScreenUtils.getFragments(this);
            tabDetailsList = ScreenUtils.getTabsFor(this);
            detailsArrayAdapter = new TabDetailsArrayAdapter(this,R.layout.tab_detail_item);
            detailsArrayAdapter.setTabDetailsList(tabDetailsList);
            mainListView.setAdapter(detailsArrayAdapter);
            detailsArrayAdapter.notifyDataSetChanged();
            //Set the tab Details Array Adapter
            SystemSettingsManager.createInstance(this).setTabDetailsArrayAdapter(detailsArrayAdapter);

            mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                    currentPosition = position;
                    //Update the current fragment
                    updateFragment();
                    //then close the drawer
                    drawerLayout.closeDrawer(mainListView);
                }
            });

            //Get the default view
            int defaultIndex = getDefaultFragment();

            if(defaultIndex != -1)
            {
                currentPosition = defaultIndex;
                updateFragment();
            }

            drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.drawable.ic_drawer,R.string.drawer_open,
                    R.string.drawer_close){

                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    mslideState = true;
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                    mslideState = false;
                }

            };

            drawerLayout.setDrawerListener(drawerToggle);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);




        }catch (Exception s)
        {
            Log.e("Error",s.getMessage());
        }

    }

    private int getDefaultFragment() {

        if(fragments == null || fragments.size() <=0) return -1;
        else
        {
            int defaultIndex = -1;

            for(int i = 0 ; i < fragments.size();i++)
            {
                if(fragments.get(i) instanceof  NewOutgoingFilesFragment)
                {
                    defaultIndex = i;
                    break;
                }
            }

            return defaultIndex;


        }



    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    private void updateFragment() {

        try
        {
            //get the fragment manager
            FragmentManager fragmentManager = getSupportFragmentManager();

            //get the fragment to view it
            currentFragment = fragments.get(currentPosition);

            String title = tabDetailsList.get(currentPosition).getTitle();

            this.setTitle(title);

            if(currentFragment != null)
            {
                //get the fragment transaction
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.replace(R.id.content_frame,currentFragment);
                fragmentTransaction.commit();
            }


        }catch (Exception s)
        {
            Log.e("Error",s.getMessage());
        }

    }

    @Override
    protected void onResume() {
        IntentFilter intentFilter = new IntentFilter(BARCODE_ACTION);
        registerReceiver(barcodeBroadcastReceiver, intentFilter);
        super.onResume();

    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(barcodeBroadcastReceiver);
        super.onDestroy();
    }

    private BroadcastReceiver barcodeBroadcastReceiver = new BroadcastReceiver() {


        MainDrawerActivity mainDrawerActivity = MainDrawerActivity.this;

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if(BARCODE_ACTION.equals(action)){
                String barcode = intent.getStringExtra(BARCODE_PARAM);

                barcode = barcode.trim();


                if(mainDrawerActivity != null)
                {
                    IFragment activeFragment = (IFragment)currentFragment;

                    if(activeFragment != null)
                        activeFragment.handleScanResults(barcode);

                }else
                {
                    //Notify the user with a toast
                    Toast.makeText(mainDrawerActivity,"There was a problem reading the barcode",
                            Toast.LENGTH_LONG)
                            .show();
                }



            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_app_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_settings) {
            Intent settingsIntent = new Intent(this,SettingsActivity.class);
            startActivity(settingsIntent);

            return true;
        }else if (item.getItemId() == R.id.btn_logout_main)
        {

            SystemSettingsManager.createInstance(this).logOut();
            //then go to the login screen
            Intent logoutIntent = new Intent(this,LoginScreen.class);
            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            this.finish();
            startActivity(logoutIntent);

        }else if (item.getItemId() == android.R.id.home)
        {
            toggleDrawer();
        }

        return true;
    }

    private void toggleDrawer() {

       try
       {

           if(mslideState)
           {
               drawerLayout.closeDrawer(Gravity.END);
               mslideState = false;
           }else
           {
               drawerLayout.openDrawer(Gravity.START);
               mslideState  = true;
           }



       }catch (Exception s)
       {
           Log.e("Error",s.getMessage());
       }
    }
}
