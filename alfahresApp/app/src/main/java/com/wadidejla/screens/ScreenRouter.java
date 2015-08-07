package com.wadidejla.screens;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;

import com.degla.restful.models.FileModelStates;
import com.degla.restful.models.RestfulFile;
import com.wadidejla.listeners.KeeperOnClickListener;
import com.wadidejla.newscreens.CheckOutFileFragment;
import com.wadidejla.settings.SystemSettingsManager;
import com.wadidejla.utils.ActionItem;
import com.wadidejla.utils.Actionable;
import com.wadidejla.utils.AlFahresFilesManager;
import com.wadidejla.utils.FilesManager;
import com.wadidejla.utils.SoundUtils;
import com.wadidejla.utils.ViewUtils;

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
                fragments.add(new CheckOutFileFragment());

            }else if(settingsManager.getAccount().getRole().equals(COORDINATOR_ROLE))
            {
                //that is the receptionist
                fragments.add(new CoordinatorCollectionFragment());
                fragments.add(new LocalSyncFilesFragment());
                fragments.add(new ScanAndReceiveFragment());

            }else
            {
                fragments.add(new LocalSyncFilesFragment());
                fragments.add(new ScanAndReceiveFragment());
            }

        }
        return fragments;

    }

    public static void getPersonalizedMenu(Context con,MenuInflater inflater,Menu menu)
    {
        SystemSettingsManager settingsManager = SystemSettingsManager.createInstance(con);
        if(settingsManager.getAccount() != null)
        {
            inflater.inflate(R.menu.keeper_menu,menu);

        }

    }

    public static KeeperOnClickListener<BaseExpandableListAdapter> getExpandableListener(final Context conn)
    {
        KeeperOnClickListener<BaseExpandableListAdapter> listener = new KeeperOnClickListener<BaseExpandableListAdapter>(conn);
        listener.getActionItems().add(new ActionItem<BaseExpandableListAdapter>("Mark file as Missing", new Actionable<BaseExpandableListAdapter>() {
            @Override
            public void doAction(Object onItem,BaseExpandableListAdapter adapter) {

                //Access the adapter
                CoordinatorExpandableAdapter expandableAdapter = (CoordinatorExpandableAdapter)adapter;
                SystemSettingsManager settingsManager = SystemSettingsManager.createInstance(conn);
                FilesManager filesManager = settingsManager.getSyncFilesManager();

                if(expandableAdapter != null)
                {
                    RestfulFile file = (RestfulFile)onItem;
                    file.setEmp(settingsManager.getAccount());
                    file.setState(FileModelStates.MISSING.toString());
                    file.setReadyFile(RestfulFile.READY_FILE);
                    filesManager.getFilesDBManager().insertFile(file);
                    expandableAdapter.removeFile(file);

                }
            }
        }));

        listener.getActionItems().add(new ActionItem("Show File Details", new Actionable<BaseExpandableListAdapter>() {
            @Override
            public void doAction(Object OnItem, BaseExpandableListAdapter adapter) {

                RestfulFile file = (RestfulFile)OnItem;

                final AlertDialog detailsDialog = new AlertDialog.Builder(conn)
                        .setCustomTitle(ViewUtils.getDetailsTitleViewFor(file,conn))
                        .setView(ViewUtils.getDetailsViewFor(file, conn))
                        .setPositiveButton("Ok.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create();


                detailsDialog.show();

            }
        }));


        return listener;
    }


    public static GenericFilesAdapter getGenericKeeperArrayAdapter(final Context conn,List<RestfulFile> files)
    {
        KeeperOnClickListener<ArrayAdapter> listener = new KeeperOnClickListener<ArrayAdapter>(conn);
        listener.getActionItems().add(new ActionItem<ArrayAdapter>("Mark file as Missing", new Actionable<ArrayAdapter>() {
            @Override
            public void doAction(Object onItem,ArrayAdapter adapter) {

                SystemSettingsManager settingsManager = SystemSettingsManager.createInstance(conn);

                AlFahresFilesManager filesManager = (AlFahresFilesManager) settingsManager.getReceivedSyncFilesManager();

                filesManager.setFiles(settingsManager.getReceivedFiles());

                RestfulFile file = (RestfulFile)onItem;

                if(file == null) return;

                file.setState(FileModelStates.MISSING.toString());

                filesManager.operateOnFile(file, settingsManager.getAccount());

                adapter.notifyDataSetChanged();

                SoundUtils.playSound(conn);
            }
        }));

        listener.getActionItems().add(new ActionItem("Show File Details", new Actionable<ArrayAdapter>() {
            @Override
            public void doAction(Object OnItem, ArrayAdapter adapter) {

                RestfulFile file = (RestfulFile)OnItem;

                final AlertDialog detailsDialog = new AlertDialog.Builder(conn)
                        .setCustomTitle(ViewUtils.getDetailsTitleViewFor(file,conn))
                        .setView(ViewUtils.getDetailsViewFor(file, conn))
                        .setPositiveButton("Ok.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create();


                detailsDialog.show();

            }
        }));


        GenericFilesAdapter adapter = new GenericFilesAdapter(conn,R.layout.single_file_view,files);

        adapter.setListener(listener);


        return adapter;

    }
}
