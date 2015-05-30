package com.wadidejla.tasks;

import android.content.Context;

import com.degla.restful.models.RestfulFile;
import com.wadidejla.settings.SystemSettingsManager;
import com.wadidejla.utils.AlFahresFilesManager;
import com.wadidejla.utils.EmployeeUtils;

import java.util.List;

/**
 * Created by snouto on 30/05/15.
 */
public class MarkingTask implements Runnable {


    private Context context;
    private int operationType;

    public MarkingTask(Context context,int operationType)
    {
        this.setContext(context);
        this.setOperationType(operationType);
    }



    @Override
    public void run() {

        SystemSettingsManager settingsManager = SystemSettingsManager.createInstance(getContext());

        List<RestfulFile> receivedFiles = settingsManager.getReceivedFiles();

        if(receivedFiles != null && receivedFiles.size() > 0)
        {
            String state = EmployeeUtils.getStatesForFiles(settingsManager.getAccount(),
                    this.getOperationType());

            for(RestfulFile file : receivedFiles)
            {
                file.setState(state);
            }

            AlFahresFilesManager syncManager = (AlFahresFilesManager) settingsManager
                    .getReceivedSyncFilesManager();

            if(syncManager != null)
            {
                syncManager.setFiles(receivedFiles);

                syncManager.operateOnFiles();

                //now delete all received files
                settingsManager.getReceivedFiles().clear();


            }

        }

    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public int getOperationType() {
        return operationType;
    }

    public void setOperationType(int operationType) {
        this.operationType = operationType;
    }
}
