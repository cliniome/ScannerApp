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
import com.wadidejla.newscreens.adapters.InpatientFilesAdapter;
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
 * Created by snouto on 06/10/15.
 */
public class CheckOutInpatientFragment extends Fragment implements IFragment {



    private ListView listview;
    private FragmentListener listener;

    private InpatientFilesAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.checkout_inpatient_layout,container,false);

        this.initView(rootView);

        return rootView;


    }

    private void initView(View rootView) {

        try
        {
            listview = (ListView)rootView.findViewById(R.id.mainFilesList);

            adapter = new InpatientFilesAdapter(getActivity(),R.layout.new_single_file_view);

            listview.setAdapter(adapter);


        }catch (Exception s)
        {
            Log.e("Checkout-Inpatient",s.getMessage());
        }
    }

    @Override
    public String getTitle() {

        String title = getResources().getString(R.string.INPATIENT_SCREEN_TITLE);

        title = String.format("%s(%s)",title,this.adapter.getCount());

        return title;
    }

    @Override
    public void chainUpdate() {

    }

    @Override
    public void refresh() {

        if(this.adapter != null)
            this.adapter.refresh();

    }

    //sortFileInpatient
    @Override
    public void handleScanResults(final String barcode) {


        try
        {
           final SystemSettingsManager settingsManager = SystemSettingsManager.createInstance(getActivity());

            if(barcode != null && !barcode.isEmpty())
            {
                //check to see if the current barcode is a true file number
                BarcodeUtils utils = new BarcodeUtils(barcode);

                if(utils.isMedicalFile())
                {
                    //that means the file is a medical file and not a trolley for example
                    final AlertDialog waitingDialog = NewViewUtils.getWaitingDialog(getActivity());
                    waitingDialog.show();

                    Runnable networkThread = new Runnable() {
                        @Override
                        public void run() {

                            try
                            {


                                //if only medical file , process it
                                AlfahresConnection connection = settingsManager
                                        .getConnection();
                                HttpResponse response= connection.setAuthorization(settingsManager.getAccount().getUserName(), settingsManager.getAccount().getPassword())
                                        .setMethodType(AlfahresConnection.GET_HTTP_METHOD)
                                        .path(String.format("files/sortFileInpatient?fileNumber=%s",barcode))
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

                                                AlertDialog dialog = NewViewUtils.getAlertDialog(getActivity(),"Warning"
                                                        ,msg);

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

                                                //Add it to the adapter
                                                CheckOutInpatientFragment.this.adapter.addFile(foundFile);



                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {



                                                        //add it to the adapter in here

                                                        //Play the sound
                                                        SoundUtils.playSound(getActivity());
                                                        CheckOutInpatientFragment.this.refresh();
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

                    Thread retrieveFileThread = new Thread(networkThread);
                    retrieveFileThread.start();

                }else if (utils.isTrolley())
                {

                    if(this.adapter.getCount() <= 0) return;
                    else
                    {
                        for(RestfulFile current:this.adapter.getFiles())
                        {
                            RestfulFile foundFile  = current;

                            if(foundFile == null) return;

                            foundFile.setTemporaryCabinetId(barcode);
                            foundFile.setReadyFile(RestfulFile.READY_FILE);
                            foundFile.setEmp(settingsManager.getAccount());
                            foundFile.setState(FileModelStates.CHECKED_OUT.toString());
                            foundFile.setDeviceOperationDate(new Date().getTime());

                            //now save it
                            settingsManager.getFilesManager().getFilesDBManager().insertFile(foundFile);
                        }

                        //Create an empty files
                        this.adapter.setFiles(new ArrayList<RestfulFile>());
                    }

                    this.refresh();


                    //Now play the sound
                    SoundUtils.playSound(getActivity());
                    //now schedule update process
                    NetworkUtils.ScheduleSynchronization(getActivity());
                }
            }

        }catch (Exception s)
        {
            Log.e("InPatient-CheckOut",s.getMessage());
        }

    }

    @Override
    public void setFragmentListener(FragmentListener listener) {

        this.listener = listener;

    }
}
