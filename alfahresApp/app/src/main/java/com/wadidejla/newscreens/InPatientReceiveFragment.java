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
import com.wadidejla.newscreens.adapters.InPatientReceiveAdapter;
import com.wadidejla.newscreens.utils.BarcodeUtils;
import com.wadidejla.newscreens.utils.NewViewUtils;
import com.wadidejla.settings.SystemSettingsManager;
import com.wadidejla.utils.SoundUtils;

import java.util.Date;
import java.util.List;

import wadidejla.com.alfahresapp.R;

/**
 * Created by snouto on 06/10/15.
 */
public class InPatientReceiveFragment extends Fragment implements IFragment {


    private ListView listView;
    private InPatientReceiveAdapter adapter;
    private FragmentListener listener;

    private SystemSettingsManager settingsManager;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.inpatient_receive_layout,container,false);

        this.initView(rootView);

        return rootView;
    }

    private void initView(View rootView) {

        try
        {
            this.listView = (ListView)rootView.findViewById(R.id.mainFilesList);
            this.adapter = new InPatientReceiveAdapter(getActivity(),R.layout.new_single_file_view);
            this.listView.setAdapter(adapter);
            settingsManager = SystemSettingsManager.createInstance(getActivity());

        }catch (Exception s)
        {
            Log.e("Inpatient-Receive",s.getMessage());
        }
    }

    @Override
    public String getTitle() {

        String title = getResources().getString(R.string.INPATIENT_RECEIVE_TITLE);
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

    @Override
    public void handleScanResults(final String barcode) {

        try
        {
            //that means the file is a medical file and not a trolley for example
            final AlertDialog waitingDialog = NewViewUtils.getWaitingDialog(getActivity());
            waitingDialog.show();

            BarcodeUtils utils = new BarcodeUtils(barcode);

            if(utils.isMedicalFile()) {

                Runnable runnable = getRunnableThread(barcode,waitingDialog);

                Thread networkThread = new Thread(runnable);
                networkThread.start();

            }else if (utils.isTrolley())
            {
                waitingDialog.dismiss();
                return;
            }else
            {
                waitingDialog.dismiss();
                //Do the rest of storing the current file with a state of "Temporary Stored"
            }



        }catch (Exception s)
        {
            Log.e("Inpatient-Receive",s.getMessage());
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

                                    //Add it to the adapter
                                   RestfulFile existingFile =  InPatientReceiveFragment.this.adapter.getFileWithNumber(foundFile.getFileNumber());

                                    if(existingFile != null)
                                    {
                                        //so receive it in here
                                        foundFile.setReadyFile(RestfulFile.READY_FILE);
                                        foundFile.setDeviceOperationDate(new Date().getTime());
                                        foundFile.setEmp(settingsManager.getAccount());
                                        foundFile.setState(InPatientReceiveFragment.this.getState(foundFile.getState()));

                                        //now save it
                                        settingsManager.getFilesManager().getFilesDBManager().insertFile(foundFile);

                                        InPatientReceiveFragment.this.adapter.getFiles().remove(foundFile);

                                    }else
                                    {
                                        //add it
                                        InPatientReceiveFragment.this.adapter.addFile(foundFile);
                                    }



                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            //add it to the adapter in here
                                            //Play the sound
                                            SoundUtils.playSound(getActivity());
                                            InPatientReceiveFragment.this.refresh();
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
    }

    private String getState(String currentState) {

        if(settingsManager.getAccount().getRole().equalsIgnoreCase(FileModelStates.ANALYSIS_COORDINATOR.toString()))
            return FileModelStates.ANALYSIS_COORDINATOR.toString();
        else if (settingsManager.getAccount().getRole().equalsIgnoreCase(FileModelStates.CODING_COORDINATOR.toString()))
            return FileModelStates.CODING_COORDINATOR.toString();
        else if (settingsManager.getAccount().getRole().equalsIgnoreCase(FileModelStates.PROCESSING_COORDINATOR.toString()))
            return FileModelStates.PROCESSING_COORDINATOR.toString();
        else if (settingsManager.getAccount().getRole().equalsIgnoreCase(FileModelStates.INCOMPLETE_COORDINATOR.toString()))
            return FileModelStates.INCOMPLETE_COORDINATOR.toString();

        return currentState;
    }

    @Override
    public void setFragmentListener(FragmentListener listener) {

        this.listener = listener;

    }
}
