package com.wadidejla.newscreens;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.opengl.Visibility;
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
import com.wadidejla.newscreens.adapters.KeeperCheckInAdapter;
import com.wadidejla.newscreens.utils.BarcodeUtils;
import com.wadidejla.newscreens.utils.DBStorageUtils;
import com.wadidejla.newscreens.utils.NewViewUtils;
import com.wadidejla.newscreens.utils.ScannerUtils;
import com.wadidejla.settings.SystemSettingsManager;
import com.wadidejla.tasks.MarkingTask;
import com.wadidejla.tasks.ScanAndReceiveTask;
import com.wadidejla.utils.EmployeeUtils;
import com.wadidejla.utils.SoundUtils;

import java.util.ArrayList;
import java.util.List;

import wadidejla.com.alfahresapp.R;

import static com.wadidejla.newscreens.utils.ScannerUtils.SCANNER_TYPE_CAMERA;

/**
 * Created by snouto on 09/06/15.
 */
public class NewArchiveFilesFragment extends Fragment implements Archiver {



    private ListView archiveListView;
    private KeeperCheckInAdapter adapter;

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

            this.archiveListView = (ListView)rootView.findViewById(R.id.mainFilesList);
            DBStorageUtils storageUtils = new DBStorageUtils(getActivity());

            List<RestfulFile> receivedFiles = storageUtils.getReceivedFiles();

            if(receivedFiles == null) receivedFiles = new ArrayList<RestfulFile>();

            this.adapter = new KeeperCheckInAdapter(getActivity(),R.layout.new_single_file_view,receivedFiles);
            this.archiveListView.setAdapter(this.adapter);

            //bind the options in here
            Button refreshAction = (Button)rootView.findViewById(R.id.new_files_layout_refresh_btn);

            refreshAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    ProgressDialog dialog = NewViewUtils.getWaitingDialog(getActivity());
                    dialog.show();

                    NewArchiveFilesFragment.this.refresh();

                    dialog.dismiss();
                }
            });


           /* //Bind the scan button
            final Button scanButton = (Button)rootView.findViewById(R.id.new_files_layout_scan_btn);
           *//* scanButton.setVisibility(View.GONE);*//*

            scanButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    //Scan for temporary container
                    ScannerUtils.ScanBarcode(getActivity(), SCANNER_TYPE_CAMERA,
                            NewArchiveFilesFragment.this,false,null);

                }
            });*/

            //Bind do actions button
            final Button DoActionsBtn = (Button)rootView.findViewById(R.id.new_receive_actions_btn);

            DoActionsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    PopupMenu menu = new PopupMenu(getActivity(),view);
                    menu.inflate(R.menu.keeper_archive_pop_menu);


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
                                                        NewArchiveFilesFragment.this.refresh();
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
        return getResources().getString(R.string.KEEPER_ARCHIVE_TITLE);
    }

    @Override
    public void chainUpdate() {

        this.refresh();
    }



    @Override
    public void refresh() {

        if(this.adapter != null)
            this.adapter.notifyDataSetChanged();

    }

    @Override
    public void handleScanResults(String barcode) {

        //That means it is normal querying for trolley to retrieve contained files

        if(barcode != null)
        {
            BarcodeUtils barcodeUtils = new BarcodeUtils(barcode);

            if(barcodeUtils.isTrolley())
            {
                //TODO : Retrieve all files from the server through the trolley barcode
                ScanAndReceiveTask scanTask = new ScanAndReceiveTask(getActivity(),
                        barcode,NewArchiveFilesFragment.this);
                ProgressDialog dialog = NewViewUtils.getWaitingDialog(getActivity());
                dialog.show();
                scanTask.setDialog(dialog);
                scanTask.setFragment(NewArchiveFilesFragment.this);

                //Start the scanning process
                Thread scanningThread = new Thread(scanTask);
                scanningThread.start();


            }else if (barcodeUtils.isMedicalFile())
            {
                //Mark the current received file as active

                //Get all received Files from the database
                DBStorageUtils storageUtils  = new DBStorageUtils(getActivity());

                List<RestfulFile> receivedFiles = storageUtils.getReceivedFiles();

                RestfulFile foundFile = null;

                if(receivedFiles != null)
                {
                    for(RestfulFile file : receivedFiles)
                    {
                        if(file.getFileNumber().equals(barcode))
                        {
                            foundFile = file;
                            break;
                        }
                    }

                    //now check if the current found file is not null
                    if(foundFile != null)
                    {
                        //Mark the file as toggle the file selection state
                        if(foundFile.getSelected() > 0)
                            foundFile.setSelected(0);
                        else foundFile.setSelected(1);

                        //Then save it
                        storageUtils.insertOrUpdateFile(foundFile);
                        //Refresh the current screen
                        this.refresh();
                    }
                }

            }else if (barcodeUtils.isShelf())
            {
                //Get the selected file only from the received files and update its shelf
                List<RestfulFile> receivedFiles = new DBStorageUtils(getActivity()).getReceivedFiles();

                if(receivedFiles != null)
                {
                    RestfulFile targetFile = null;

                    for(RestfulFile foundFile : receivedFiles)
                    {
                        if(foundFile.getSelected() > 0 ) //that means it is selected already
                        {
                            targetFile = foundFile;
                            break;
                        }
                    }

                    //assign the shelf to it
                    if(targetFile != null)
                    {
                        targetFile.setShelfId(barcode);
                        targetFile.setState(FileModelStates.CHECKED_IN.toString());
                        targetFile.setReadyFile(RestfulFile.READY_FILE);

                        //operate on that file
                        new DBStorageUtils(getActivity())
                                .operateOnFile(targetFile,FileModelStates.CHECKED_IN.toString(),
                                        RestfulFile.READY_FILE);
                        SoundUtils.playSound(getActivity());
                        this.refresh();
                    }else
                    {
                        Toast.makeText(getActivity(),"There is no file selected",Toast.LENGTH_LONG)
                                .show();
                    }
                }
            }


        }else
        {
            Toast.makeText(getActivity(), "Barcode Is empty", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void handleShelfBarcode(String fileNumber, String shelfNumber) {

        try
        {
            //Get the File by knowing its file number
            DBStorageUtils storageUtils = new DBStorageUtils(getActivity());

            RestfulFile foundFile = null;

            List<RestfulFile> receivedFiles = storageUtils.getReceivedFiles();

            if(receivedFiles != null)
            {
                for(RestfulFile file : receivedFiles)
                {
                    if(file.getFileNumber().equalsIgnoreCase(fileNumber))
                    {
                        foundFile = file;
                        break;
                    }
                }
            }

            if(foundFile != null)
            {
                //set the shelf code to that file and mark it as check_in and ready
                foundFile.setShelfId(shelfNumber);
                foundFile.setState(FileModelStates.CHECKED_IN.toString());
                foundFile.setReadyFile(RestfulFile.READY_FILE);

                //operate on that file
                storageUtils.operateOnFile(foundFile,FileModelStates.CHECKED_IN.toString(),
                        RestfulFile.READY_FILE);
                SoundUtils.playSound(getActivity());
                this.refresh();
             }

        }catch (Exception s)
        {
            s.printStackTrace();
        }

    }
}
