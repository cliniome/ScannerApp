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
import com.wadidejla.newscreens.adapters.NewReceiveFilesAdapter;
import com.wadidejla.newscreens.utils.DBStorageUtils;
import com.wadidejla.newscreens.utils.NewViewUtils;
import com.wadidejla.newscreens.utils.ScannerUtils;
import com.wadidejla.tasks.ScanAndReceiveTask;
import com.wadidejla.utils.SoundUtils;

import java.util.List;

import wadidejla.com.alfahresapp.R;

/**
 * Created by snouto on 09/06/15.
 */
public class NewReceiveFilesFragment extends Fragment implements IFragment{


    private ListView receiveFilesList;
    private NewReceiveFilesAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        View rootView = inflater.inflate(R.layout.new_receive_files_layout,container,false);
        this.initView(rootView);
        return rootView;

    }

    private void initView(View rootView) {

        try
        {
            DBStorageUtils storageUtils = new DBStorageUtils(getActivity());

            this.setReceiveFilesList((ListView)rootView.findViewById(R.id.mainFilesList));

            List<RestfulFile> receivedFiles = storageUtils.getReceivedFiles();

            this.setAdapter(new NewReceiveFilesAdapter(getActivity(),R.layout.new_single_file_view
                    ,receivedFiles));

            this.getReceiveFilesList().setAdapter(this.getAdapter());


            //Bind the action buttons in here
            //Access the refresh button
            Button refreshAction = (Button)rootView.findViewById(R.id.new_files_layout_refresh_btn);

            refreshAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    ProgressDialog dialog = NewViewUtils.getWaitingDialog(getActivity());
                    dialog.show();

                    NewReceiveFilesFragment.this.refresh();

                    dialog.dismiss();
                }
            });

            final Button scanButton = (Button)rootView.findViewById(R.id.new_files_layout_scan_btn);

            scanButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    //Scan for temporary container
                    String fileBarcode = ScannerUtils.ScanBarcode(getActivity());

                    if(fileBarcode != null)
                    {
                       //TODO : Retrieve all files from the server through the trolley barcode
                        ScanAndReceiveTask scanTask = new ScanAndReceiveTask(getActivity(),
                                fileBarcode,NewReceiveFilesFragment.this);
                        ProgressDialog dialog = NewViewUtils.getWaitingDialog(getActivity());
                        dialog.show();
                        scanTask.setDialog(dialog);
                        scanTask.setFragment(NewReceiveFilesFragment.this);

                        //Start the scanning process
                        Thread scanningThread = new Thread(scanTask);
                        scanningThread.start();

                    }else
                    {
                        Toast.makeText(getActivity(),"Barcode Is empty",Toast.LENGTH_SHORT)
                                .show();
                    }

                }
            });


            //Do action
            final Button DoActionsBtn = (Button)rootView.findViewById(R.id.new_receive_actions_btn);

            DoActionsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    PopupMenu menu = new PopupMenu(getActivity(),view);
                    menu.inflate(R.menu.new_receive_pop_menu);
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

                                                    List<RestfulFile> availableFiles = storageUtils.getReceivedFiles();

                                                    if (availableFiles != null) {
                                                        for (RestfulFile file : availableFiles) {
                                                            storageUtils.operateOnFile(file, FileModelStates.MISSING.toString(),
                                                                    RestfulFile.READY_FILE);
                                                        }

                                                        //Empty all files
                                                        storageUtils.getReceivedFiles().clear();

                                                        //now update
                                                        SoundUtils.playSound(getActivity());
                                                        NewReceiveFilesFragment.this.refresh();
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



        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }

    @Override
    public String getTitle() {
        return getResources().getString(R.string.ScreenUtils_Receive_Files);
    }

    @Override
    public void chainUpdate() {

        this.refresh();

    }

    @Override
    public void refresh() {

        if(this.getAdapter() != null)
            this.getAdapter().notifyDataSetChanged();

    }

    @Override
    public void handleScanResults(String barcode) {

    }


    public ListView getReceiveFilesList() {
        return receiveFilesList;
    }

    public void setReceiveFilesList(ListView receiveFilesList) {
        this.receiveFilesList = receiveFilesList;
    }

    public NewReceiveFilesAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(NewReceiveFilesAdapter adapter) {
        this.adapter = adapter;
    }
}
