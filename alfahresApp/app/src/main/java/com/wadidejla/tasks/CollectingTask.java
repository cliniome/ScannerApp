package com.wadidejla.tasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import com.degla.restful.models.RestfulFile;
import com.wadidejla.newscreens.IFragment;
import com.wadidejla.newscreens.utils.DBStorageUtils;
import com.wadidejla.settings.SystemSettingsManager;
import com.wadidejla.utils.AlFahresFilesManager;
import com.wadidejla.utils.EmployeeUtils;
import com.wadidejla.utils.SoundUtils;

import java.util.List;

/**
 * Created by snouto on 09/06/15.
 */
public class CollectingTask implements Runnable {


    private Context context;
    private int operationType;
    private ProgressDialog dialog;
    private IFragment fragment;

    public CollectingTask(Context context,int operationType)
    {
        this.setContext(context);
        this.setOperationType(operationType);
    }



    @Override
    public void run() {

        try
        {
            DBStorageUtils storageUtils = new DBStorageUtils(getContext());

            List<RestfulFile> collectedFiles = storageUtils.getFilesReadyForCollection();

            if(collectedFiles != null && collectedFiles.size() > 0)
            {


                for(RestfulFile file : collectedFiles)
                {
                    String state = EmployeeUtils.getStatesForFiles(file, storageUtils.getSettingsManager()
                                    .getAccount(),
                            this.getOperationType());
                    file.setState(state);
                    //remove the temporary cabinet
                    file.setTemporaryCabinetId("");
                    file.setReadyFile(RestfulFile.READY_FILE);

                    storageUtils.getSettingsManager().getFilesManager().getFilesDBManager()
                            .insertFile(file);
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

                    if(CollectingTask.this.getFragment() != null)
                        CollectingTask.this.getFragment().refresh();

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
