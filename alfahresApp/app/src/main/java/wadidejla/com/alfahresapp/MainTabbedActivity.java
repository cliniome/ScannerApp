package wadidejla.com.alfahresapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Layout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.wadidejla.barcode.IntentIntegrator;
import com.wadidejla.barcode.IntentResult;
import com.wadidejla.newscreens.Archiver;
import com.wadidejla.newscreens.FragmentRollerAdapter;
import com.wadidejla.newscreens.IFragment;
import com.wadidejla.newscreens.ScreenUtils;
import com.wadidejla.newscreens.utils.BarcodeUtils;
import com.wadidejla.newscreens.utils.ScannerUtils;
import com.wadidejla.newscreens.utils.TabDetails;
import com.wadidejla.settings.SystemSettingsManager;

import java.util.List;

/**
 * Created by snouto on 08/06/15.
 */
public class MainTabbedActivity extends ActionBarActivity implements ActionBar.TabListener {


    private static final String BARCODE_ACTION = "com.barcode.sendBroadcast";
    private static final String BARCODE_PARAM = "BARCODE";


    private ViewPager viewPager;
    private FragmentRollerAdapter adapter;
    private List<TabDetails> tabs;

    public static String SCANNED_ARCHIVER_FILE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_alfahres_main);
        this.initView();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            moveTaskToBack(true);
            return true;
        }
        else
            return super.onKeyDown(keyCode, event);
    }

    private void initView() {

        try
        {


            //access the view
            setViewPager((ViewPager)this.findViewById(R.id.pager));

            this.getViewPager().setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                    getSupportActionBar().setSelectedNavigationItem(position);

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            //set the navigation mode to tabs
            if(getSupportActionBar() != null)
            {
                getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            }

            this.setAdapter(new FragmentRollerAdapter(getSupportFragmentManager(),this));
            this.getViewPager().setAdapter(this.getAdapter());
            //set the tabs
            this.setTabs(ScreenUtils.getTabsFor(this));

            if(getTabs() != null && getTabs().size() > 0)
            {
                for(TabDetails details : getTabs())
                {
                    ActionBar.Tab tab = getSupportActionBar().newTab();
                    tab.setIcon(details.getIcon());
                    /*tab.setText(details.getTitle());*/
                    tab.setTabListener(this);
                    getSupportActionBar().addTab(tab);
                }
            }



        }catch (Exception s)
        {
            s.printStackTrace();

        }

    }




    /////////////////////////////////////////Barcode Scanning Section//////////////////////////////


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


        MainTabbedActivity tabbedActivity = MainTabbedActivity.this;

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if(BARCODE_ACTION.equals(action)){
                String barcode = intent.getStringExtra(BARCODE_PARAM);

                barcode = barcode.trim();


                if(tabbedActivity != null)
                {
                    IFragment activeFragment = (IFragment)tabbedActivity.getAdapter().getItem(tabbedActivity
                            .getViewPager().getCurrentItem());

                    if(activeFragment != null)
                        activeFragment.handleScanResults(barcode);

                }else
                {
                    //Notify the user with a toast
                    Toast.makeText(tabbedActivity,"There was a problem reading the barcode",
                            Toast.LENGTH_LONG)
                            .show();
                }



            }
        }
    };

    ///////////////////////////////////////End of Barcode Scanning Section///////////////////////

    //Handle on activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);


        BarcodeUtils barcodeUtils = new BarcodeUtils(result.getContents());

        if(barcodeUtils.isShelf())
        {
            //That means the current Task is archiving task
            if(this.getViewPager() != null)
            {
                try
                {
                    Archiver currentFragment = (Archiver)
                            this.getAdapter().getItem(this.getViewPager().getCurrentItem());

                    if(currentFragment != null && SCANNED_ARCHIVER_FILE != null)
                    {
                    /*Object fileNumber = data.getExtras().get(ScannerUtils.ARCHIVER_FILE_NUMBER);*/
                        if(SCANNED_ARCHIVER_FILE == null) return;
                        currentFragment.handleShelfBarcode(SCANNED_ARCHIVER_FILE,result.getContents());
                    }

                }catch (Exception s)
                {
                    s.printStackTrace();
                }

            }


        }else
        {
            try
            {
                if(this.getViewPager() != null)
                {
                    IFragment currentFragment = (IFragment)this.getAdapter().getItem(this.getViewPager().getCurrentItem());

                    if(currentFragment != null)
                        currentFragment.handleScanResults(result.getContents());
                }

            }catch (Exception s)
            {
                s.printStackTrace();
            }

        }




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.new_app_menu,menu);


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
            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(logoutIntent);
            this.finish();

        }

        return true;
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
    }

    public FragmentRollerAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(FragmentRollerAdapter adapter) {
        this.adapter = adapter;
    }


    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

        this.viewPager.setCurrentItem(tab.getPosition());

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    public List<TabDetails> getTabs() {
        return tabs;
    }

    public void setTabs(List<TabDetails> tabs) {
        this.tabs = tabs;
    }
}
