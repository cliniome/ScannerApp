package com.wadidejla.newscreens;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.wadidejla.newscreens.adapters.InPatientCompleteAdapter;
import com.wadidejla.newscreens.utils.BarcodeUtils;
import com.wadidejla.newscreens.utils.NetworkUtils;
import com.wadidejla.newscreens.utils.NewViewUtils;
import com.wadidejla.settings.SystemSettingsManager;
import com.wadidejla.utils.SoundUtils;

import java.util.Date;
import java.util.List;

import wadidejla.com.alfahresapp.R;

/**
 * This fragment will be used to send the files to medical record and mark the file as completed
 * Created by snouto on 08/10/15.
 */
public class InPatientCompleteFilesFragment extends Fragment implements IFragment
{
    private InPatientCompleteAdapter adapter;
    private ListView listView;
    private FragmentListener listener;
    private SystemSettingsManager settingsManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = (View)inflater.inflate(R.layout.inpatient_storage_screen_layout,container,false);
        this.initView(rootView);
        return rootView;
    }

    private void initView(View rootView) {


        this.listView = (ListView)rootView.findViewById(R.id.mainFilesList);
        this.adapter = new InPatientCompleteAdapter(getActivity(),R.layout.new_single_file_view);
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

        if(barcode == null || barcode.isEmpty()) return;

        BarcodeUtils utils = new BarcodeUtils(barcode);

        final AlertDialog waitingDialog = NewViewUtils.getWaitingDialog(getActivity());
        waitingDialog.show();

        if(utils.isMedicalFile())
        {
            Runnable runnable = getRunnableThread(barcode,waitingDialog);
            Thread networkThread = new Thread(runnable);
            networkThread.start();
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

                                    //Add it to the adapter
                                    RestfulFile existingFile =  InPatientCompleteFilesFragment.this.adapter.getFileWithNumber(foundFile.getFileNumber());

                                    if(existingFile != null)
                                    {
                                        //so receive it in here
                                        foundFile.setReadyFile(RestfulFile.READY_FILE);
                                        foundFile.setDeviceOperationDate(new Date().getTime());
                                        foundFile.setEmp(settingsManager.getAccount());
                                        foundFile.setState(FileModelStates.INPATIENT_COMPLETED.toString());

                                        //now save it
                                        settingsManager.getFilesManager().getFilesDBManager().insertFile(foundFile);

                                        InPatientCompleteFilesFragment.this.adapter.getFiles().remove(foundFile);



                                    }else
                                    {
                                        //add it
                                        InPatientCompleteFilesFragment.this.adapter.addFile(foundFile);
                                    }



                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            //add it to the adapter in here
                                            //Play the sound
                                            SoundUtils.playSound(getActivity());
                                            InPatientCompleteFilesFragment.this.refresh();

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
