package com.wadidejla.newscreens;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.net.Network;
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
import com.degla.restful.models.SyncBatch;
import com.degla.restful.models.http.HttpResponse;
import com.wadidejla.network.AlfahresConnection;
import com.wadidejla.newscreens.adapters.NewReceiveFilesAdapter;
import com.wadidejla.newscreens.utils.BarcodeUtils;
import com.wadidejla.newscreens.utils.DBStorageUtils;
import com.wadidejla.newscreens.utils.NetworkUtils;
import com.wadidejla.newscreens.utils.NewViewUtils;
import com.wadidejla.newscreens.utils.ScannerUtils;
import com.wadidejla.settings.SystemSettingsManager;
import com.wadidejla.tasks.MarkingTask;
import com.wadidejla.tasks.ScanAndReceiveTask;
import com.wadidejla.utils.EmployeeUtils;
import com.wadidejla.utils.SoundUtils;
import static com.wadidejla.newscreens.utils.ScannerUtils.*;
import java.util.List;

import wadidejla.com.alfahresapp.R;

/**
 * Created by snouto on 09/06/15.
 */
public class NewReceiveFilesFragment extends Fragment implements IFragment , IAdapterListener{


    private ListView receiveFilesList;
    private NewReceiveFilesAdapter adapter;

    private FragmentListener listener;


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




            //Do action
            final Button DoActionsBtn = (Button)rootView.findViewById(R.id.new_receive_actions_btn);

            DoActionsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    PopupMenu menu = new PopupMenu(getActivity(),view);
                    menu.inflate(R.menu.new_receive_pop_menu);

                  /*  SystemSettingsManager settingsManager = SystemSettingsManager.createInstance(getActivity());

                    if(settingsManager.getAccount().isKeeper() ||
                            settingsManager.getAccount().isReceptionist())
                    {
                        menu.getMenu().getItem(R.id.pop_mark_all_received).setVisible(false);
                    }*/

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

                                case R.id.pop_mark_all_received: {
                                    ProgressDialog dialog = NewViewUtils.getWaitingDialog(getActivity());
                                    dialog.show();

                                    //start The marking Task in the background
                                    MarkingTask markingTask = new MarkingTask(getActivity(), EmployeeUtils.RECEIVE_FILES);
                                    markingTask.setDialog(dialog);
                                    markingTask.setFragment(NewReceiveFilesFragment.this);

                                    //now encapsulate the task into a thread
                                    Thread markingThread = new Thread(markingTask);
                                    markingThread.start();
                                }

                                break;

                                case R.id.pop_mark_all_clear:
                                {
                                    List<RestfulFile> receivedFiles = SystemSettingsManager.createInstance(getActivity())
                                            .getReceivedFiles();

                                    if(receivedFiles!= null && receivedFiles.size() > 0)
                                    {
                                        for(RestfulFile file : receivedFiles)
                                        {
                                            //delete that file
                                            SystemSettingsManager.createInstance(getActivity())
                                                    .getFilesManager().getFilesDBManager()
                                                    .deleteFile(file.getFileNumber());
                                        }

                                        //clear all
                                        receivedFiles.clear();
                                    }

                                    //then refresh
                                    NewReceiveFilesFragment.this.refresh();
                                    SoundUtils.playSound(getActivity());
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
        String title =  getResources().getString(R.string.ScreenUtils_Receive_Files);

        if(this.adapter != null)
        {
            title = String.format("%s(%s)",title,this.adapter.getCount());
        }

        return title;
    }

    @Override
    public void chainUpdate() {

        this.refresh();

    }

    @Override
    public void refresh() {

        if(this.getAdapter() != null)
            this.getAdapter().notifyDataSetChanged();

        if(this.listener != null)
        {
            ((Activity)this.listener).setTitle(this.getTitle());
        }

        NetworkUtils.ScheduleSynchronization(getActivity());

    }

    @Override
    public void handleScanResults(final String fileBarcode) {

        if(fileBarcode != null)
        {
            BarcodeUtils utils = new BarcodeUtils(fileBarcode);

            if(utils.isTrolley()) //that means it is a trolley
            {

                ScanAndReceiveTask scanTask = new ScanAndReceiveTask(getActivity(),
                        fileBarcode,NewReceiveFilesFragment.this);
                ProgressDialog dialog = NewViewUtils.getWaitingDialog(getActivity());
                dialog.show();
                scanTask.setDialog(dialog);
                scanTask.setFragment(NewReceiveFilesFragment.this);

                //Start the scanning process
                Thread scanningThread = new Thread(scanTask);
                scanningThread.start();


            }else if (utils.isMedicalFile())
            {
                final SystemSettingsManager settingsManager = SystemSettingsManager.createInstance(getActivity());

                if(settingsManager.getReceivedFiles() != null && settingsManager.getReceivedFiles().size() > 0)
                {
                    RestfulFile foundFile = null;

                    for(RestfulFile file : settingsManager.getReceivedFiles())
                    {
                        if(file.getFileNumber().equals(fileBarcode))
                        {
                            foundFile = file;
                            break;
                        }
                    }

                    if(foundFile == null)
                    {
                        Toast.makeText(getActivity(),String.format("%s not Found",fileBarcode),Toast.LENGTH_LONG)
                                .show();
                        return;
                    }

                    //Remove the restful file from the received Files
                    settingsManager.getReceivedFiles().remove(foundFile);

                    DBStorageUtils storageUtils = new DBStorageUtils(getActivity());
                    //Remove it from the database
                    storageUtils.operateOnFile(foundFile, FileModelStates.COORDINATOR_IN.toString(),
                            RestfulFile.READY_FILE);

                    //Play the sound
                    SoundUtils.playSound(getActivity());

                    this.refresh();



                }else
                {
                    final AlertDialog waitingDialog = NewViewUtils.getWaitingDialog(getActivity());
                    waitingDialog.show();
                    //that means it is an individual file
                    Runnable individualFileTask = new Runnable() {
                        @Override
                        public void run() {

                            try
                            {
                                AlfahresConnection connection = settingsManager.getConnection();
                                HttpResponse response = connection.setAuthorization(settingsManager.getAccount().getUserName(),
                                        settingsManager.getAccount().getPassword())
                                        .setMethodType(AlfahresConnection.GET_HTTP_METHOD)
                                        .path(String.format("files/oneFile?fileNumber=%s",fileBarcode))
                                        .call(SyncBatch.class);

                                if(response != null && Integer.parseInt(response.getResponseCode())
                                        == HttpResponse.OK_HTTP_CODE)
                                {
                                    //get that file
                                    SyncBatch batch = (SyncBatch)response.getPayload();

                                    if(batch.getFiles() != null && batch.getFiles().size() > 0)
                                    {
                                        //get that individual file
                                        RestfulFile individualFile = batch.getFiles().get(0);

                                        //mark that file as coordinator_in (Received) and make it ready
                                        individualFile.setState(FileModelStates.COORDINATOR_IN.toString());
                                        individualFile.setEmp(settingsManager.getAccount());
                                        individualFile.setReadyFile(RestfulFile.READY_FILE);

                                        //now save it into the database
                                        settingsManager.getFilesManager().getFilesDBManager().insertFile(individualFile);
                                        settingsManager.addToReceivedFiles(individualFile);



                                    }else
                                    {
                                        NewReceiveFilesFragment.this.getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                //dismiss the current waitingDialog
                                                waitingDialog.dismiss();

                                                final AlertDialog choiceDialog = NewViewUtils.getAlertDialog(getActivity(),
                                                        "Scan Results", "There are no files for the moment !");

                                                choiceDialog.show();


                                            }
                                        });
                                    }
                                }else
                                {
                                    NewReceiveFilesFragment.this.getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            //dismiss the current waitingDialog
                                            waitingDialog.dismiss();

                                            final AlertDialog choiceDialog = NewViewUtils.getAlertDialog(getActivity(),
                                                    "Scan Results", "There are no files for the moment !,Or you don't have " +
                                                            "Permissions to access that file");

                                            choiceDialog.show();


                                        }
                                    });
                                }

                            }catch (Exception s)
                            {
                                s.printStackTrace();
                            }
                            finally {

                                NewReceiveFilesFragment.this.getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        NewReceiveFilesFragment.this.refresh();
                                        SoundUtils.playSound(getActivity());
                                        try
                                        {
                                            waitingDialog.dismiss();

                                        }catch (Exception s)
                                        {

                                        }
                                    }
                                });
                            }

                        }
                    };//the end of the individualFileTask

                    Thread scanThread = new Thread(individualFileTask);
                    scanThread.start();
                }

            }

        }else
        {
            Toast.makeText(getActivity(),"Barcode Is empty",Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void setFragmentListener(FragmentListener listener) {

        this.listener = listener;
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

    @Override
    public void doUpdateFragment() {

        if(this.listener != null)
            this.listener.invalidate();
    }
}
