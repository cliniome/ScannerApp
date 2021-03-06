package com.wadidejla.newscreens;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.degla.restful.models.FileModelStates;
import com.degla.restful.models.RestfulFile;
import com.degla.restful.models.http.HttpResponse;
import com.google.gson.reflect.TypeToken;
import com.wadidejla.network.AlfahresConnection;
import com.wadidejla.newscreens.adapters.NewRequestsAdapter;
import com.wadidejla.newscreens.utils.ConnectivityUtils;
import com.wadidejla.newscreens.utils.DBStorageUtils;
import com.wadidejla.newscreens.utils.NewViewUtils;
import com.wadidejla.newscreens.utils.ScannerUtils;
import com.wadidejla.utils.FilesUtils;
import com.wadidejla.utils.SoundUtils;
import static com.wadidejla.newscreens.utils.ScannerUtils.*;

import java.util.ArrayList;
import java.util.List;
import wadidejla.com.alfahresapp.R;

/**
 * Created by snouto on 08/06/15.
 */
public class NewRequestsFragment extends Fragment implements IFragment {


    private NewRequestsAdapter requestsAdapter;
    private ListView requestsListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.new_main_files_layout, container, false);

        this.initView(rootView);

        return rootView;
    }

    private void initView(View rootView) {

        final ProgressDialog dialog = NewViewUtils.getDeterminateDialog(getActivity());
        //show dialog
        dialog.show();

        try
        {
            this.setRequestsListView((ListView) rootView.findViewById(R.id.mainFilesList));


            final Thread newRequestsThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    final DBStorageUtils storageUtils = new DBStorageUtils(getActivity());

                    if(storageUtils.getSettingsManager().isEmptyRequests())
                    {
                        AlfahresConnection conn = storageUtils.getSettingsManager().getConnection();

                        final HttpResponse response = conn.setAuthorization(storageUtils.getSettingsManager()
                                .getAccount().getUserName(), storageUtils.getSettingsManager()
                                .getAccount().getPassword())
                                .setMethodType(conn.GET_HTTP_METHOD)
                                .path("files/new")
                                .call(new TypeToken<List<RestfulFile>>() {
                                }.getType());

                        if(response != null && Integer.parseInt(response.getResponseCode())
                                == HttpResponse.OK_HTTP_CODE)
                        {

                            List<RestfulFile> files = (List<RestfulFile>) response.getPayload();

                            if (files == null)
                                files = new ArrayList<RestfulFile>();

                            FilesUtils.prepareFiles(files);

                            dialog.setMax(files.size());

                            int counter = 0;
                            //List<RestfulFile> tempList = new ArrayList<RestfulFile>();

                            for(RestfulFile file : files)
                            {
                                dialog.setProgress(counter);
                                //add the current employee to the current file
                                file.setEmp(storageUtils.getSettingsManager().getAccount());
                                file.setState(FileModelStates.NEW.toString());

                                /*if(storageUtils.getSettingsManager().getSyncFilesManager()
                                        .getFilesDBManager().getFileByNumber(file.getFileNumber()) == null)
                                {
                                    tempList.add(file);
                                }*/

                                storageUtils.insertOrUpdateFile(file);

                                ++counter;

                            }

                            storageUtils.setNewRequests(files);
                        }
                    }


                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            try
                            {
                                //bind the newRequests to the listView
                                requestsAdapter = new NewRequestsAdapter(getActivity(),
                                        R.layout.new_single_file_view,
                                        storageUtils.getNewRequests());
                                getRequestsListView().setAdapter(requestsAdapter);
                                requestsAdapter.notifyDataSetChanged();

                                dialog.dismiss();

                            }catch (Exception s)
                            {
                                s.printStackTrace();
                            }
                        }
                    });



                }
            });

            //Binding the actions
            // First : Bind the refresh Button
            Button refreshButton = (Button)rootView.findViewById(R.id.new_files_layout_refresh_btn);
            refreshButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    NewRequestsFragment.this.refreshLocal();
                }
            });


            //Scan button
            Button scanButton = (Button)rootView.findViewById(R.id.new_files_layout_scan_btn);

            scanButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    //Scan
                    NewRequestsFragment.this.refresh();



                }
            });

            newRequestsThread.start();

        }catch (Exception s)
        {
            s.printStackTrace();
        }

    }

    @Override
    public String getTitle() {
        return getResources().getString(R.string.Fragment_NewRequests_Title);
    }

    @Override
    public void chainUpdate() {

        if(this.getRequestsAdapter() != null)
            this.getRequestsAdapter().notifyDataSetChanged();

    }


    public void refreshLocal()
    {
        try
        {

            DBStorageUtils storageUtils = new DBStorageUtils(getActivity());

            List<RestfulFile> newRequests = storageUtils.getNewRequests();

            if(newRequests == null)
                newRequests = new ArrayList<RestfulFile>();


            NewRequestsAdapter adapter = new NewRequestsAdapter(getActivity()
                    ,R.layout.new_single_file_view,
                    storageUtils.getNewRequests());
            getRequestsListView().setAdapter(adapter);
            adapter.notifyDataSetChanged();

            SoundUtils.playSound(getActivity());


        }catch (Exception s)
        {
            Log.e("Error", s.getMessage());
        }
    }

    @Override
    public void refresh() {
        try
        {
            if(ConnectivityUtils.isConnected(getActivity()))
            {
                final ProgressDialog dialog = NewViewUtils.getDeterminateDialog(getActivity());
                //show dialog
                dialog.show();

                final Thread newRequestsThread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try
                        {
                            final DBStorageUtils storageUtils = new DBStorageUtils(getActivity());


                            AlfahresConnection conn = storageUtils.getSettingsManager().getConnection();

                            final HttpResponse response = conn.setAuthorization(storageUtils.getSettingsManager()
                                    .getAccount().getUserName(), storageUtils.getSettingsManager()
                                    .getAccount().getPassword())
                                    .setMethodType(conn.GET_HTTP_METHOD)
                                    .path("files/new")
                                    .call(new TypeToken<List<RestfulFile>>() {
                                    }.getType());

                            if(response != null && Integer.parseInt(response.getResponseCode())
                                    == HttpResponse.OK_HTTP_CODE)
                            {

                                List<RestfulFile> files = (List<RestfulFile>) response.getPayload();

                                if (files == null)
                                    files = new ArrayList<RestfulFile>();

                                FilesUtils.prepareFiles(files);

                               // List<RestfulFile> tempList = new ArrayList<RestfulFile>();

                                dialog.setMax(files.size());
                                int counter = 0;

                                for(RestfulFile file : files)
                                {

                                    //add the current employee to the current file
                                    file.setEmp(storageUtils.getSettingsManager().getAccount());
                                    file.setState(FileModelStates.NEW.toString());

                                    storageUtils.insertOrUpdateFile(file);

                                    dialog.setProgress(counter);

                                    ++counter;
                                }

                                storageUtils.addToNewRequests(files);
                            }



                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    try
                                    {
                                        //bind the newRequests to the listView
                                        NewRequestsAdapter adapter = new NewRequestsAdapter(getActivity()
                                                ,R.layout.new_single_file_view,
                                                storageUtils.getNewRequests());
                                        getRequestsListView().setAdapter(adapter);
                                        adapter.notifyDataSetChanged();

                                        dialog.dismiss();

                                    }catch (Exception s)
                                    {
                                        s.printStackTrace();
                                    }
                                }
                            });

                        }catch (Exception s)
                        {
                            Log.e("Error",s.getMessage());
                        }

                        finally {

                            if(dialog.isShowing())
                                dialog.dismiss();
                        }



                    }
                });

                newRequestsThread.start();
            }

        }catch (Exception s)
        {
            s.printStackTrace();

        }
    }

    @Override
    public void handleScanResults(String fileBarcode) {

        if(fileBarcode != null)
        {
            //Get the Restful file
            DBStorageUtils storageUtils = new DBStorageUtils(getActivity());
            //Get the restful File
            RestfulFile foundFile = storageUtils.getRestfulRequestByBarcode(fileBarcode);

            if(foundFile != null)
            {
                if(foundFile.isInpatient())
                {
                    //Now check_OUT the current file
                    storageUtils.operateOnFile(foundFile, FileModelStates.CHECKED_OUT.toString()
                            , RestfulFile.READY_FILE);

                }else
                {
                    //Now check_OUT the current file
                    storageUtils.operateOnFile(foundFile, FileModelStates.OUT_OF_CABIN.toString()
                            , RestfulFile.READY_FILE);
                }

                //Now refresh the current fragment
                NewRequestsFragment.this.refreshLocal();

            }else
            {
                Toast.makeText(getActivity(),String.format("File %s is not found ",fileBarcode)
                        ,Toast.LENGTH_SHORT)
                        .show();

            }

        }else
        {
            Toast.makeText(getActivity(),"Barcode Is empty",Toast.LENGTH_SHORT)
                    .show();
        }

    }

    public NewRequestsAdapter getRequestsAdapter() {
        return requestsAdapter;
    }

    public void setRequestsAdapter(NewRequestsAdapter requestsAdapter) {
        this.requestsAdapter = requestsAdapter;
    }

    public ListView getRequestsListView() {
        return requestsListView;
    }

    public void setRequestsListView(ListView requestsListView) {
        this.requestsListView = requestsListView;
    }
}
