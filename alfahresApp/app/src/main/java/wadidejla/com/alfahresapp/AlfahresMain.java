package wadidejla.com.alfahresapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

import com.degla.restful.models.FileModelStates;
import com.degla.restful.models.RestfulEmployee;
import com.degla.restful.models.RestfulFile;
import com.degla.restful.models.http.HttpResponse;
import com.google.gson.reflect.TypeToken;
import com.wadidejla.barcode.IntentIntegrator;
import com.wadidejla.barcode.IntentResult;
import com.wadidejla.db.FilesDBManager;
import com.wadidejla.network.AlfahresConnection;
import com.wadidejla.preferences.AlfahresPreferenceManager;
import com.wadidejla.screens.CoordinatorCollectionFragment;
import com.wadidejla.screens.FilesArrayAdapter;
import com.wadidejla.screens.LocalSyncFilesFragment;
import com.wadidejla.screens.MainFilesScreenFragment;
import com.wadidejla.screens.ScanAndReceiveFragment;
import com.wadidejla.screens.ScreenRouter;
import com.wadidejla.screens.SectionsPagerAdapter;
import com.wadidejla.screens.ViewPagerSlave;
import com.wadidejla.settings.SystemSettingsManager;
import com.wadidejla.tasks.ManualSyncTask;
import com.wadidejla.tasks.MarkingTask;
import com.wadidejla.tasks.ScanAndReceiveTask;
import com.wadidejla.utils.AlFahresFilesManager;
import com.wadidejla.utils.EmployeeUtils;
import com.wadidejla.utils.RoleTypes;

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
    private List<Fragment> fragmentList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alfahres_main);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        this.init();

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        Fragment currentFragment = fragmentList.get(mViewPager.getCurrentItem());

        if(currentFragment != null)
        {
            if(currentFragment instanceof LocalSyncFilesFragment)
            {
                menu.findItem(R.id.sync_btn).setVisible(true);

            }else if (currentFragment instanceof ScanAndReceiveFragment)
            {
                menu.findItem(R.id.sync_btn).setVisible(false);
                SystemSettingsManager settingsManager = SystemSettingsManager.createInstance(this);
                try
                {

                    RestfulEmployee emp = (RestfulEmployee)settingsManager.getAccount();

                    if(emp != null)
                    {
                        if(!emp.getRole().equalsIgnoreCase(RoleTypes.RECEPTIONIST.toString()))
                        {
                            menu.findItem(R.id.markFiles).setVisible(true);
                        }
                    }

                }catch (Exception s)
                {
                    Log.w("AlfahresMain",s.getMessage());
                }
            }
        }

        return true;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        Fragment currentFragment = null;

        if(fragmentList != null)
        {
            currentFragment = fragmentList.get(mViewPager.getCurrentItem());

        }

        if(result != null && resultCode == Activity.RESULT_OK)
        {


           if (currentFragment instanceof  LocalSyncFilesFragment)
           {
               final AlertDialog dlg = new AlertDialog.Builder(this)
                       .setTitle(R.string.main_loading_title)
                       .setMessage("Updating files...")
                       .setCancelable(false).create();

               dlg.show();

               Thread newThread = new Thread(new Runnable() {
                   @Override
                   public void run() {

                       SystemSettingsManager systemSettingsManager = SystemSettingsManager.createInstance(AlfahresMain.this);

                       FilesDBManager dbManager = systemSettingsManager.getSyncFilesManager()
                               .getFilesDBManager();

                       boolean bresult = dbManager.updateAllFilesFor(
                               String.valueOf(systemSettingsManager.getAccount().getUserName()),
                               result.getContents()
                       );

                       if(bresult)
                       {


                           AlfahresMain.this.runOnUiThread(new Runnable() {
                               @Override
                               public void run() {

                                   dlg.dismiss();

                                   final AlertDialog cancelDlg = new AlertDialog.Builder(AlfahresMain.this)
                                           .setTitle("Done.").setMessage("Done Updating files.")
                                           .setCancelable(true)
                                           .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                               @Override
                                               public void onClick(DialogInterface dialogInterface, int i) {

                                                   dialogInterface.dismiss();

                                               }
                                           }).create();

                                   cancelDlg.show();

                               }
                           });
                       }else
                       {
                           AlfahresMain.this.runOnUiThread(new Runnable() {
                               @Override
                               public void run() {

                                   final AlertDialog cancelDlg = new AlertDialog.Builder(AlfahresMain.this)
                                           .setTitle("Error.").setMessage("There was a problem updating the files.")
                                           .setCancelable(true)
                                           .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                               @Override
                                               public void onClick(DialogInterface dialogInterface, int i) {

                                                   dialogInterface.dismiss();

                                               }
                                           }).create();

                                   cancelDlg.show();

                               }
                           });
                       }

                   }
               });

               newThread.start();



           }else if (currentFragment instanceof MainFilesScreenFragment)
           {
              try
              {
                  //now try to remove it and mark it as checked out
                  boolean bresult = SystemSettingsManager.createInstance(this)
                          .getSyncFilesManager().operateOnFile(result.getContents()
                                  ,FileModelStates.CHECKED_OUT.toString());
                  if(bresult)
                  {

                      Toast.makeText(this,String.format("Format:%s , BarCode : %s",result.getFormatName(),result.getContents())
                              ,Toast.LENGTH_LONG).show();
                  }else
                  {
                      Toast.makeText(this, String.format("No matched Files Found"), Toast.LENGTH_LONG).show();
                  }

              }catch (Exception s)
              {
                  Log.w("AlfahresMain",s.getMessage());
              }
           }else if(currentFragment instanceof ScanAndReceiveFragment)
           {
               final AlertDialog dialog = new AlertDialog.Builder(this)
                       .setTitle(R.string.main_files_alertDlg_Title)
                       .setMessage(R.string.main_loading_title)
                       .setCancelable(false).create();

               dialog.show();

               ScanAndReceiveTask scanTask = new ScanAndReceiveTask(this,result.getContents()
                       ,(ScanAndReceiveFragment)currentFragment);
               scanTask.setDialog(dialog);

               Thread scanThread = new Thread(scanTask);

               scanThread.start();

           }

        }
    }

    private void init()
    {
        final List<Fragment> fragList = ScreenRouter.getFragments(this);
        this.setFragmentList(fragList);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),fragList);
        mSectionsPagerAdapter.setParentActivity(this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                Fragment currentFragment = fragList.get(position);

                if(currentFragment instanceof MainFilesScreenFragment)
                {
                    String title = ((MainFilesScreenFragment)currentFragment).getTitle();
                    AlfahresMain.this.setTitle(title);

                    SystemSettingsManager.createInstance(AlfahresMain.this)
                            .getSyncFilesManager().getFilesListener().clear();

                    SystemSettingsManager.createInstance(AlfahresMain.this)
                            .getSyncFilesManager().getFilesListener().add((MainFilesScreenFragment)currentFragment);

                }else if (currentFragment instanceof LocalSyncFilesFragment)
                {
                    String title = ((LocalSyncFilesFragment)currentFragment).getTitle();
                    AlfahresMain.this.setTitle(title);
                    SystemSettingsManager.createInstance(AlfahresMain.this)
                            .getSyncFilesManager().getFilesListener().clear();

                    SystemSettingsManager.createInstance(AlfahresMain.this)
                            .getSyncFilesManager().getFilesListener().add((LocalSyncFilesFragment)currentFragment);

                }else if (currentFragment instanceof ScanAndReceiveFragment)
                {
                    String title= ((ScanAndReceiveFragment)currentFragment).getTitle();
                    AlfahresMain.this.setTitle(title);
                    SystemSettingsManager.createInstance(AlfahresMain.this)
                            .getSyncFilesManager().getFilesListener().clear();

                    SystemSettingsManager.createInstance(AlfahresMain.this)
                            .getSyncFilesManager().getFilesListener().add((ScanAndReceiveFragment)currentFragment);
                }else if (currentFragment instanceof CoordinatorCollectionFragment)
                {
                    String title = ((CoordinatorCollectionFragment)currentFragment).getTitle();
                    AlfahresMain.this.setTitle(title);
                    SystemSettingsManager.createInstance(AlfahresMain.this)
                            .getSyncFilesManager().getFilesListener().clear();


                    SystemSettingsManager.createInstance(AlfahresMain.this)
                            .getSyncFilesManager().getFilesListener().add((CoordinatorCollectionFragment)currentFragment);

                }

                //notify all of them about any changes
                ViewPagerSlave slave = (ViewPagerSlave)currentFragment;

                if(slave != null)
                    slave.update();


                invalidateOptionsMenu();


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

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
        ScreenRouter.getPersonalizedMenu(this, getMenuInflater(), menu);
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
        }else if (id == R.id.markFiles)
        {
            final AlertDialog waitDialog = new AlertDialog.Builder(this)
                    .setTitle("Marking Files...")
                    .setMessage("Please Wait...")
                    .setCancelable(false).create();

            waitDialog.show();
            MarkingTask currentMarkingTask = new MarkingTask(this,EmployeeUtils.RECEIVE_FILES);
            currentMarkingTask.setDialog(waitDialog);

            Thread markingThread = new Thread(currentMarkingTask);
            markingThread.start();



        } else if (id == R.id.btn_logout_main)
        {

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
        }else if (id == R.id.sync_btn)
        {
            final AlertDialog syncingDialog = new AlertDialog.Builder(this)
                    .setTitle("Syncing Files...")
                    .setMessage("Please Wait...")
                    .setCancelable(false)
                    .create();

            syncingDialog.show();
            //that means the user is picking synchornization manually
            Thread manualSync = new Thread(new Runnable() {
                @Override
                public void run() {

                    ManualSyncTask syncTask = new ManualSyncTask(AlfahresMain.this);

                    //now do the syncing process
                    syncTask.run();

                    AlfahresMain.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            //afterwards, check to see if the current fragment is localSync
                            int currentFragmentIndex = AlfahresMain.this.mViewPager.getCurrentItem();

                            Fragment currentFragment = AlfahresMain.this.mSectionsPagerAdapter.getItem(currentFragmentIndex);

                            if(currentFragment instanceof LocalSyncFilesFragment)
                            {
                                LocalSyncFilesFragment fragment = new LocalSyncFilesFragment();
                                fragment.notifyChange();
                                syncingDialog.dismiss();
                            }

                        }
                    });


                }
            });

            manualSync.start();




        }

        return super.onOptionsItemSelected(item);
    }

    public List<Fragment> getFragmentList() {
        return fragmentList;
    }

    public void setFragmentList(List<Fragment> fragmentList) {
        this.fragmentList = fragmentList;
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
