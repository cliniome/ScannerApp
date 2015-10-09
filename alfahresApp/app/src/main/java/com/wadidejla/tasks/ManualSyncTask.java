package com.wadidejla.tasks;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;

import com.degla.restful.models.BooleanResult;
import com.degla.restful.models.FileModelStates;
import com.degla.restful.models.RestfulFile;
import com.degla.restful.models.SyncBatch;
import com.degla.restful.models.http.HttpResponse;
import com.wadidejla.network.AlfahresConnection;
import com.wadidejla.newscreens.IFragment;
import com.wadidejla.settings.SystemSettingsManager;
import com.wadidejla.utils.SoundUtils;

import java.util.ArrayList;
import java.util.List;

import wadidejla.com.alfahresapp.R;

/**
 * Created by snouto on 30/05/15.
 */
public class ManualSyncTask implements Runnable {


    private static final String CLASS_NAME = "SyncTask";

    private SystemSettingsManager systemSettingsManager;

    private AlfahresConnection connection;
    private Context context;

    private ProgressDialog progressDialog;

    private IFragment currentFragment;

    public ManualSyncTask(Context context)
    {
        systemSettingsManager = SystemSettingsManager.createInstance(context);
        this.context = context;

    }

    @Override
    public void run() {

        ConnectivityManager connectivityManager = (ConnectivityManager)context.
                getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager != null)
        {
            try
            {
                NetworkInfo info = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                boolean connected = info.isConnected();

                if(connected)
                {

                    try
                    {
                        //begin the synchronization process in here
                        this.beginSynchronization();
                        SoundUtils.playSound(context);

                    }catch (Exception s)
                    {
                        Log.w(CLASS_NAME, s.getMessage());

                    }
                }else
                {
                   if(context instanceof Activity)
                   {
                       ((Activity)context).runOnUiThread(new Runnable() {
                           @Override
                           public void run() {

                               SoundUtils.PlayError(context);

                               final AlertDialog dialog = new AlertDialog.Builder(context)
                                       .setTitle(R.string.SYNC_OFFLINE_TITLE)
                                       .setMessage(R.string.SYNC_DIALOG_MESSAGE)
                                       .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                           @Override
                                           public void onClick(DialogInterface dialogInterface, int i) {

                                               dialogInterface.dismiss();
                                           }
                                       }).create();

                               dialog.show();
                           }
                       });
                   }
                }

            }catch (Exception s)
            {
                Log.w(CLASS_NAME,s.getMessage());
            }
        }

        //finally dismiss the progress dialog

        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                progressDialog.dismiss();
                currentFragment.refresh();
            }
        });

    }



    private void beginSynchronization() throws Exception {

        connection = systemSettingsManager.getConnection();

        connection = connection.path("sync/now").setAuthorization(systemSettingsManager.getAccount().getUserName(),
                systemSettingsManager.getAccount().getPassword())
                .setMethodType(connection.POST_HTTP_METHOD);

        //get all the restful files
        List<RestfulFile> availableFiles = systemSettingsManager.getSyncFilesManager().getFilesDBManager()
                .getAllReadyFiles();

        if(availableFiles != null && availableFiles.size() > 0)

        {
            List<RestfulFile> readyFiles = new ArrayList<RestfulFile>();

            for(RestfulFile file : availableFiles)
            {
                if(file.isReadyForSync())
                 readyFiles.add(file);
            }

            //now add all these files to a syncBatch
            SyncBatch batch = new SyncBatch(readyFiles);

            //now add them to the connection
            HttpResponse response = connection.setBody(batch)
                    .call(SyncBatch.class);

            if(response != null)
            {
                if(HttpResponse.OK_HTTP_CODE == Integer.parseInt(response.getResponseCode()))
                {
                    if (response.getPayload() instanceof BooleanResult)
                    {

                        BooleanResult bResult = (BooleanResult)response.getPayload();
                        if(bResult.isState())
                        {
                            this.updateThoseFiles(readyFiles,null);
                        }else
                        {
                            SyncBatch regurgitatedFiles = (SyncBatch)response.getPayload();

                            this.updateThoseFiles(readyFiles,regurgitatedFiles);
                        }
                        //it means that some files may be synced and others not
                        //so only update the files that are not included within the
                        //response



                    }else
                    {
                        //it means that all files have been synced successfully
                        this.updateThoseFiles(readyFiles,null);

                    }
                }
            }



        }





    }

    private void updateThoseFiles(List<RestfulFile> readyFiles, SyncBatch regurgitatedFiles) {

        try
        {
            if(readyFiles != null && readyFiles.size() > 0)
            {
                if(regurgitatedFiles != null )
                {
                    //it means to update only files not within regurgitated Files

                    for(RestfulFile file : readyFiles)
                    {
                        if (!regurgitatedFiles.containsFile(file))
                        {
                            systemSettingsManager.getSyncFilesManager().getFilesDBManager()
                                    .deleteFile(file.getFileNumber());
                        }
                    }


                }else
                {
                    //remove all readyFiles
                    //from the local database
                    for(RestfulFile file : readyFiles)
                    {
                        systemSettingsManager.getSyncFilesManager().getFilesDBManager()
                                .deleteFile(file.getFileNumber());
                    }
                }
            }

        }catch (Exception s)
        {
            Log.w(CLASS_NAME,s.getMessage());
        }
    }


    public ProgressDialog getProgressDialog() {
        return progressDialog;
    }

    public void setProgressDialog(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }

    public IFragment getCurrentFragment() {
        return currentFragment;
    }

    public void setCurrentFragment(IFragment currentFragment) {
        this.currentFragment = currentFragment;
    }
}
