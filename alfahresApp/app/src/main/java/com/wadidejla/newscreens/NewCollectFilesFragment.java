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
import com.wadidejla.newscreens.adapters.NewCoordinatorExpandableAdapter;
import com.wadidejla.newscreens.utils.BarcodeUtils;
import com.wadidejla.newscreens.utils.DBStorageUtils;
import com.wadidejla.newscreens.utils.ExpandableListViewUtils;
import com.wadidejla.newscreens.utils.NetworkUtils;
import com.wadidejla.newscreens.utils.NewViewUtils;
import com.wadidejla.newscreens.utils.ScannerUtils;
import com.wadidejla.settings.SystemSettingsManager;
import com.wadidejla.tasks.CollectingTask;
import com.wadidejla.tasks.MarkingTask;
import com.wadidejla.utils.EmployeeUtils;
import com.wadidejla.utils.SoundUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import wadidejla.com.alfahresapp.R;

import static com.wadidejla.newscreens.utils.ScannerUtils.SCANNER_TYPE_CAMERA;

/**
 * Created by snouto on 09/06/15.
 */
public class NewCollectFilesFragment extends Fragment implements IFragment , IAdapterListener
{

    private ExpandableListView expandableListView;
    private FragmentListener listener;
    private NewCoordinatorExpandableAdapter adapter;

    private int totalFiles = 0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.latest_collect_screen,container,false);

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
            this.adapter = new NewCoordinatorExpandableAdapter(getActivity());
            this.adapter.setFragment(this);
            this.expandableListView.setAdapter(this.adapter);
            this.adapter.setListView(this.expandableListView);
            //Bind the actions in here



           /* //Bind the scan Button
            final Button scanButton = (Button)rootView.findViewById(R.id.new_files_layout_scan_btn);
            scanButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Scan for temporary container
                    ScannerUtils.ScanBarcode(getActivity(), SCANNER_TYPE_CAMERA,
                            NewCollectFilesFragment.this,false,null);
                }
            });
*/
            //Bind Do Actions Menu Button

            //Do action
            final Button DoActionsBtn = (Button)rootView.findViewById(R.id.new_receive_actions_btn);

            DoActionsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    PopupMenu menu = new PopupMenu(getActivity(),view);
                    menu.inflate(R.menu.new_coordinator_pop_menu);

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
                                                        NewCollectFilesFragment.this.chainUpdate();
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
            this.chainUpdate();


        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }


    @Override
    public synchronized String getTitle() {

        String title = "Collect Files";
        title = String.format("%s(%s)",title,this.getTotalFiles());

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


       /* if(this.expandableListView != null)
        {
            this.expandableListView.smoothScrollToPosition(0);
        }*/



    }

    @Override
    public void handleScanResults(String barcode) {

        try {

            if(barcode != null && !barcode.isEmpty())
            {
                BarcodeUtils barcodeUtils = new BarcodeUtils(barcode);
                //mark it as collected
                final  DBStorageUtils storageUtils = new DBStorageUtils(getActivity());

                         if(barcodeUtils.isMedicalFile())
                            {
                                NewCoordinatorExpandableAdapter adapter = (NewCoordinatorExpandableAdapter)this.adapter;

                                if(adapter != null)
                                {


                                    Map<String,List<RestfulFile>> categorizedData = adapter.getCategorizedData();

                                    //RestfulFile foundFile = storageUtils.getCollectableFile(barcode);
                                    RestfulFile foundFile = null;

                                    if(foundFile != null)
                                    {

                                        //otherwise , toggle the selection of that file

                                        if(foundFile.getSelected() > 0 && foundFile.isMultipleClinics())
                                        {

                                            SoundUtils.PlayError(getActivity());
                                            SoundUtils.vibrateDevice(getActivity());

                                            final RestfulFile tempFinal = foundFile;
                                           AlertDialog dialog = NewViewUtils.getChoiceDialog(getActivity(), "Warning", String.format("File : %s has another appointment , would you like " +
                                                   "to transfer Now ?", foundFile.getFileNumber()), new Runnable() {
                                               @Override
                                               public void run() {

                                                   //Mark that file immediately as sent out
                                                   storageUtils.operateOnFile(tempFinal, FileModelStates.COORDINATOR_OUT.toString(), RestfulFile.READY_FILE);


                                                   NewCollectFilesFragment.this.chainUpdate();


                                               }
                                           }, new Runnable() {
                                               @Override
                                               public void run() {

                                               }
                                           });

                                            dialog.show();




                                            return;
                                        }

                                        foundFile.toggleSelection();

                                        //No need for the below commented Method Call
                                        //this.getFilesReadyToBeCollected(foundFile);
                                        storageUtils.operateOnFile(foundFile, foundFile.getState(), RestfulFile.NOT_READY_FILE);



                                        //Play the sound
                                        SoundUtils.playSound(getActivity());


                                    }

                            }


                        }else if (barcodeUtils.isTrolley())
                         {

                             //Get all selected collectable files
                             boolean transferExists = false;

                             List<RestfulFile> selectableCollectableFiles = storageUtils.getFilesReadyForCollection(true);

                             if(selectableCollectableFiles != null && selectableCollectableFiles.size() >0)
                             {
                                 for(final RestfulFile currentFile :selectableCollectableFiles)
                                 {
                                     if(currentFile.isMultipleClinics())
                                     {
                                         transferExists = true;
                                         continue;
                                     }

                                     currentFile.setTemporaryCabinetId(barcode);
                                     currentFile.setSelected(0);
                                     storageUtils.operateOnFile(currentFile,FileModelStates.COORDINATOR_OUT.toString(),RestfulFile.READY_FILE);

                                 }


                                 if(transferExists)
                                 {

                                     AlertDialog dialog = NewViewUtils.getAlertDialog(getActivity(),"Warning","There are some file(s) has/have other appointments." +
                                     " , Please take actions separately on these files .");
                                     dialog.show();
                                 }

                                 //update the view
                                 this.chainUpdate();

                             }

                         }






            }

        }catch (Exception s)
        {

            Log.e("error",s.getMessage());
        }

        finally {
            //Then play the sound
            SoundUtils.playSound(getActivity());

          if(this.adapter != null)
          {
              this.adapter.doRefresh();
          }
        }

    }

    @Override
    public void setFragmentListener(FragmentListener listener) {


        this.listener = listener;

    }

    private void getFilesReadyToBeCollected(RestfulFile foundFile) {

        try
        {
            DBStorageUtils storageUtils = new DBStorageUtils(getActivity());

            List<RestfulFile> foundFiles = storageUtils.getFilesReadyForCollection();

            if(foundFiles != null && foundFiles.size() > 0)
            {
                for(RestfulFile current : foundFiles)
                {
                    if(current.getFileNumber().equals(foundFile.getFileNumber()))
                        continue;
                    else
                    {
                        if(current.getSelected() > 1)
                        {
                            current.setSelected(0);
                            storageUtils.operateOnFile(current,current.getState(),current.getReadyFile());
                        }

                        //then update it

                    }
                }
            }

        }catch (Exception s)
        {
            Log.e("Error",s.getMessage());
        }
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
