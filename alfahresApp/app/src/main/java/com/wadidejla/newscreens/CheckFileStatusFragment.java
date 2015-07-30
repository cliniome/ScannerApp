package com.wadidejla.newscreens;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.degla.restful.models.FileBatchDetails;
import com.degla.restful.models.RestfulFile;
import com.degla.restful.models.SyncBatch;
import com.degla.restful.models.http.HttpResponse;
import com.wadidejla.network.AlfahresConnection;
import com.wadidejla.newscreens.adapters.CheckFileStatusArrayAdapter;
import com.wadidejla.newscreens.utils.BarcodeUtils;
import com.wadidejla.settings.SystemSettingsManager;

import java.util.ArrayList;
import java.util.List;

import wadidejla.com.alfahresapp.R;

/**
 * Created by snouto on 28/07/15.
 */
public class CheckFileStatusFragment extends Fragment implements IFragment {

    private ListView listView;

    private CheckFileStatusArrayAdapter adapter;

    private List<RestfulFile> availableFiles;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout_view = inflater.inflate(R.layout.check_file_status_layout,container,false);


        try
        {
            this.initView(layout_view);

        }catch (Exception s)
        {
            Log.e("error",s.getMessage());

        }

        return layout_view;
    }

    private void initView(View layout_view) {

        try
        {
            //get the list view
            listView = (ListView)layout_view.findViewById(R.id.mainFilesList);
            adapter = new CheckFileStatusArrayAdapter(getActivity(),R.layout.single_file_status);
            listView.setAdapter(adapter);
            setAvailableFiles(new ArrayList<RestfulFile>());


            Button btnClear = (Button)layout_view.findViewById(R.id.new_files_layout_refresh_btn);

            btnClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //Clear all the files
                    CheckFileStatusFragment.this
                            .getAvailableFiles().clear();

                    //then refresh
                    CheckFileStatusFragment.this.refresh();
                }
            });


        }catch (Exception s)
        {
            Log.e("Error",s.getMessage());
        }
    }

    @Override
    public String getTitle() {
        return getResources().getString(R.string.CHECK_FILE_STATUS_TITLE);
    }

    @Override
    public void chainUpdate() {

    }


    public void addFiles(RestfulFile file,List<RestfulFile> collection)
    {
        if(this.getAvailableFiles()  == null || this.getAvailableFiles().isEmpty()) {
            this.setAvailableFiles(new ArrayList<RestfulFile>());
            this.getAvailableFiles().add(file);
        }
        else
        {
            if(file != null)
            {
                if(!collection.contains(file))
                    collection.add(file);
            }
        }
    }




    @Override
    public void refresh() {

        try {

            adapter = new CheckFileStatusArrayAdapter(getActivity(),R.layout.single_file_status);
            adapter.setFiles(this.getAvailableFiles());
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        }catch (Exception s)
        {
            Log.e("Error",s.getMessage());
        }

    }

    @Override
    public void handleScanResults(final String barcode) {

        try
        {
            //get the barcode
            BarcodeUtils utils = new BarcodeUtils(barcode);

            if(utils.isMedicalFile())
            {
                //if that barcode is truely a medical file barcode
                Runnable networkTask = new Runnable() {
                    @Override
                    public void run() {

                        try
                        {
                            SystemSettingsManager settingsManager = SystemSettingsManager.createInstance(getActivity());

                            AlfahresConnection connection = settingsManager.getConnection();
                            HttpResponse response = connection.setAuthorization(settingsManager.getAccount())
                                    .setMethodType(AlfahresConnection.GET_HTTP_METHOD)
                                    .path(String.format("files/fileDetails?fileNumber=%s",barcode))
                                    .call(FileBatchDetails.class);

                            if(response != null && Integer.parseInt(response.getResponseCode()) ==
                                    HttpResponse.OK_HTTP_CODE)
                            {
                                //That means everything is ok
                                //get the syncBatch
                                FileBatchDetails batch = (FileBatchDetails)response.getPayload();

                                if(batch != null)
                                {
                                    //get the files
                                    final RestfulFile file = batch.getFile();
                                    file.setLastEmployeeName(batch.getEmployeeName());
                                    //set the files
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            CheckFileStatusFragment.this.addFiles(file,
                                                    CheckFileStatusFragment.this.getAvailableFiles());
                                            //refresh them
                                            CheckFileStatusFragment.this.refresh();
                                        }
                                    });
                                }
                            }

                        }catch (Exception s)
                        {
                            Log.e("Error",s.getMessage());
                        }
                    }
                };

                //Start that task
                Thread networkThread = new Thread(networkTask);
                networkThread.start();
            }

        }catch (Exception s)
        {
            Log.e("Error",s.getMessage());
        }

    }

    public List<RestfulFile> getAvailableFiles() {
        return availableFiles;
    }

    public void setAvailableFiles(List<RestfulFile> availableFiles) {
        this.availableFiles = availableFiles;
    }
}
