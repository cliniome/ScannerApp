package com.wadidejla.tasks;

import android.app.Activity;
import android.app.AlertDialog;
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
    private AlertDialog dialog;

    public MarkingTask(Context context,int operationType)
    {
        this.setContext(context);
        this.setOperationType(operationType);
    }



    @Override
    public void run() {

        try
        {
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

                    syncManager.operateOnFiles(RestfulFile.READY_FILE,
                            settingsManager.getAccount());

                    //now delete all received files
                    settingsManager.getReceivedFiles().clear();


                }

            }

        }catch (Exception s)
        {
            s.printStackTrace();
        }
        finally {

            Activity currentActivity = (Activity)getContext();

            currentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    dialog.dismiss();
                }
            });
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

    public AlertDialog getDialog() {
        return dialog;
    }

    public void setDialog(AlertDialog dialog) {
        this.dialog = dialog;
    }
}
