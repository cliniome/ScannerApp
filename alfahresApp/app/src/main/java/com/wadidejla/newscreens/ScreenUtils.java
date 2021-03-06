package com.wadidejla.newscreens;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.wadidejla.newscreens.utils.TabDetails;
import com.wadidejla.newscreens.utils.TabTypes;
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
                            .getString(R.string.ScreenUtils_NewRequests),R.drawable.newrequests,
                            TabTypes.NEW_REQUESTS);

                    TabDetails ongoingFiles = new TabDetails(con.getResources().getString(R.string.ScreenUtils_Ongoing_Files),
                            R.drawable.ongoing,TabTypes.OUTGOING);
                    TabDetails receiveFiles = new TabDetails(con.getResources()
                            .getString(R.string.KEEPER_ARCHIVE_TITLE),
                            R.drawable.checkin,TabTypes.CHECKIN);

                    TabDetails sortingShippingFiles = new TabDetails(con.getResources()
                            .getString(R.string.SHIPPING_SCREEN_TITLE),
                            R.drawable.shipping,TabTypes.SORTING);

                    TabDetails checkFileStatus = new TabDetails(con.getResources().getString(R.string.CHECK_FILE_STATUS_TITLE),
                            R.drawable.eye,TabTypes.CHECKSTATUS);

                    availableTabs.add(newRequests);
                    availableTabs.add(ongoingFiles);
                    availableTabs.add(receiveFiles);
                    availableTabs.add(sortingShippingFiles);
                    availableTabs.add(checkFileStatus);



                }else if(settingsManager.getAccount().getRole().equals(COORDINATOR_ROLE))
                {
                    //that is the coordinator role
                    TabDetails ongoingFiles = new TabDetails(con.getResources().getString(R.string.ScreenUtils_Ongoing_Files),
                            R.drawable.ongoing,TabTypes.OUTGOING);

                    availableTabs.add(ongoingFiles);

                    TabDetails receiveFiles = new TabDetails(con.getResources().getString(R.string.ScreenUtils_Receive_Files),
                            R.drawable.receive,TabTypes.RECEIVED);

                    availableTabs.add(receiveFiles);

                    TabDetails distributeFiles = new TabDetails(con.getResources().getString(R.string.ScreenUtils_Distribute_Files),
                            R.drawable.delivery,TabTypes.DISTRIBUTE);
                    availableTabs.add(distributeFiles);

                    TabDetails collectFiles = new TabDetails(con.getResources().getString(R.string.ScreenUtils_Collect_Files),
                            R.drawable.collects,TabTypes.COLLECT);

                    availableTabs.add(collectFiles);

                }else if(settingsManager.getAccount().getRole().equals(RECEPTIONIST_ROLE))
                {
                    //that is the receptionist role
                    TabDetails ongoingFiles = new TabDetails(con.getResources().getString(R.string.ScreenUtils_Ongoing_Files),
                            R.drawable.ongoing,TabTypes.OUTGOING);

                    TabDetails receiveFiles = new TabDetails(con.getResources()
                            .getString(R.string.ScreenUtils_Receive_Files),
                            R.drawable.receive,TabTypes.RECEIVED);

                    TabDetails checkFileStatus = new TabDetails(con.getResources().getString(R.string.CHECK_FILE_STATUS_TITLE),
                            R.drawable.eye,TabTypes.CHECKSTATUS);

                    availableTabs.add(ongoingFiles);
                    availableTabs.add(receiveFiles);
                    availableTabs.add(checkFileStatus);
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
                fragments.add(new NewArchiveFilesFragment());
                fragments.add(new SortingShippingScreen());
                fragments.add(new CheckFileStatusFragment());

            }else if(settingsManager.getAccount().getRole().equals(COORDINATOR_ROLE))
            {
                //that is the receptionist
                fragments.add(new NewOutgoingFilesFragment());
                fragments.add(new NewReceiveFilesFragment());
                fragments.add(new NewDistributeFilesFragment());
                fragments.add(new NewCollectFilesFragment());


            }else // that means he is a receptionist
            {
                fragments.add(new NewOutgoingFilesFragment());
                fragments.add(new NewReceptionistReceiveFragment());
                fragments.add(new CheckFileStatusFragment());
            }

        }

        return fragments;

    }

}
