package wadidejla.com.alfahresapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;

import com.wadidejla.newscreens.FragmentRollerAdapter;
import com.wadidejla.newscreens.ScreenUtils;
import com.wadidejla.newscreens.utils.TabDetails;
import com.wadidejla.settings.SystemSettingsManager;

import java.util.List;

/**
 * Created by snouto on 08/06/15.
 */
public class MainTabbedActivity extends ActionBarActivity implements ActionBar.TabListener {

    private ViewPager viewPager;
    private FragmentRollerAdapter adapter;
    private List<TabDetails> tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_alfahres_main);
        this.initView();
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
            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            this.finish();
            startActivity(logoutIntent);

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
