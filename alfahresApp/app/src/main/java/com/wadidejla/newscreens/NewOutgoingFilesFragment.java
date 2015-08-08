package com.wadidejla.newscreens;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.wadidejla.newscreens.adapters.NewOutgoingFilesAdapter;
import com.wadidejla.newscreens.utils.BarcodeUtils;
import com.wadidejla.newscreens.utils.DBStorageUtils;
import com.wadidejla.newscreens.utils.NewViewUtils;
import com.wadidejla.newscreens.utils.ScannerUtils;
import com.wadidejla.tasks.ManualSyncTask;
import com.wadidejla.utils.SoundUtils;
import static com.wadidejla.newscreens.utils.ScannerUtils.*;
import java.util.List;

import wadidejla.com.alfahresapp.R;

/**
 * Created by snouto on 08/06/15.
 */
public class NewOutgoingFilesFragment extends Fragment implements IFragment , IAdapterListener {


    private ListView outgoingList;
    private NewOutgoingFilesAdapter adapter;

    private FragmentListener listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.new_outgoing_files_layout,container,false);

        this.initView(rootView);

        return rootView;

    }

    private void initView(View rootView) {

        try
        {
            this.outgoingList = (ListView)rootView.findViewById(R.id.mainFilesList);
            this.adapter = new NewOutgoingFilesAdapter(getActivity(),R.layout.new_single_file_view);
            this.adapter.setListener(this);
            this.adapter.getListener().doUpdateFragment();
            this.outgoingList.setAdapter(this.adapter);




            //Access the syncing process
            final Button syncButton = (Button)rootView.findViewById(R.id.new_outgoing_sync_btn);

            syncButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ProgressDialog dialog = new NewViewUtils().getWaitingDialog(getActivity());
                    dialog.show();

                    ManualSyncTask syncTask = new ManualSyncTask(getActivity());
                    syncTask.setProgressDialog(dialog);
                    syncTask.setCurrentFragment(NewOutgoingFilesFragment.this);

                    Thread syncingThread = new Thread(syncTask);
                    syncingThread.start();



                }
            });


            /*//Do action
            final Button DoActionsBtn = (Button)rootView.findViewById(R.id.new_outgoing_actions_btn);

            DoActionsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    PopupMenu menu = new PopupMenu(getActivity(),view);
                    menu.inflate(R.menu.outgoing_pop_menu);
                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {

                            switch (menuItem.getItemId()) {

                                case R.id.pop_mark_all_missing:

                                {
                                    //Do the missing actions on all the available Files
                                    //TODO : mark all current files as missing
                                    //Ask the user if he is going to mark all as missing
                                    AlertDialog dialog = NewViewUtils.getChoiceDialog(getActivity(),
                                            "Mark all Files as Missing", "Are you sure to mark all files as missing?",
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    //Mark them

                                                    DBStorageUtils storageUtils = new DBStorageUtils(getActivity());

                                                    List<RestfulFile> availableFiles = storageUtils.getAllReadyFilesForCurrentEmployee();

                                                    if(availableFiles != null)
                                                    {
                                                        for(RestfulFile file : availableFiles)
                                                        {
                                                            storageUtils.operateOnFile(file, FileModelStates.MISSING.toString(),
                                                                    RestfulFile.READY_FILE);
                                                        }

                                                        //now update
                                                        SoundUtils.playSound(getActivity());
                                                        NewOutgoingFilesFragment.this.refresh();
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
                                *//*case R.id.pop_add_all_container:
                                {
                                    //TODO : Scan for Temporary containers here

                                    //Get the trolley barcode
                                   ScannerUtils.ScanBarcode(getActivity(), SCANNER_TYPE_CAMERA
                                           , NewOutgoingFilesFragment.this,false,null);




                                }*//*
                                break;
                            }

                            return true;
                        }
                    });
                    menu.show();

                }
            });*/

        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }

    @Override
    public String getTitle() {

        String title= getResources().getString(R.string.ScreenUtils_Ongoing_Files);

        if(this.adapter != null)
        {
            title = String.format("%s(%s)",title,this.adapter.getCount());
        }

        return title;
    }

    @Override
    public void chainUpdate() {

        if(this.adapter != null)
        {
            this.adapter.notifyDataSetChanged();
        }

        if(this.listener != null)
        {
            ((Activity)listener).setTitle(this.getTitle());
        }


    }

    @Override
    public void refresh() {

        this.chainUpdate();

    }

    @Override
    public void handleScanResults(String trolleyBarcode) {


       /* BarcodeUtils barcodeUtils = new BarcodeUtils(trolleyBarcode);


        if(!barcodeUtils.isTrolley())
        {
            Toast.makeText(getActivity(),"Not a Trolley Barcode.",Toast.LENGTH_LONG)
                    .show();

            return;
        }

        ProgressDialog dialog = NewViewUtils.getWaitingDialog(getActivity());

        dialog.show();
        //update all available files , setting temporary cabinetID

        DBStorageUtils storageUtils = new DBStorageUtils(getActivity());

        List<RestfulFile> availableFiles = storageUtils.getAllReadyFilesForCurrentEmployee();

        if(availableFiles != null && availableFiles.size() > 0)
        {
            for(RestfulFile file : availableFiles)
            {
                file.setTemporaryCabinetId(trolleyBarcode);
                //update the current file
                storageUtils.insertOrUpdateFile(file);

            }
        }

        //Now refresh all the files
        NewOutgoingFilesFragment.this.refresh();
        dialog.dismiss();
        SoundUtils.playSound(getActivity());*/

    }

    @Override
    public void setFragmentListener(FragmentListener listener) {

        this.listener = listener;
    }

    @Override
    public void doUpdateFragment() {

        if(this.listener != null)
        {
            ((Activity)listener).setTitle(this.getTitle());
        }
    }
}
