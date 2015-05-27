package com.wadidejla.utils;

import android.util.Log;

import com.degla.restful.models.RestfulFile;
import com.wadidejla.db.AlfahresDBHelper;
import com.wadidejla.db.EmployeeDBManager;
import com.wadidejla.db.FilesDBManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by snouto on 27/05/15.
 */
public class AlFahresFilesManager implements FilesManager {

    private static final String CLASS_NAME="AlFahresFilesManager";

    private String operatingTable;

    private FilesDBManager filesDBManager;

    private List<RestfulFile> files;

    private List<FilesOnChangeListener> listeners;

    public AlFahresFilesManager(AlfahresDBHelper helper,String tableName)
    {
        this.setOperatingTable(tableName);
        filesDBManager = new FilesDBManager(helper,tableName);
        listeners = new ArrayList<FilesOnChangeListener>();
    }



    @Override
    public synchronized boolean operateOnFile(String barcode) {

        try
        {

            RestfulFile foundFile = getFileWithBarcode(barcode);

            if(foundFile != null)
            {
                //add the current file into the sync_Files table
                boolean result = filesDBManager.insertFile(foundFile);

                if(result)
                {
                    //now remove it from the list
                    result = files.remove(foundFile);
                }

                //now notify all listeners
                if(result)
                {
                    for(FilesOnChangeListener listener : this.getFilesListener())
                    {
                        listener.notifyChange();
                    }
                }

                return result;

            }else return false;

        }catch (Exception s)
        {
            Log.w(CLASS_NAME,s.getMessage());
            return false;
        }
    }

    private RestfulFile getFileWithBarcode(String barcode)
    {
        RestfulFile found = null;

        for(RestfulFile file : this.getFiles())
        {
            if(file.getFileNumber().equals(barcode))
            {
                found = file;
                break;
            }
        }

        return found;
    }

    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    public String getOperatingTable() {
        return this.operatingTable;
    }

    @Override
    public List<FilesOnChangeListener> getFilesListener() {
        return this.listeners;
    }

    public void setOperatingTable(String operatingTable) {
        this.operatingTable = operatingTable;
    }

    public List<RestfulFile> getFiles() {
        return files;
    }

    public void setFiles(List<RestfulFile> files) {
        this.files = files;
    }
}