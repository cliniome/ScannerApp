package com.wadidejla.newscreens.utils;

import android.content.Context;
import android.util.Log;

import com.degla.restful.models.BooleanResult;
import com.degla.restful.models.RestfulFile;
import com.degla.restful.models.SyncBatch;
import com.degla.restful.models.http.HttpResponse;
import com.wadidejla.network.AlfahresConnection;
import com.wadidejla.settings.SystemSettingsManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by snouto on 07/08/15.
 */
public class NetworkUtils {


    public static void ScheduleSynchronization(final Context context)
    {
        try
        {
            //TODO : Implement the synchronization process in here
            Runnable synchronizationProcess = new Runnable() {
                @Override
                public void run() {

                    if(ConnectivityUtils.isConnected(context))
                    {
                        beginSynchronization(context);
                    }
                }
            };


            //Begin the synchronization process now
            Thread syncThread = new Thread(synchronizationProcess);
            syncThread.start();

        }catch (Exception s)
        {
            Log.e("Error",s.getMessage());

        }
    }


    public static void beginSynchronization(Context context)
    {
        SystemSettingsManager systemSettingsManager = SystemSettingsManager.createInstance(context);
        AlfahresConnection connection = systemSettingsManager.getConnection();

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
                            updateThoseFiles(readyFiles, null,systemSettingsManager);
                        }else
                        {
                            SyncBatch regurgitatedFiles = (SyncBatch)response.getPayload();

                            updateThoseFiles(readyFiles, regurgitatedFiles,systemSettingsManager);
                        }
                        //it means that some files may be synced and others not
                        //so only update the files that are not included within the
                        //response



                    }else
                    {
                        //it means that all files have been synced successfully
                        updateThoseFiles(readyFiles, null,systemSettingsManager);

                    }
                }
            }



        }
    }

    private static void updateThoseFiles(List<RestfulFile> readyFiles, SyncBatch regurgitatedFiles , SystemSettingsManager systemSettingsManager) {

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
            Log.w("Error",s.getMessage());
        }
    }
}
