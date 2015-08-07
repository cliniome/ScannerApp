package com.wadidejla.newscreens;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.PopupMenu;

import com.degla.restful.models.BooleanResult;
import com.degla.restful.models.CollectionBatch;
import com.degla.restful.models.FileModelStates;
import com.degla.restful.models.RestfulClinic;
import com.degla.restful.models.RestfulFile;
import com.degla.restful.models.SyncBatch;
import com.degla.restful.models.http.HttpResponse;
import com.wadidejla.network.AlfahresConnection;
import com.wadidejla.newscreens.adapters.NewTransferFilesAdapter;
import com.wadidejla.newscreens.utils.ConnectivityUtils;
import com.wadidejla.newscreens.utils.DBStorageUtils;
import com.wadidejla.newscreens.utils.NewViewUtils;
import com.wadidejla.newscreens.utils.ScannerUtils;
import com.wadidejla.settings.SystemSettingsManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import wadidejla.com.alfahresapp.R;

import static com.wadidejla.newscreens.utils.ScannerUtils.SCANNER_TYPE_CAMERA;

/**
 * Created by snouto on 20/06/15.
 */
public class NewTransferFilesFragment extends Fragment implements IFragment {


    private ExpandableListView expandableListView;
    private NewTransferFilesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.coordinator_main_list_view,container,false);

        this.initView(rootView);

        return rootView;
    }

    private void initView(View rootView) {

        try
        {
            this.expandableListView = (ExpandableListView)rootView.findViewById(R.id.coordinator_list_view);
            this.adapter = new NewTransferFilesAdapter(getActivity());
            this.expandableListView.setAdapter(this.adapter);

            //Now bind the buttons

           /* //Bind the refresh Button
            Button refreshButton = (Button)rootView.findViewById(R.id.new_files_layout_refresh_btn);

            refreshButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    NewTransferFilesFragment.this.refresh();
                }
            });*/




            // Bind the options button
            Button optionsBtn = (Button)rootView.findViewById(R.id.new_receive_actions_btn);

            optionsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final PopupMenu menu = new PopupMenu(getActivity(),view);
                    menu.inflate(R.menu.new_coordinator_transfer_pop_menu);

                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {

                            switch (menuItem.getItemId())
                            {
                                case R.id.pop_mark_all_missing:
                                {
                                    final AlertDialog choiceDialog = NewViewUtils.getChoiceDialog(getActivity(),
                                            "Mark all files as Missing", "Are you sure to Mark files as Missing ?"
                                            , new Runnable() {
                                                @Override
                                                public void run() {

                                                    //get all files
                                                    SystemSettingsManager settingsManager = SystemSettingsManager.createInstance(getActivity());

                                                    List<RestfulFile> transferrableFiles = settingsManager.getTransferrableFiles();

                                                    if(transferrableFiles != null || transferrableFiles.size() > 0)
                                                    {
                                                        for(RestfulFile transferrableFile : transferrableFiles)
                                                        {
                                                            transferrableFile.setState(FileModelStates.MISSING.toString());
                                                            transferrableFile.setEmp(settingsManager.getAccount());
                                                            transferrableFile.setReadyFile(RestfulFile.READY_FILE);

                                                            //now operate on that file
                                                            settingsManager.getFilesManager().getFilesDBManager()
                                                                    .insertFile(transferrableFile);
                                                        }

                                                        //now remove all these
                                                        settingsManager.setTransferrableFiles(new ArrayList<RestfulFile>());
                                                    }

                                                }
                                            },null);

                                    choiceDialog.show();
                                }
                                break;

                                case R.id.pop_mark_all_transfer:
                                {
                                    final AlertDialog choiceDialog = NewViewUtils.getChoiceDialog(getActivity(), "Transfer All Files",
                                            "Are you sure to Transfer all These Files ?", new Runnable() {
                                                @Override
                                                public void run() {

                                                    try
                                                    {

                                                        //get all files
                                                        final SystemSettingsManager settingsManager = SystemSettingsManager.createInstance(getActivity());

                                                        final List<RestfulFile> transferrableFiles = settingsManager.getTransferrableFiles();

                                                        if(transferrableFiles != null && transferrableFiles.size() > 0)
                                                        {
                                                            //begin the transfer process
                                                            Runnable transferTask = new Runnable() {
                                                                @Override
                                                                public void run() {

                                                                    //Create a sync Batch
                                                                    SyncBatch batch = new SyncBatch();
                                                                    batch.setCreatedAt(new Date().getTime());
                                                                    batch.setFiles(transferrableFiles);
                                                                    AlfahresConnection connection = settingsManager.getConnection();
                                                                    connection.setMethodType(AlfahresConnection.POST_HTTP_METHOD)
                                                                            .setAuthorization(settingsManager.getAccount().getUserName(),
                                                                                    settingsManager.getAccount().getPassword())
                                                                            .setBody(batch)
                                                                            .call(BooleanResult.class);


                                                                }
                                                            };

                                                            Thread transferThread = new Thread(transferTask);
                                                            transferThread.start();
                                                        }

                                                    }catch (Exception s)
                                                    {
                                                        s.printStackTrace();
                                                    }
                                                }
                                            },null);

                                    choiceDialog.show();
                                }
                                break;
                            }

                            return true;
                        }
                    });
                }
            });

        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }

    @Override
    public String getTitle() {
        return getResources().getString(R.string.TRANSFER_FILES_SCREEN_TITLE);
    }

    @Override
    public void chainUpdate() {

    }

    @Override
    public void refresh() {

        if(ConnectivityUtils.isConnected(getActivity()))
        {
            final DBStorageUtils storageUtils = new DBStorageUtils(getActivity());

            final AlertDialog waitingDialog = NewViewUtils.getWaitingDialog(getActivity());

            waitingDialog.show();

            Runnable networkingTask = new Runnable() {
                @Override
                public void run() {

                   try
                   {
                       AlfahresConnection connection = storageUtils.getSettingsManager().getConnection();

                       HttpResponse response = connection.path("files/collect").setMethodType(AlfahresConnection.POST_HTTP_METHOD)
                               .setAuthorization(storageUtils.getSettingsManager().getAccount().getUserName(),
                                       storageUtils.getSettingsManager().getAccount().getPassword())
                               .call(CollectionBatch.class);

                       if(response != null && Integer.parseInt(response.getResponseCode())==
                               HttpResponse.OK_HTTP_CODE)
                       {
                           //Get the collection Batch payload
                           CollectionBatch batch = (CollectionBatch)response.getPayload();

                           //save the current files into the local database
                           List<RestfulFile> allFiles = new ArrayList<RestfulFile>();

                           if(batch != null && batch
                                   .getTransferrableFiles() != null && batch.getTransferrableFiles().size() > 0)
                           {
                               //that means there are some data ,
                               for(RestfulClinic clinic : batch.getTransferrableFiles())
                               {
                                   if(clinic.getFiles() != null)
                                   {
                                       for(RestfulFile tempFile : clinic.getFiles())
                                       {
                                           allFiles.add(tempFile);

                                       }
                                   }
                               }
                           }

                           SystemSettingsManager settingsManager =
                                   SystemSettingsManager.createInstance(getActivity());

                           settingsManager.setTransferrableFiles(allFiles);
                       }


                   }catch (Exception s)
                   {
                       s.printStackTrace();
                   }
                    finally {

                       getActivity().runOnUiThread(new Runnable() {
                           @Override
                           public void run() {

                               waitingDialog.dismiss();
                           }
                       });
                   }
                }
            };

            // start the network thread
            Thread networkingThread =new Thread(networkingTask);
            networkingThread.start();


        }

    }

    @Override
    public void handleScanResults(String barcode) {

    }

    @Override
    public void setFragmentListener(FragmentListener listener) {

    }
}
