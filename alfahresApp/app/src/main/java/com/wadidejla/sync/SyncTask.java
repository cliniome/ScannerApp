package com.wadidejla.sync;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.degla.restful.models.BooleanResult;
import com.degla.restful.models.FileModelStates;
import com.degla.restful.models.RestfulFile;
import com.degla.restful.models.SyncBatch;
import com.degla.restful.models.http.HttpResponse;
import com.wadidejla.network.AlfahresConnection;
import com.wadidejla.settings.SystemSettingsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

/**
 * Created by snouto on 27/05/15.
 */
public class SyncTask implements Runnable {

    private static final String CLASS_NAME = "SyncTask";

    private SystemSettingsManager systemSettingsManager;
    
    private AlfahresConnection connection;
    private Context context;

    public SyncTask(Context context)
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

               while(connected)
               {

                   try
                   {
                       //begin the synchronization process in here
                       this.beginSynchronization();

                   }catch (Exception s)
                   {
                       Log.w(CLASS_NAME,s.getMessage());

                   }
                   finally {

                       long waitingMillis =  5000;

                       try {
                           Thread.sleep(waitingMillis);
                       } catch (InterruptedException e) {
                           e.printStackTrace();
                       }

                       //check the internet

                       info = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                       connected = info.isConnected();

                   }


               }

           }catch (Exception s)
           {
               Log.w(CLASS_NAME,s.getMessage());
           }
        }

    }

    private void beginSynchronization() throws Exception {

       connection = systemSettingsManager.getConnection();

        connection = connection.path("sync/now").setAuthorization(systemSettingsManager.getAccount().getUserName(),
                systemSettingsManager.getAccount().getPassword())
                .setMethodType(connection.POST_HTTP_METHOD);

        //get all the restful files
        List<RestfulFile> availableFiles = systemSettingsManager.getSyncFilesManager().getFilesDBManager()
                .getAllFiles();

        if(availableFiles != null && availableFiles.size() > 0)

        {
            List<RestfulFile> readyFiles = new ArrayList<RestfulFile>();

            for(RestfulFile file : availableFiles)
            {
                if(file.getState() != null &&
                        file.getState().equals(FileModelStates.MISSING.toString()))
                {
                    readyFiles.add(file);
                    continue;
                }

                if(file.getState() !=null && !file.getState().equals(FileModelStates.MISSING.toString()))
                {
                    //it means that this file state is something else
                    //check to see if that file contains emp ID , username and
                    //the most important is a temporary Cabin ID
                    if(file.getEmp() != null && file.getEmp().getUserName() != null &&
                            (file.getEmp().getId() != 0 && file.getEmp().getId() != -1) &&
                            file.getTemporaryCabinetId() != null && file.getTemporaryCabinetId().length() > 0)
                    {
                        //it means that this file is ready
                        readyFiles.add(file);
                        continue;

                    }
                }
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


}
