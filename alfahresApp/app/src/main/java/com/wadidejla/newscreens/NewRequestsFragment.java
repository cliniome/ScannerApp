package com.wadidejla.newscreens;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.wadidejla.newscreens.utils.NetworkUtils;
import com.wadidejla.newscreens.utils.NewViewUtils;
import com.wadidejla.newscreens.utils.ScannerUtils;
import com.wadidejla.settings.SystemSettingsManager;
import com.wadidejla.utils.FilesUtils;
import com.wadidejla.utils.SoundUtils;
import static com.wadidejla.newscreens.utils.ScannerUtils.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import wadidejla.com.alfahresapp.R;

/**
 * Created by snouto on 08/06/15.
 */
public class NewRequestsFragment extends Fragment implements IFragment , DatePickerDialog.OnDateSetListener , IAdapterListener {


    private NewRequestsAdapter requestsAdapter;
    private ListView requestsListView;

    private FragmentListener listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.new_main_files_layout, container, false);

        this.initView(rootView);

        this.refreshLocal();

        return rootView;
    }

    private void initView(View rootView) {



        this.setRequestsListView((ListView) rootView.findViewById(R.id.mainFilesList));


        //Bind the date Object in here
        Button dateBtn = (Button)rootView.findViewById(R.id.pick_choose_date);

        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Choose Date in here

                NewRequestsFragment.this.onCreateDateDialog();


            }
        });


    }

    private void onCreateDateDialog() {

        try
        {
            //Get today's Date
            Date today = new Date();
            Calendar calc = Calendar.getInstance();
            int year = calc.get(Calendar.YEAR);
            int month = calc.get(Calendar.MONTH);
            int day = calc.get(Calendar.DAY_OF_MONTH);

            //Show the Dialog Picker
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),this,year,month,day);
            datePickerDialog.show();

        }catch (Exception s)
        {

        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

        Calendar calc = Calendar.getInstance();
        calc.set(Calendar.YEAR,year);
        calc.set(Calendar.MONTH,month);
        calc.set(Calendar.DAY_OF_MONTH,day);

        Date chosenDate = calc.getTime();

        SimpleDateFormat formatter = new SimpleDateFormat("d-MMM-yy");

        String chosenFormattedDate = formatter.format(chosenDate);

        this.doScanWithDate(chosenFormattedDate);

    }


    private void doScanWithDate(final String chosenDate)
    {
        final ProgressDialog dialog = NewViewUtils.getDeterminateDialog(getActivity());
        //show dialog
        dialog.show();

        try
        {



            final Thread newRequestsThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    final DBStorageUtils storageUtils = new DBStorageUtils(getActivity());

                    SystemSettingsManager settingsManager = SystemSettingsManager.createInstance(getActivity());
                    settingsManager.getNewRequests().clear();

                    if(storageUtils.getSettingsManager().isEmptyRequests())
                    {

                        AlfahresConnection conn = storageUtils.getSettingsManager().getConnection();

                        final HttpResponse response = conn.setAuthorization(storageUtils.getSettingsManager()
                                .getAccount().getUserName(), storageUtils.getSettingsManager()
                                .getAccount().getPassword())
                                .setMethodType(conn.GET_HTTP_METHOD)
                                .path(String.format("files/selectDate?date=%s",chosenDate))
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


                               // storageUtils.insertOrUpdateFile(file);

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

                                requestsAdapter.setListener(NewRequestsFragment.this);
                                getRequestsListView().setAdapter(requestsAdapter);
                                requestsAdapter.notifyDataSetChanged();

                                NewRequestsFragment.this.refreshLocal();

                                dialog.dismiss();

                            }catch (Exception s)
                            {
                                s.printStackTrace();
                            }
                        }
                    });





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
        String title = getResources().getString(R.string.Fragment_NewRequests_Title);

        if(this.getRequestsAdapter() != null)
        {
            title  = String.format("%s(%s)",title,this.getRequestsAdapter().getCount());
        }

        return title;
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


            //Arrange and sort them accordingly
            FilesUtils.prepareFiles(newRequests);

            NewRequestsAdapter adapter = new NewRequestsAdapter(getActivity()
                    ,R.layout.new_single_file_view,
                    newRequests);

            adapter.setListener(this);
            getRequestsListView().setAdapter(adapter);
            adapter.notifyDataSetChanged();


            //Force the fragment listener to update

            updateListener();







        }catch (Exception s)
        {
            Log.e("Error", s.getMessage());
        }
    }

    private void updateListener() {

        if(this.listener != null)
        {
            ((Activity)this.listener).setTitle(this.getTitle());
        }
    }


    @Override
    public void refresh() {

        this.refreshLocal();
       /* try
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
                                        List<RestfulFile> availableRequests = storageUtils.getNewRequests();

                                        //Prepare them and sort them accordingly
                                        FilesUtils.prepareFiles(availableRequests);

                                        //bind the newRequests to the listView
                                        NewRequestsAdapter adapter = new NewRequestsAdapter(getActivity()
                                                ,R.layout.new_single_file_view,
                                                availableRequests);
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

        }*/
    }

    @Override
    public void handleScanResults(String fileBarcode) {

        try {
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

                    SoundUtils.playSound(getActivity());

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

        }catch (Exception s)
        {
            Log.e("Error",s.getMessage());
        }

        finally {

            //Then do the synchronization in background
            NetworkUtils.ScheduleSynchronization(getActivity());
        }

    }

    @Override
    public void setFragmentListener(FragmentListener listener) {

        this.listener = listener;

    }

    public NewRequestsAdapter getRequestsAdapter() {
        return requestsAdapter;
    }

    public void setRequestsAdapter(NewRequestsAdapter requestsAdapter) {
        this.requestsAdapter = requestsAdapter;
        this.requestsAdapter.setListener(this);
    }

    public ListView getRequestsListView() {
        return requestsListView;
    }

    public void setRequestsListView(ListView requestsListView) {
        this.requestsListView = requestsListView;
    }


    @Override
    public void doUpdateFragment() {

        this.refresh();
    }
}
