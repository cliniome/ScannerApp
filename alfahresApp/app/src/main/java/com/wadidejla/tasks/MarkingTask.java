package com.wadidejla.tasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import com.degla.restful.models.RestfulFile;
import com.wadidejla.newscreens.IFragment;
import com.wadidejla.settings.SystemSettingsManager;
import com.wadidejla.utils.AlFahresFilesManager;
import com.wadidejla.utils.EmployeeUtils;
import com.wadidejla.utils.SoundUtils;

import java.util.List;

/**
 * Created by snouto on 30/05/15.
 */
public class MarkingTask implements Runnable {


    private Context context;
    private int operationType;
    private ProgressDialog dialog;
    private IFragment fragment;

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


                for(RestfulFile file : receivedFiles)
                {
                    String state = EmployeeUtils.getStatesForFiles(file,settingsManager.getAccount(),
                            this.getOperationType());
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



            SoundUtils.playSound(getContext());

            Activity currentActivity = (Activity)getContext();

            currentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if(MarkingTask.this.getFragment() != null)
                        MarkingTask.this.getFragment().refresh();

                    getDialog().dismiss();
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


    public ProgressDialog getDialog() {
        return dialog;
    }

    public void setDialog(ProgressDialog dialog) {
        this.dialog = dialog;
    }

    public IFragment getFragment() {
        return fragment;
    }

    public void setFragment(IFragment fragment) {
        this.fragment = fragment;
    }
}
