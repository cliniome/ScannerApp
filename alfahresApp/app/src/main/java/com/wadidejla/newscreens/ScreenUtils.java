package com.wadidejla.newscreens;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.wadidejla.newscreens.utils.TabDetails;
import com.wadidejla.screens.CoordinatorCollectionFragment;
import com.wadidejla.screens.LocalSyncFilesFragment;
import com.wadidejla.screens.MainFilesScreenFragment;
import com.wadidejla.screens.ScanAndReceiveFragment;
import com.wadidejla.settings.SystemSettingsManager;

import java.util.ArrayList;
import java.util.List;

import wadidejla.com.alfahresapp.R;

/**
 * Created by snouto on 08/06/15.
 */
public class ScreenUtils {

    private static final String KEEPER_ROLE="KEEPER";
    private static final String RECEPTIONIST_ROLE="RECEPTIONIST";
    private static final String COORDINATOR_ROLE="COORDINATOR";
    private static final String ADMIN_ROLE="ADMIN";



    public static List<TabDetails> getTabsFor(Context con)
    {
        try
        {
            List<TabDetails> availableTabs = new ArrayList<TabDetails>();

            SystemSettingsManager settingsManager = SystemSettingsManager.createInstance(con);

            if(settingsManager.getAccount() != null)
            {
                if(settingsManager.getAccount().getRole().equals(KEEPER_ROLE))
                {
                    //that is the keeper role
                    TabDetails newRequests = new TabDetails(con.getResources()
                            .getString(R.string.ScreenUtils_NewRequests),R.drawable.newrequests);

                    TabDetails ongoingFiles = new TabDetails(con.getResources().getString(R.string.ScreenUtils_Ongoing_Files),
                            R.drawable.ongoing);
                    TabDetails receiveFiles = new TabDetails(con.getResources().getString(R.string.ScreenUtils_Receive_Files),
                            R.drawable.receive);
                    availableTabs.add(newRequests);
                    availableTabs.add(ongoingFiles);
                    availableTabs.add(receiveFiles);



                }else if(settingsManager.getAccount().getRole().equals(COORDINATOR_ROLE))
                {
                    //that is the coordinator role
                    TabDetails ongoingFiles = new TabDetails(con.getResources().getString(R.string.ScreenUtils_Ongoing_Files),
                            R.drawable.ongoing);

                    availableTabs.add(ongoingFiles);

                    TabDetails receiveFiles = new TabDetails(con.getResources().getString(R.string.ScreenUtils_Receive_Files),
                            R.drawable.receive);

                    availableTabs.add(receiveFiles);

                    TabDetails distributeFiles = new TabDetails(con.getResources().getString(R.string.ScreenUtils_Distribute_Files),
                            R.drawable.distribute);
                    availableTabs.add(distributeFiles);

                    TabDetails collectFiles = new TabDetails(con.getResources().getString(R.string.ScreenUtils_Collect_Files),
                            R.drawable.collects);

                    availableTabs.add(collectFiles);


                }else if(settingsManager.getAccount().getRole().equals(RECEPTIONIST_ROLE))
                {
                    //that is the receptionist role
                    TabDetails ongoingFiles = new TabDetails(con.getResources().getString(R.string.ScreenUtils_Ongoing_Files),
                            R.drawable.ongoing);

                    TabDetails receiveFiles = new TabDetails(con.getResources().getString(R.string.ScreenUtils_Receive_Files),
                            R.drawable.receive);

                    availableTabs.add(ongoingFiles);
                    availableTabs.add(receiveFiles);
                }

            }

            return availableTabs;

        }catch (Exception s)
        {
            s.printStackTrace();
            return new ArrayList<TabDetails>();
        }
    }

    public static List<Fragment> getFragments(Context con)
    {
        SystemSettingsManager settingsManager = SystemSettingsManager.createInstance(con);

        List<Fragment> fragments = new ArrayList<Fragment>();


        if(settingsManager.getAccount() != null)
        {
            if(settingsManager.getAccount().getRole().equals(KEEPER_ROLE))
            {
                //that is the keeper
                fragments.add(new NewRequestsFragment());
                fragments.add(new NewOutgoingFilesFragment());
                fragments.add(new NewReceiveFilesFragment());

            }else if(settingsManager.getAccount().getRole().equals(COORDINATOR_ROLE))
            {
                //that is the receptionist
                fragments.add(new CoordinatorCollectionFragment());
                fragments.add(new NewOutgoingFilesFragment());
                fragments.add(new NewReceiveFilesFragment());

            }else
            {
                fragments.add(new NewOutgoingFilesFragment());
                fragments.add(new NewReceiveFilesFragment());
            }

        }

        return fragments;

    }

}
