package com.wadidejla.newscreens.utils;

import android.content.Context;

import com.degla.restful.models.FileModelStates;
import com.degla.restful.models.RestfulFile;
import com.wadidejla.db.AlfahresDBHelper;
import com.wadidejla.db.FilesDBManager;
import com.wadidejla.settings.SystemSettingsManager;
import com.wadidejla.utils.AlFahresFilesManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by snouto on 08/06/15.
 */
public class DBStorageUtils {

    private SystemSettingsManager settingsManager;

    private Context context;

    public DBStorageUtils(Context context)
    {
        this.context = context;
        setSettingsManager(SystemSettingsManager.createInstance(context));
    }

    public void setNewRequests(List<RestfulFile> files)
    {
        getSettingsManager().setNewRequests(files);

    }


    public void saveDistributedFiles(List<RestfulFile> distributeFiles)
    {
        if(distributeFiles != null)
        {
            for(RestfulFile file : distributeFiles)
            {
                file.setEmp(settingsManager.getAccount());
                //now save it
                FilesDBManager filesDBManager = settingsManager.getFilesManager().getFilesDBManager();
                if(filesDBManager.getFileByEmployeeAndNumber(settingsManager.getAccount().getUserName(),
                        file.getFileNumber()) == null)
                {
                    //so insert it
                    filesDBManager.insertFile(file);
                }
                //otherwise the file already exists, don't get it again.
            }
        }
    }

    public void insertOrUpdateFile(RestfulFile file)
    {
        settingsManager.getFilesManager().getFilesDBManager()
                .insertFile(file);
    }

    public void addToNewRequests(List<RestfulFile> files)
    {
        getSettingsManager().addToNewRequests(files);
    }


    public List<RestfulFile> getAllReadyFilesForCurrentEmployee()
    {
        try
        {
            List<RestfulFile> availableFiles = settingsManager.getSyncFilesManager().getFilesDBManager()
                    .getAllReadyFilesForEmployee(settingsManager.getAccount().getUserName());

            if(availableFiles == null) return new ArrayList<RestfulFile>();

            return availableFiles;

        }catch (Exception s)
        {
            s.printStackTrace();
            return new ArrayList<RestfulFile>();
        }
    }


    public void setReceivedFiles(List<RestfulFile> receivedFiles)
    {
        settingsManager.setReceivedFiles(receivedFiles);
    }

    public List<RestfulFile> getReceivedFiles()
    {
        List<RestfulFile> receivedFiles = settingsManager.getReceivedFiles();

        if(receivedFiles == null) receivedFiles = new ArrayList<RestfulFile>();

        return receivedFiles;
    }

    public RestfulFile getReceivedFileByBarcode(String barcode)
    {
        RestfulFile foundFile = null;

        List<RestfulFile> availableFiles = this.getReceivedFiles();

        if(availableFiles != null && availableFiles.size() > 0)
        {
            for(RestfulFile file : availableFiles)
            {
                if(file.getFileNumber().equals(barcode))
                {
                    foundFile = file;
                    break;
                }
            }
        }

        return foundFile;

    }

    public void deleteReceivedFile(RestfulFile file)
    {
         this.getReceivedFiles().remove(file);
    }


    public RestfulFile getRestfulRequestByBarcode(String barcode)
    {
        RestfulFile foundFile = null;

        List<RestfulFile> availableFiles = this.getNewRequests();

        if(availableFiles != null && availableFiles.size() > 0)
        {
            for(RestfulFile file : availableFiles)
            {
                if(file.getFileNumber().equals(barcode))
                {
                    foundFile = file;
                    break;
                }
            }
        }

        return foundFile;
    }




    public void operateOnFile(RestfulFile file, String newState, int Ready_File)
    {
        //now operate on the current file
        file.setState(newState);
        file.setReadyFile(Ready_File);
        FilesDBManager filesDBManager = settingsManager.getFilesManager().getFilesDBManager();

        boolean result = filesDBManager.insertFile(file);

        if(result)
        {
            //now remove it from the requests
            settingsManager.getNewRequests().remove(file);
            settingsManager.getReceivedFiles().remove(file);
        }


    }



    public List<RestfulFile> getFilesReadyForCollection()
    {
        try
        {

            //get all NEW requests from the database
            FilesDBManager filesDBManager = settingsManager.getFilesManager().getFilesDBManager();

            StringBuffer whereClause = new StringBuffer();
            whereClause.append(AlfahresDBHelper.EMP_ID).append("=").append("'")
                    .append(settingsManager.getAccount().getUserName())
                    .append("'")
                    .append(" AND ")
                    .append(AlfahresDBHelper.COL_STATE).append("=")
                    .append("'")
                    .append(FileModelStates.DISTRIBUTED.toString())
                    .append("'");

            List<RestfulFile> newFiles = filesDBManager.getFilesWhere(whereClause.toString());


            if(newFiles == null) newFiles = new ArrayList<RestfulFile>();

            return newFiles;


        }catch (Exception s)
        {
            s.printStackTrace();
            return new ArrayList<RestfulFile>();
        }
    }
    public List<RestfulFile> getFilesReadyForDistribution()
    {
        try
        {

            //get all NEW requests from the database
            FilesDBManager filesDBManager = settingsManager.getFilesManager().getFilesDBManager();

            StringBuffer whereClause = new StringBuffer();
            whereClause.append(AlfahresDBHelper.EMP_ID).append("=").append("'")
                    .append(settingsManager.getAccount().getUserName())
                    .append("'")
                    .append(" AND ")
                    .append(AlfahresDBHelper.COL_STATE).append("=")
                    .append("'")
                    .append(FileModelStates.COORDINATOR_IN.toString())
                    .append("'");

            List<RestfulFile> newFiles = filesDBManager.getFilesWhere(whereClause.toString());


            if(newFiles == null) newFiles = new ArrayList<RestfulFile>();

            return newFiles;


        }catch (Exception s)
        {
            s.printStackTrace();
            return new ArrayList<RestfulFile>();
        }
    }




    public List<RestfulFile> getNewRequests()
    {
        try
        {
            if(settingsManager.isEmptyRequests()) {
                //get all NEW requests from the database
                FilesDBManager filesDBManager = settingsManager.getFilesManager().getFilesDBManager();

                StringBuffer whereClause = new StringBuffer();
                whereClause.append(AlfahresDBHelper.EMP_ID).append("=").append("'")
                        .append(settingsManager.getAccount().getUserName())
                        .append("'")
                        .append(" AND ")
                        .append(AlfahresDBHelper.COL_STATE).append("=")
                        .append("'")
                        .append(FileModelStates.NEW.toString())
                        .append("'");

                List<RestfulFile> newFiles = filesDBManager.getFilesWhere(whereClause.toString());
                this.setNewRequests(newFiles);

            }

            return settingsManager.getNewRequests();



        }catch (Exception s)
        {
            s.printStackTrace();
            return new ArrayList<RestfulFile>();
        }
    }


    public SystemSettingsManager getSettingsManager() {
        return settingsManager;
    }

    public void setSettingsManager(SystemSettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }
}
