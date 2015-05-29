package com.wadidejla.screens;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.degla.restful.models.FileModelStates;
import com.degla.restful.models.RestfulFile;
import com.degla.restful.models.SyncBatch;
import com.wadidejla.listeners.KeeperOnClickListener;
import com.wadidejla.settings.SystemSettingsManager;
import com.wadidejla.utils.ActionItem;
import com.wadidejla.utils.Actionable;
import com.wadidejla.utils.AlFahresFilesManager;
import com.wadidejla.utils.FilesManager;

import java.util.ArrayList;
import java.util.List;

import wadidejla.com.alfahresapp.R;

/**
 * Created by snouto on 23/05/15.
 */
public class ScreenRouter {

    //RECEPTIONIST, KEEPER, COORDINATOR,ADMIN;

    private static final String KEEPER_ROLE="KEEPER";
    private static final String RECEPTIONIST_ROLE="RECEPTIONIST";
    private static final String COORDINATOR_ROLE="COORDINATOR";
    private static final String ADMIN_ROLE="ADMIN";

    public static List<Fragment> getFragments(Context con)
    {
        SystemSettingsManager settingsManager = SystemSettingsManager.createInstance(con);

        List<Fragment> fragments = new ArrayList<Fragment>();


        if(settingsManager.getAccount() != null)
        {
            if(settingsManager.getAccount().getRole().equals(KEEPER_ROLE))
            {
                //that is the keeper
                fragments.add(new MainFilesScreenFragment());
                fragments.add(new LocalSyncFilesFragment());
                fragments.add(new ScanAndReceiveFragment());

            }else if (settingsManager.getAccount().getRole().equals(RECEPTIONIST_ROLE))
            {
                //that is the receptionist
            }
            else if (settingsManager.getAccount().getRole().equals(COORDINATOR_ROLE))
            {
                //that is the coordinator
            }else if (settingsManager.getAccount().getRole().equals(ADMIN_ROLE))
            {
                //that is the admin role
            }
        }
        return fragments;

    }

    public static void getPersonalizedMenu(Context con,MenuInflater inflater,Menu menu)
    {
        SystemSettingsManager settingsManager = SystemSettingsManager.createInstance(con);
        if(settingsManager.getAccount() != null)
        {
            if(settingsManager.getAccount().getRole().equals(KEEPER_ROLE))
            {
                //that is the Keeper
                inflater.inflate(R.menu.keeper_menu, menu);

            }else if (settingsManager.getAccount().getRole().equals(RECEPTIONIST_ROLE))
            {
                //that is the receptionist
            }
            else if (settingsManager.getAccount().getRole().equals(COORDINATOR_ROLE))
            {
                //that is the coordinator
            }else if (settingsManager.getAccount().getRole().equals(ADMIN_ROLE))
            {
                //that is the admin role
            }
        }

    }


    public static GenericFilesAdapter getGenericKeeperArrayAdapter(final Context conn,List<RestfulFile> files)
    {
        KeeperOnClickListener listener = new KeeperOnClickListener(conn);
        listener.getActionItems().add(new ActionItem("Mark file as Missing", new Actionable() {
            @Override
            public void doAction(Object onItem,ArrayAdapter adapter) {

                SystemSettingsManager settingsManager = SystemSettingsManager.createInstance(conn);

                AlFahresFilesManager filesManager = (AlFahresFilesManager) settingsManager.getReceivedSyncFilesManager();



                filesManager.setFiles(settingsManager.getReceivedFiles());

                RestfulFile file = (RestfulFile)onItem;

                if(file == null) return;

                file.setState(FileModelStates.MISSING.toString());

                filesManager.operateOnFile(file);

                adapter.notifyDataSetChanged();
            }
        }));


        GenericFilesAdapter adapter = new GenericFilesAdapter(conn,R.layout.single_file_view,files);

        adapter.setListener(listener);


        return adapter;

    }
}
