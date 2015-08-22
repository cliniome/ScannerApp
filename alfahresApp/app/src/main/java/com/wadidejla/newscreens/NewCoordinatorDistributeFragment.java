package com.wadidejla.newscreens;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.PopupMenu;

import com.degla.restful.models.FileModelStates;
import com.degla.restful.models.RestfulFile;
import com.wadidejla.newscreens.adapters.NewDistributeExpandableAdapter;
import com.wadidejla.newscreens.utils.BarcodeUtils;
import com.wadidejla.newscreens.utils.DBStorageUtils;
import com.wadidejla.newscreens.utils.NetworkUtils;
import com.wadidejla.newscreens.utils.NewViewUtils;
import com.wadidejla.settings.SystemSettingsManager;
import com.wadidejla.tasks.CollectingTask;
import com.wadidejla.utils.EmployeeUtils;
import com.wadidejla.utils.SoundUtils;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import wadidejla.com.alfahresapp.R;

/**
 * Created by snouto on 09/06/15.
 */
public class NewCoordinatorDistributeFragment extends Fragment implements IFragment , IAdapterListener
{

    private ExpandableListView expandableListView;
    private NewDistributeExpandableAdapter adapter;
    private FragmentListener listener;

    private int totalFiles = 0;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.coordinator_main_list_view,container,false);

        this.initView(rootView);

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        if(this.adapter != null)
        {
            this.adapter.notifyDataSetChanged();

        }
    }

    private void initView(View rootView) {

        try
        {
            this.expandableListView = (ExpandableListView)rootView.findViewById(R.id.coordinator_list_view);
            this.adapter = new NewDistributeExpandableAdapter(getActivity());
            this.expandableListView.setAdapter(this.adapter);
            this.adapter.setFragment(this);
            this.adapter.setListView(this.expandableListView);

            //Bind the actions in here

            //Bind Do Actions Menu Button

            //Do action
            final Button DoActionsBtn = (Button)rootView.findViewById(R.id.new_receive_actions_btn);

            DoActionsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    PopupMenu menu = new PopupMenu(getActivity(),view);
                    menu.inflate(R.menu.new_distribute_pop_menu);

                    SystemSettingsManager settingsManager = SystemSettingsManager.createInstance(getActivity());

                    if(settingsManager.getAccount().isKeeper() ||
                            settingsManager.getAccount().isReceptionist())
                    {
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
                                                            getFilesReadyForCollection();

                                                    if (availableFiles != null) {
                                                        for (RestfulFile file : availableFiles) {
                                                            storageUtils.operateOnFile(file, FileModelStates.MISSING.toString(),
                                                                    RestfulFile.READY_FILE);
                                                        }

                                                        //Empty all files
                                                        storageUtils.getReceivedFiles().clear();

                                                        //now update
                                                        SoundUtils.playSound(getActivity());
                                                        NewCoordinatorDistributeFragment.this.refresh();
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

                                case R.id.pop_mark_all_distribute: {

                                    AlertDialog dialog = NewViewUtils.getChoiceDialog(getActivity(),
                                            "Mark all Files as Distributed",
                                            "Are you sure to mark all files as Distributed?",
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    //Mark them

                                                    DBStorageUtils storageUtils = new DBStorageUtils(getActivity());

                                                    List<RestfulFile> availableFiles = storageUtils.getFilesReadyForDistribution();

                                                    if (availableFiles != null) {
                                                        for (RestfulFile file : availableFiles) {
                                                            storageUtils.operateOnFile(file, FileModelStates.DISTRIBUTED.toString(),
                                                                    RestfulFile.READY_FILE);
                                                        }

                                                        //Empty all files
                                                        storageUtils.getReceivedFiles().clear();

                                                        //now update
                                                        SoundUtils.playSound(getActivity());
                                                        NewCoordinatorDistributeFragment.this.refresh();
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

            //Do the refresh
            this.refresh();


        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }

    @Override
    public synchronized String getTitle() {

        String title = "Distribute Files";

        if(this.adapter != null)
        {
            title = String.format("%s(%s)",title,this.getTotalFiles());
        }

        return title;
    }

    @Override
    public void chainUpdate() {

        NetworkUtils.ScheduleSynchronization(getActivity(),this);
        this.refresh();
    }

    @Override
    public void refresh() {

        if(this.adapter != null)
        {

            this.adapter.loadData();
            this.adapter.doRefresh();
        }

        /*NetworkUtils.ScheduleSynchronization(getActivity());*/


    }

    @Override
    public void handleScanResults(String barcode) {

        try {

            if(barcode != null && !barcode.isEmpty())
            {
                if(this.adapter != null)
                {
                    NewDistributeExpandableAdapter adapter = (NewDistributeExpandableAdapter)this.adapter;

                    if(adapter != null)
                    {
                        DBStorageUtils storageUtils = new DBStorageUtils(getActivity());

                        Map<String,List<RestfulFile>> categorizedData = adapter.getCategorizedData();

                        RestfulFile foundFile = storageUtils.getDistributableFile(barcode);

                        /*Collection<List<RestfulFile>> collectedFiles = categorizedData.values();

                        if(collectedFiles != null && !collectedFiles.isEmpty())
                        {
                            Iterator<List<RestfulFile>> iterator = collectedFiles.iterator();

                            while(iterator.hasNext())
                            {
                                List<RestfulFile> oneBatchFiles = iterator.next();

                                if(oneBatchFiles != null && !oneBatchFiles.isEmpty())
                                {
                                    for(RestfulFile file : oneBatchFiles)
                                    {
                                        if(file.getFileNumber().equals(barcode))
                                        {
                                            foundFile = file;
                                            break;
                                        }
                                    }
                                }
                            }
                        }*/


                        //check if the found file is not null
                        if(foundFile != null)
                        {
                            BarcodeUtils barcodeUtils = new BarcodeUtils(barcode);
                            //mark it as Distributed
                            foundFile.setState(FileModelStates.DISTRIBUTED.toString());
                            foundFile.setEmp(storageUtils.getSettingsManager().getAccount());
                            foundFile.setReadyFile(RestfulFile.READY_FILE);
                            //now save the current file
                            storageUtils.getSettingsManager().getFilesManager().getFilesDBManager()
                                    .insertFile(foundFile);

                            //Then play the sound
                            SoundUtils.playSound(getActivity());

                            //Update the screen
                            this.chainUpdate();

                        }


                    }


                }
            }

        }catch (Exception s)
        {
            Log.e("error",s.getMessage());
        }

    }

    @Override
    public void setFragmentListener(FragmentListener listener) {

        this.listener = listener;

    }


    @Override
    public synchronized void doUpdateFragment() {


        if(this.listener != null)
        {
            this.listener.invalidate();

            if(this.expandableListView != null)
            {
                this.expandableListView.expandGroup(0);
            }

            ((Activity)this.listener).setTitle(this.getTitle());
        }

    }

    public int getTotalFiles() {
        return totalFiles;
    }

    public void setTotalFiles(int totalFiles) {
        this.totalFiles = totalFiles;
    }
}
