package com.wadidejla.screens;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.degla.restful.models.RestfulFile;
import com.wadidejla.db.FilesDBManager;
import com.wadidejla.settings.SystemSettingsManager;
import com.wadidejla.utils.FilesManager;
import com.wadidejla.utils.FilesOnChangeListener;

import java.util.ArrayList;
import java.util.List;

import wadidejla.com.alfahresapp.R;

/**
 * Created by snouto on 27/05/15.
 */
public class LocalSyncFilesFragment extends Fragment implements FilesOnChangeListener,
 ViewPagerSlave{

    private Activity parentActivity;
    private ListView listView;
    private FilesManager filesManager;
    private String title;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.main_files_layout,container,false);
       /* getActivity().setTitle(R.string.title_section2);*/
        this.setTitle(getResources().getString(R.string.title_section2));
        listView = (ListView)rootView.findViewById(R.id.mainFilesList);
        TextView emptyView = new TextView(getActivity());
        emptyView.setText("No Sync Files for the moment");
        listView.setEmptyView(emptyView);
        filesManager = SystemSettingsManager.createInstance(getActivity()).getSyncFilesManager();
        filesManager.getFilesListener().add(this);
        this.initViews(rootView);
        return rootView;

    }

    private void initViews(View rootView) {

        //get the available Restful Files for that particular user
        final ProgressDialog dlg = ProgressDialog.show(getActivity(),"Please Wait","Loading Files....");

        dlg.setCancelable(false);

        //access all Local Files
        FilesDBManager filesDBManager = filesManager.getFilesDBManager();
        SystemSettingsManager settingsManager = SystemSettingsManager.createInstance(getActivity());

        List<RestfulFile> localFiles = filesDBManager.getAllFilesForEmployee(String.
                valueOf(settingsManager.getAccount().getId()));

        if(localFiles != null)
        {
            //bind it now to the list View
            //through a separate files Adapter
            SyncFilesArrayAdapter adapter = new SyncFilesArrayAdapter(getActivity(),R.layout.single_file_view,localFiles);

            listView.setAdapter(adapter);

        }

        dlg.dismiss();



    }

    @Override
    public void notifyChange() {



    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void onResume() {
        super.onResume();

        SyncFilesArrayAdapter syncAdapter = (SyncFilesArrayAdapter)listView.getAdapter();

        syncAdapter.notifyDataSetChanged();
    }

    @Override
    public void update() {

        try
        {
            final AlertDialog dlg = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.main_loading_title)
                    .setMessage(R.string.main_files_alertDlg_Title)
                    .create();

            dlg.show();

            Thread backGroundThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    try
                    {
                        FilesDBManager filesDBManager = filesManager.getFilesDBManager();
                        SystemSettingsManager settingsManager = SystemSettingsManager.createInstance(getActivity());

                        List<RestfulFile> localFiles = filesDBManager.getAllFilesForEmployee(String.
                                valueOf(settingsManager.getAccount().getId()));

                        if(localFiles == null)
                            localFiles = new ArrayList<RestfulFile>();

                        final SyncFilesArrayAdapter filesArrayAdapter = new SyncFilesArrayAdapter(getActivity()
                                ,R.layout.single_file_view,localFiles);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                listView.setAdapter(filesArrayAdapter);
                                filesArrayAdapter.notifyDataSetChanged();

                                dlg.dismiss();
                            }
                        });

                    }catch (Exception s)
                    {
                        Log.w("LocalSyncFilesFragment",s.getMessage());
                    }
                }
            });

            backGroundThread.start();
            //access all Local Files


        }catch (Exception s)
        {
            Log.w("LocalSyncFilesFragment",s.getMessage());
        }

    }
}
