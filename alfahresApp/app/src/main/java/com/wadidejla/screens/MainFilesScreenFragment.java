package com.wadidejla.screens;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.degla.restful.models.RestfulFile;
import com.degla.restful.models.http.HttpResponse;
import com.google.gson.reflect.TypeToken;
import com.wadidejla.network.AlfahresConnection;
import com.wadidejla.settings.SystemSettingsManager;

import java.util.ArrayList;
import java.util.List;

import wadidejla.com.alfahresapp.R;

/**
 * Created by snouto on 23/05/15.
 */

public class MainFilesScreenFragment extends Fragment
{

    private Activity parentActivity;
    private ListView listView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.main_files_layout,container,false);
        getActivity().setTitle(R.string.title_section1);
        listView = (ListView)rootView.findViewById(R.id.mainFilesList);
        this.initViews(rootView);
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void initViews(final View rootView)
    {
        try
        {
            //get the available Restful Files for that particular user
            final ProgressDialog dlg = ProgressDialog.show(getActivity(),"Please Wait","Loading Files....");

            dlg.setCancelable(false);

            if(SystemSettingsManager.createInstance(getActivity()).getAvailableFiles() == null)
            {
                Thread loadingThread = new Thread(new Runnable() {
                    @Override
                    public void run() {


                        SystemSettingsManager manager = SystemSettingsManager
                                .createInstance(getActivity());

                        AlfahresConnection conn = manager.getConnection();

                        final HttpResponse response = conn.setAuthorization(manager.getAccount().getUserName(), manager.getAccount().getPassword())
                                .setMethodType(conn.GET_HTTP_METHOD)
                                .path("files/new")
                                .call(new TypeToken<List<RestfulFile>>() {
                                }.getType());


                        if(Integer.parseInt(response.getResponseCode()) == HttpResponse.OK_HTTP_CODE)
                        {


                            //finally disable the dialog
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    try {


                                        List<RestfulFile> files = (List<RestfulFile>) response.getPayload();

                                        if (files == null)
                                            files = new ArrayList<RestfulFile>();

                                        SystemSettingsManager.createInstance(getActivity()).setAvailableFiles(files);
                                        FilesArrayAdapter filesArrayAdapter = new FilesArrayAdapter(getActivity()
                                                , R.layout.single_file_view, files);

                                        listView.setAdapter(filesArrayAdapter);
                                        filesArrayAdapter.notifyDataSetChanged();


                                        dlg.dismiss();

                                    } catch (Exception e) {
                                        Log.e("MainFilesScreenFragment", e.getMessage());
                                    }
                                }
                            });





                        }



                    }
                });

                loadingThread.start();

            }else
            {
                List<RestfulFile> files = SystemSettingsManager.createInstance(getActivity()).getAvailableFiles();

                if (files == null)
                    files = new ArrayList<RestfulFile>();

                FilesArrayAdapter filesArrayAdapter = new FilesArrayAdapter(getActivity()
                        , R.layout.single_file_view, files);

                listView.setAdapter(filesArrayAdapter);
                filesArrayAdapter.notifyDataSetChanged();
                dlg.dismiss();
            }


        }catch (Exception s)
        {
            Log.e("MainFilesScreenFragment", s.getMessage());
        }
    }


    public Activity getParentActivity() {
        return parentActivity;
    }

    public void setParentActivity(Activity parentActivity) {
        this.parentActivity = parentActivity;
    }
}
