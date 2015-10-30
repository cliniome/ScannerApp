package com.wadidejla.newscreens;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.degla.restful.models.FileModelStates;
import com.degla.restful.models.RestfulFile;
import com.degla.restful.models.SyncBatch;
import com.degla.restful.models.http.HttpResponse;
import com.wadidejla.network.AlfahresConnection;
import com.wadidejla.newscreens.adapters.LatestCollectScreenAdapter;
import com.wadidejla.newscreens.utils.BarcodeUtils;
import com.wadidejla.newscreens.utils.ConnectivityUtils;
import com.wadidejla.newscreens.utils.DBStorageUtils;
import com.wadidejla.newscreens.utils.NetworkUtils;
import com.wadidejla.newscreens.utils.NewViewUtils;
import com.wadidejla.settings.SystemSettingsManager;
import com.wadidejla.utils.SoundUtils;

import java.util.Date;
import java.util.List;

import wadidejla.com.alfahresapp.R;

/**
 * Created by snouto on 27/10/15.
 */
public class LatestCollectScreen extends Fragment implements IFragment , IAdapterListener {


    private FragmentListener listener;

    private ListView listview;

    private int totalFiles = 0;

    private LatestCollectScreenAdapter adapter;

    private boolean showMultipleAppointments;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.coordinator_new_collect_fragment,container,false);

        this.initView(rootView);

        return rootView;
    }

    private void initView(View rootView) {

        this.listview = (ListView)rootView.findViewById(R.id.coordinator_list_view);
        this.adapter = new LatestCollectScreenAdapter(getActivity(),R.layout.new_single_file_view);
        this.adapter.setShowMultipleAppointments(isShowMultipleAppointments());
        this.adapter.setFragment(this);
        this.listview.setAdapter(adapter);
        final Button DoActionsBtn = (Button)rootView.findViewById(R.id.new_receive_actions_btn);

        DoActionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu menu = new PopupMenu(getActivity(), view);
                menu.inflate(R.menu.new_coordinator_pop_menu);

                SystemSettingsManager settingsManager = SystemSettingsManager.createInstance(getActivity());

                if (settingsManager.getAccount().isKeeper() ||
                        settingsManager.getAccount().isReceptionist()) {
                    menu.getMenu().getItem(R.id.pop_mark_all_received).setVisible(false);
                }

                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        switch (menuItem.getItemId()) {

                            case R.id.pop_mark_all_missing:

                            {
                                //Do the missing actions on all the available Files

                                //Ask the user if he is going to mark all as missing
                                AlertDialog dialog = NewViewUtils.getChoiceDialog(getActivity(),
                                        "Mark all Files as Missing", "Are you sure to mark all files as missing?",
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                //Mark them

                                                DBStorageUtils storageUtils = new DBStorageUtils(getActivity());

                                                List<RestfulFile> availableFiles = storageUtils.
                                                        getCollectableFilesWithTransfer(isShowMultipleAppointments());

                                                if (availableFiles != null) {
                                                    for (RestfulFile file : availableFiles) {
                                                        storageUtils.operateOnFile(file, FileModelStates.MISSING.toString(),
                                                                RestfulFile.READY_FILE);
                                                    }

                                                    //Empty all files
                                                    storageUtils.getReceivedFiles().clear();
                                                    //now update
                                                    SoundUtils.playSound(getActivity());

                                                    //Notify the adapter
                                                    LatestCollectScreen.this.adapter.notifyDataSetChanged();
                                                }
                                            }
                                        }, new Runnable() {
                                            @Override
                                            public void run() {
                                                //do nothing in here
                                            }
                                        });

                                dialog.show();
                            }
                            break;


                        }

                        return true;
                    }
                });
                menu.show();

            }
        });



        //Load the data from the server
        this.loadData();
    }


    private void loadData(){


        try
        {

            final DBStorageUtils storageUtils = new DBStorageUtils(getActivity());

            List<RestfulFile> availableFiles = storageUtils.getCollectableFilesWithTransfer(isShowMultipleAppointments());

            if(ConnectivityUtils.isConnected(getActivity()))
            {
                Runnable networkThread = new Runnable() {
                    @Override
                    public void run() {

                        //force synchronization first in synchronous way
                        boolean success = false;

                        try
                        {
                            NetworkUtils.beginSynchronization(getActivity());
                            success = true;

                        }catch (Exception s)
                        {
                            Log.e("Error", s.getMessage());
                            success = false;
                        }

                        if(!success) return;


                        AlfahresConnection connection = storageUtils.getSettingsManager().getConnection();

                        HttpResponse response = null;

                        long latestTimeStamp = storageUtils.getRecentServerTimeStamp(FileModelStates.DISTRIBUTED);

                        response = connection.path(String.format("files/collectScreen?serverTimeStamp=%s"
                                ,latestTimeStamp))
                                .setMethodType(AlfahresConnection.GET_HTTP_METHOD)
                                .setAuthorization(storageUtils.getSettingsManager().getAccount().getUserName(),
                                        storageUtils.getSettingsManager().getAccount().getPassword())
                                .call(SyncBatch.class);

                        if(response != null && Integer.parseInt(response.getResponseCode())==
                                HttpResponse.OK_HTTP_CODE)
                        {
                            SyncBatch sync = (SyncBatch)response.getPayload();

                            List<RestfulFile> tempFiles = sync.getFiles();

                            if(tempFiles != null && tempFiles.size() > 0){

                                for(RestfulFile file : tempFiles)
                                {
                                    RestfulFile existingFile = storageUtils.getSettingsManager().getFilesManager()
                                            .getFilesDBManager().getFileByNumber(file.getFileNumber());

                                    if(existingFile != null)
                                        file.setSelected(existingFile.getSelected());

                                    storageUtils.operateOnFile(file,file.getState(),RestfulFile.NOT_READY_FILE);

                                }

                                //Get all files
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {


                                        boolean multipleApps = LatestCollectScreen.this.isShowMultipleAppointments();
                                        LatestCollectScreen.this.adapter.setAvailableFiles(storageUtils.getCollectableFilesWithTransfer(multipleApps));
                                        //now notify the adapter
                                        LatestCollectScreen.this.adapter.notifyDataSetChanged();

                                        //Vibrate
                                        SoundUtils.vibrateDevice(getActivity());
                                    }
                                });
                            }
                        }
                    }
                };
                //Start the network Thread
                Thread downloadingThread = new Thread(networkThread);
                downloadingThread.start();
                //get the already stored files
                this.adapter.setAvailableFiles(availableFiles);
                this.adapter.notifyDataSetChanged();
                SoundUtils.vibrateDevice(getActivity());
            }

        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(this.adapter != null)
        {
            this.adapter.notifyDataSetChanged();
        }
    }


    @Override
    public String getTitle() {
        String title = "Collect Files";

        if(isShowMultipleAppointments())
            title = "Collect Transfers";

        title = String.format("%s(%s)",title,this.getTotalFiles());

        return title;
    }

    @Override
    public void chainUpdate() {

    }

    @Override
    public void refresh() {

    }

    @Override
    public void handleScanResults(String barcode) {

        try {

            SystemSettingsManager settingsManager = SystemSettingsManager.createInstance(getActivity());

            BarcodeUtils utils = new BarcodeUtils(barcode);

            DBStorageUtils storageUtils = new DBStorageUtils(getActivity());

            if(utils.isMedicalFile())
            {
                //Select that file from the database
                RestfulFile foundFile = storageUtils.getCollectableFile(barcode,isShowMultipleAppointments());

                if(foundFile == null)
                {
                    SoundUtils.PlayError(getActivity());
                    SoundUtils.vibrateDevice(getActivity());
                }else
                {
                    foundFile.setEmp(settingsManager.getAccount());

                    foundFile.setDeviceOperationDate(new Date().getTime());

                    if(!isShowMultipleAppointments())
                    {
                        //That means this file is out patient , so toggle that file selection
                        foundFile.toggleSelection();
                        //play the sound
                        SoundUtils.playSound(getActivity());
                        SoundUtils.vibrateDevice(getActivity());
                        storageUtils.operateOnFile(foundFile,foundFile.getState(),RestfulFile.NOT_READY_FILE);
                        //now notify the adapter of any changes
                        this.adapter.notifyDataSetChanged();
                    }else
                    {
                        boolean operationResult = storageUtils.operateOnFile(foundFile,FileModelStates.COORDINATOR_OUT.toString(),RestfulFile.READY_FILE);

                        if(operationResult)
                        {
                            SoundUtils.playSound(getActivity());
                            SoundUtils.vibrateDevice(getActivity());
                        }
                    }
                }

            }else if (utils.isTrolley())
            {
                //that means we should collect all selectable files based on either they are outpatient or transfers
                List<RestfulFile> files = storageUtils.getSelectedCollectFiles(isShowMultipleAppointments());

                if(files == null ||files.size() <= 0) return;

                for(RestfulFile file :files)
                {
                    file.setEmp(settingsManager.getAccount());
                    file.setDeviceOperationDate(new Date().getTime());
                    if(!isShowMultipleAppointments()) // that means transfers view
                        file.setTemporaryCabinetId(barcode);


                    storageUtils.operateOnFile(file,FileModelStates.COORDINATOR_OUT.toString(),RestfulFile.READY_FILE);
                }
                //now notify the adapter
                this.adapter.notifyDataSetChanged();
                SoundUtils.vibrateDevice(getActivity());
                SoundUtils.playSound(getActivity());
            }

        }catch (Exception s)
        {
            Log.e("LatestCollectScreen",s.getMessage());
        }


        //Do Automatic Background Syncing process
        NetworkUtils.ScheduleSynchronization(getActivity());

    }

    @Override
    public void setFragmentListener(FragmentListener listener) {

        this.listener = listener;

    }

    @Override
    public void doUpdateFragment() {

        if(this.listener != null)
        {
            ((Activity)this.listener).setTitle(this.getTitle());
        }

    }

    public int getTotalFiles() {
        return totalFiles;
    }

    public void setTotalFiles(int totalFiles) {
        this.totalFiles = totalFiles;
    }

    public boolean isShowMultipleAppointments() {
        return showMultipleAppointments;
    }

    public void setShowMultipleAppointments(boolean showMultipleAppointments) {
        this.showMultipleAppointments = showMultipleAppointments;
    }
}
