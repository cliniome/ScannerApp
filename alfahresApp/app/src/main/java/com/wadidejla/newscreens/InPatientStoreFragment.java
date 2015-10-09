package com.wadidejla.newscreens;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.degla.restful.models.BooleanResult;
import com.degla.restful.models.FileModelStates;
import com.degla.restful.models.RestfulFile;
import com.degla.restful.models.SyncBatch;
import com.degla.restful.models.http.HttpResponse;
import com.wadidejla.network.AlfahresConnection;
import com.wadidejla.newscreens.adapters.InPatientCheckoutAdapter;
import com.wadidejla.newscreens.utils.BarcodeUtils;
import com.wadidejla.newscreens.utils.NetworkUtils;
import com.wadidejla.newscreens.utils.NewViewUtils;
import com.wadidejla.settings.SystemSettingsManager;
import com.wadidejla.utils.SoundUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import wadidejla.com.alfahresapp.R;

/**
 * Created by snouto on 08/10/15.
 */
public class InPatientStoreFragment extends Fragment implements IFragment {



    private InPatientCheckoutAdapter adapter;
    private ListView listView;
    private FragmentListener listener;
    private SystemSettingsManager settingsManager;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = (View)inflater.inflate(R.layout.inpatient_storage_screen_layout,container,false);
        this.initView(rootView);
        return rootView;
    }

    private void initView(View rootView) {


        this.listView = (ListView)rootView.findViewById(R.id.mainFilesList);
        this.adapter = new InPatientCheckoutAdapter(getActivity(),R.layout.new_single_file_view);
        this.listView.setAdapter(this.adapter);
        settingsManager = SystemSettingsManager.createInstance(getActivity());

    }

    @Override
    public String getTitle() {
        String title =  getResources().getString(R.string.INPATIENT_STORAGE_SCREEN_TITLE);

        title = String.format("%s(%s)", title, this.adapter.getCount());

        return title;
    }

    @Override
    public void chainUpdate() {

    }



    @Override
    public void refresh() {


        if(this.adapter != null)
        {
            this.adapter.refresh();
        }

    }

    @Override
    public void handleScanResults(String barcode) {

        try
        {
            if(barcode == null ||barcode.isEmpty()) return;

            BarcodeUtils utils = new BarcodeUtils(barcode);

            final AlertDialog waitingDialog = NewViewUtils.getWaitingDialog(getActivity());
            waitingDialog.show();

            if(utils.isMedicalFile())
            {
                Runnable runnable = getRunnableThread(barcode,waitingDialog);

                Thread networkThread = new Thread(runnable);
                networkThread.start();



            }else if(utils.isTemporaryShelf()) // that means
            {
                waitingDialog.dismiss();
               /*
               1.receive
2.store temporary
3.check file status
4.send to mrd
                */

                //Get the restful File
                if (this.adapter.getFiles() != null && this.adapter.getFiles().size() >0)
                {
                    for(RestfulFile file : this.adapter.getFiles()) {

                        //file.setTemporaryCabinetId(barcode);
                        file.setDeviceOperationDate(new Date().getTime());
                        file.setEmp(settingsManager.getAccount());
                        file.setState(FileModelStates.TEMPORARY_STORED.toString());
                        file.setReadyFile(RestfulFile.READY_FILE);
                        file.setShelfId(barcode);
                        file.setProcessed(false);
                        settingsManager.getFilesManager().getFilesDBManager().insertFile(file);

                    }

                    //clear all the files within the adapter
                    this.adapter.getFiles().clear();
                    this.adapter.setFiles(new ArrayList<RestfulFile>());
                    this.refresh();
                    SoundUtils.playSound(getActivity());
                    NetworkUtils.ScheduleSynchronization(getActivity());
                }

            }





        }catch (Exception s)
        {
            Log.e("InPatientCheckOut",s.getMessage());
        }

    }

    private Runnable getRunnableThread(final String barcode,final AlertDialog waitingDialog)
    {
        return new Runnable() {
            @Override
            public void run() {

                try
                {
                    //if only medical file , process it
                    AlfahresConnection connection = settingsManager
                            .getConnection();
                    HttpResponse response= connection.setAuthorization(settingsManager.getAccount().getUserName(), settingsManager.getAccount().getPassword())
                            .setMethodType(AlfahresConnection.GET_HTTP_METHOD)
                            .path(String.format("files/receiveInPatient?fileNumber=%s",barcode))
                            .call(SyncBatch.class);


                    if(response != null && Integer.parseInt(response.getResponseCode()) ==
                            HttpResponse.OK_HTTP_CODE)
                    {
                        //get the sync Batch
                        BooleanResult boolResult = (BooleanResult)response.getPayload();

                        if(!boolResult.isState())
                        {
                            //get the message
                            final String msg = boolResult.getMessage();

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    SoundUtils.PlayError(getActivity());

                                    waitingDialog.dismiss();

                                    AlertDialog dialog = NewViewUtils.getAlertDialog(getActivity(), "Warning"
                                            , msg);

                                    dialog.show();
                                }
                            });

                        }else
                        {
                            SyncBatch batch = (SyncBatch)response.getPayload();

                            if(batch != null)
                            {
                                //get the file
                                List<RestfulFile> foundFiles = batch.getFiles();

                                //get the first file
                                if(foundFiles != null && foundFiles.size() > 0)
                                {
                                    final RestfulFile foundFile = foundFiles.get(0);

                                    //add it
                                    InPatientStoreFragment.this.adapter.addFile(foundFile);



                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            //add it to the adapter in here
                                            //Play the sound
                                            SoundUtils.playSound(getActivity());
                                            InPatientStoreFragment.this.refresh();

                                            NetworkUtils.ScheduleSynchronization(getActivity());
                                        }
                                    });
                                }
                            }
                        }
                    }else
                    {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                SoundUtils.PlayError(getActivity());

                                AlertDialog dialog = NewViewUtils.getAlertDialog(getActivity(),"Warning"
                                        ,"The Patient File does not exist or you are not authorized to scan " +
                                        "that file");

                                dialog.show();
                            }
                        });
                    }

                }catch (Exception s)
                {
                    s.printStackTrace();
                }
                finally {
                    waitingDialog.dismiss();
                }

            }
        };
    }

    @Override
    public void setFragmentListener(FragmentListener listener) {

        this.listener = listener;

    }
}
