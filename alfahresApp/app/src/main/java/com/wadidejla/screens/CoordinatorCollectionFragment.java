package com.wadidejla.screens;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;

import com.degla.restful.models.CollectionBatch;
import com.degla.restful.models.RestfulClinic;
import com.degla.restful.models.RestfulFile;
import com.degla.restful.models.http.HttpResponse;
import com.wadidejla.db.AlfahresDBHelper;
import com.wadidejla.listeners.KeeperOnClickListener;
import com.wadidejla.network.AlfahresConnection;
import com.wadidejla.settings.SystemSettingsManager;
import com.wadidejla.utils.ActionItem;
import com.wadidejla.utils.AlFahresFilesManager;
import com.wadidejla.utils.FilesOnChangeListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import wadidejla.com.alfahresapp.R;

/**
 * Created by snouto on 06/06/15.
 */
public class CoordinatorCollectionFragment extends Fragment implements FilesOnChangeListener,ViewPagerSlave {


    private String title;

    private ExpandableListView expandableListView;





    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.coordinator_main_list_view,container,false);
        this.initView(rootView);
        return rootView;

    }

    private void initView(View rootView) {

        try
        {
            this.setTitle(getResources().getString(R.string.COORDINATOR_COLLECT_SCREEN));



           final ExpandableListView listView
                   = (ExpandableListView)rootView.findViewById(R.id.coordinator_list_view);

            this.setExpandableListView(listView);

            final SystemSettingsManager systemSettingsManager = SystemSettingsManager
                    .createInstance(getActivity());


            final ProgressDialog dlg = ProgressDialog.show(getActivity(),"Please Wait","Loading Files....");

            dlg.setCancelable(false);

            dlg.show();

            //Now try to get all the files for the current coordinator
            Thread collectingThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    try
                    {

                        SystemSettingsManager settingsManager = SystemSettingsManager.createInstance(getActivity());

                        if(!settingsManager.isCollectingBegun())
                        {
                            AlfahresConnection connection = systemSettingsManager.getConnection();
                            HttpResponse response = connection.path("files/collect").setMethodType(AlfahresConnection.POST_HTTP_METHOD)
                                    .setAuthorization(systemSettingsManager.getAccount().getUserName(),
                                            systemSettingsManager.getAccount().getPassword())
                                    .call(CollectionBatch.class);

                            if(response != null && Integer.parseInt(response.getResponseCode()) == HttpResponse.OK_HTTP_CODE)
                            {
                                //access the collection batch
                                CollectionBatch batch = (CollectionBatch)response.getPayload();

                                settingsManager.setCollectingBegun(true);


                                //now create the ExpandableListAdapter
                                final List<String> mainCategories = batch.getCategories();

                                final HashMap<String,List<RestfulFile>> categorizedData = batch.getCategorizedData();

                                AlFahresFilesManager filesManager = (AlFahresFilesManager) settingsManager.getSyncFilesManager();

                                List<RestfulFile> tempFiles = new ArrayList<RestfulFile>();

                                for(String currentkey : categorizedData.keySet())
                                {
                                    List<RestfulFile> availableFiles = categorizedData.get(currentkey);
                                    tempFiles.addAll(availableFiles);
                                }

                                if(tempFiles != null && tempFiles.size() > 0)
                                {
                                    filesManager.setFiles(tempFiles);
                                    filesManager.operateOnFiles(RestfulFile.NOT_READY_FILE
                                            ,settingsManager.getAccount());
                                }

                                //now create the ExpandableListAdapter
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        CoordinatorExpandableAdapter adapter= new CoordinatorExpandableAdapter(getActivity(),
                                                mainCategories,categorizedData);

                                        KeeperOnClickListener<BaseExpandableListAdapter> listener =
                                                ScreenRouter.getExpandableListener(getActivity());
                                        listener.setParentAdapter(adapter);
                                        adapter.setListener(listener);

                                        listView.setAdapter(adapter);

                                        dlg.dismiss();
                                    }
                                });
                            }else throw new Exception("There is no data available");
                        }else
                        {
                            //get all data
                            CollectionBatch batch = settingsManager.getSyncFilesManager()
                                    .getCoordinatorFiles(RestfulFile.NOT_READY_FILE
                                            ,settingsManager.getAccount());

                            if(batch != null)
                            {
                                final List<String> mainCategories = batch.getCategories();

                                final HashMap<String,List<RestfulFile>> categorizedData = batch.getCategorizedData();

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        CoordinatorExpandableAdapter adapter= new CoordinatorExpandableAdapter(getActivity(),
                                                mainCategories,categorizedData);

                                        KeeperOnClickListener<BaseExpandableListAdapter> listener =
                                                ScreenRouter.getExpandableListener(getActivity());
                                        listener.setParentAdapter(adapter);
                                        adapter.setListener(listener);

                                        listView.setAdapter(adapter);

                                        dlg.dismiss();
                                    }
                                });


                            }
                        }


                    }catch (Exception s)
                    {
                        Log.w("CoordinatorScreen",s.getMessage());
                    }
                    finally {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                dlg.dismiss();
                            }
                        });
                    }
                }
            });

            collectingThread.start();



        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }

    @Override
    public void notifyChange() {


    }

    @Override
    public void update() {



    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ExpandableListView getExpandableListView() {
        return expandableListView;
    }

    public void setExpandableListView(ExpandableListView expandableListView) {
        this.expandableListView = expandableListView;
    }
}
