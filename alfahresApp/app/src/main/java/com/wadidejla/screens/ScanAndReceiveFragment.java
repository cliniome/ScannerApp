package com.wadidejla.screens;

import android.app.Activity;
import android.app.AlertDialog;
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
 * Created by snouto on 28/05/15.
 */
public class ScanAndReceiveFragment extends Fragment implements FilesOnChangeListener,ViewPagerSlave {


    private static final String CLASS_NAME="ScanAndReceiveFragment";

    private Activity parentActivity;
    private ListView listView;
    private FilesManager filesManager;
    private String title;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.main_files_layout,container,false);
        this.setTitle(getResources().getString(R.string.SCAN_RECEIVE_TITLE));
        setListView((ListView)rootView.findViewById(R.id.mainFilesList));
        TextView emptyView = new TextView(getActivity());
        emptyView.setText("Trolley has no Files");
        getListView().setEmptyView(emptyView);
        filesManager = SystemSettingsManager.createInstance(getActivity()).getSyncFilesManager();
        filesManager.getFilesListener().add(this);
        this.initViews(rootView);
        return rootView;
    }

    private void initViews(View rootView) {

        try
        {
          SystemSettingsManager settingsManager = SystemSettingsManager.createInstance(getActivity());

            if(settingsManager.getReceivedFiles() != null && settingsManager.getReceivedFiles().size() > 0)
            {
                List<RestfulFile> receivedFiles = SystemSettingsManager.createInstance(getActivity()).getReceivedFiles();

                if(receivedFiles == null) receivedFiles = new ArrayList<RestfulFile>();

                  //bind the listview to the received files if present
                GenericFilesAdapter adapter = ScreenRouter.getGenericKeeperArrayAdapter(getActivity(),
                        receivedFiles);

                listView.setAdapter(adapter);
            }

        }catch (Exception s)
        {
            Log.w(CLASS_NAME,s.getMessage());
        }

    }

    @Override
    public void notifyChange() {

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

                        List<RestfulFile> localFiles = settingsManager.getReceivedFiles();

                        if(localFiles == null)
                            localFiles = new ArrayList<RestfulFile>();

                        final GenericFilesAdapter adapter = ScreenRouter.getGenericKeeperArrayAdapter(getActivity(),
                                localFiles);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                listView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();

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

    public Activity getParentActivity() {
        return parentActivity;
    }

    public void setParentActivity(Activity parentActivity) {
        this.parentActivity = parentActivity;
    }

    public FilesManager getFilesManager() {
        return filesManager;
    }

    public void setFilesManager(FilesManager filesManager) {
        this.filesManager = filesManager;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ListView getListView() {
        return listView;
    }

    public void setListView(ListView listView) {
        this.listView = listView;
    }
}
