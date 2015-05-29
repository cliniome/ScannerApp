package com.wadidejla.tasks;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import com.degla.restful.models.BooleanResult;
import com.degla.restful.models.SyncBatch;
import com.degla.restful.models.http.HttpResponse;
import com.wadidejla.network.AlfahresConnection;
import com.wadidejla.screens.GenericFilesAdapter;
import com.wadidejla.screens.ScanAndReceiveFragment;
import com.wadidejla.screens.ScreenRouter;
import com.wadidejla.settings.SystemSettingsManager;

/**
 * Created by snouto on 29/05/15.
 */
public class ScanAndReceiveTask implements Runnable {


    private Context context;
    private String barcode;
    private ScanAndReceiveFragment fragment;
    private AlertDialog dialog;

    public ScanAndReceiveTask(Context cont,String barcode
            ,ScanAndReceiveFragment fragment)
    {
        this.setBarcode(barcode);
        this.setContext(cont);
        this.setFragment(fragment);
    }



    @Override
    public void run() {

        try
        {
            final SystemSettingsManager settingsManager = SystemSettingsManager.createInstance(context);

            AlfahresConnection connection = settingsManager.getConnection();

            HttpResponse response = connection.path(String.format("files/scan/%s", barcode)).setAuthorization(settingsManager.getAccount()
            .getUserName(),settingsManager.getAccount().getPassword())
                    .setMethodType(AlfahresConnection.GET_HTTP_METHOD)
                    .call(SyncBatch.class);

            if(response != null)
            {
                if(Integer.parseInt(response.getResponseCode()) == HttpResponse.OK_HTTP_CODE)
                {
                    if(response.getPayload() instanceof SyncBatch)
                    {
                        //get the syncBatch
                        final SyncBatch batch = (SyncBatch)response.getPayload();


                        //now bind that to the list view
                        Activity currentActivity = (Activity)context;

                        currentActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                /*//create a files Array Adapter
                                FilesArrayAdapter adapter = new FilesArrayAdapter(context, R.layout.single_file_view,batch.getFiles());

                                fragment.getListView().setAdapter(adapter);
                                adapter.notifyDataSetChanged();*/
                                settingsManager.setReceivedFiles(batch.getFiles());
                                //Create the Keeper Listener
                                GenericFilesAdapter adapter = ScreenRouter.getGenericKeeperArrayAdapter(context
                                        ,settingsManager.getReceivedFiles());

                                fragment.getListView().setAdapter(adapter);
                                adapter.notifyDataSetChanged();



                            }
                        });


                    }else if (response.getPayload() instanceof BooleanResult)
                    {

                    }
                }
            }

        }catch (Exception s)
        {
            Log.w("ScanAndReceiveTask",s.getMessage());
        }

        finally {

            dialog.dismiss();
        }

    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public ScanAndReceiveFragment getFragment() {
        return fragment;
    }

    public void setFragment(ScanAndReceiveFragment fragment) {
        this.fragment = fragment;
    }

    public AlertDialog getDialog() {
        return dialog;
    }

    public void setDialog(AlertDialog dialog) {
        this.dialog = dialog;
    }
}
