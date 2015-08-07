package com.wadidejla.newscreens;

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
import com.wadidejla.newscreens.adapters.NewOutgoingCoordinatorAdapter;
import com.wadidejla.newscreens.adapters.NewOutgoingFilesAdapter;
import com.wadidejla.newscreens.utils.BarcodeUtils;
import com.wadidejla.newscreens.utils.DBStorageUtils;
import com.wadidejla.newscreens.utils.NewViewUtils;
import com.wadidejla.tasks.ManualSyncTask;
import com.wadidejla.utils.SoundUtils;

import java.util.List;

import wadidejla.com.alfahresapp.R;

/**
 * Created by snouto on 08/06/15.
 */
public class NewOutgoingCoordinatorFragment extends Fragment implements IFragment , IAdapterListener {


    private ListView outgoingList;
    private NewOutgoingCoordinatorAdapter adapter;

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
            this.adapter = new NewOutgoingCoordinatorAdapter(getActivity(),R.layout.new_single_file_view);
            this.adapter.setListener(this);

            this.outgoingList.setAdapter(this.adapter);


            //Access the refresh button
            Button refreshAction = (Button)rootView.findViewById(R.id.new_outgoing_layout_refresh_btn);

            refreshAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    ProgressDialog dialog = NewViewUtils.getWaitingDialog(getActivity());
                    dialog.show();

                    NewOutgoingCoordinatorFragment.this.refresh();

                    dialog.dismiss();
                }
            });


            //Access the syncing process
            final Button syncButton = (Button)rootView.findViewById(R.id.new_outgoing_sync_btn);

            syncButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ProgressDialog dialog = new NewViewUtils().getWaitingDialog(getActivity());
                    dialog.show();

                    ManualSyncTask syncTask = new ManualSyncTask(getActivity());
                    syncTask.setProgressDialog(dialog);
                    syncTask.setCurrentFragment(NewOutgoingCoordinatorFragment.this);

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
                                                        NewOutgoingCoordinatorFragment.this.refresh();
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
            });*/

        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }

    @Override
    public String getTitle() {
        String title =  getResources().getString(R.string.ScreenUtils_Ongoing_Files);

        if(this.adapter != null)
        {
            title = String.format("%s(%s)",title,this.adapter.getCount());
        }

        return title;
    }

    @Override
    public void chainUpdate() {

        if(this.adapter != null)
            this.adapter.notifyDataSetChanged();

    }

    @Override
    public void refresh() {

        this.chainUpdate();

    }

    @Override
    public void handleScanResults(String trolleyBarcode) {


      /*  BarcodeUtils barcodeUtils = new BarcodeUtils(trolleyBarcode);


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
        NewOutgoingCoordinatorFragment.this.refresh();
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
            this.listener.invalidate();
    }
}
