package com.wadidejla.newscreens.utils;

import android.content.Context;
import android.util.Log;

import com.degla.restful.models.FileModelStates;
import com.degla.restful.models.RestfulFile;
import com.wadidejla.db.AlfahresDBHelper;
import com.wadidejla.db.FilesDBManager;
import com.wadidejla.settings.SystemSettingsManager;
import com.wadidejla.utils.AlFahresFilesManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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

    public void saveNewRequests(List<RestfulFile> requests)
    {
        try
        {

            if(requests != null && requests.size() > 0)
            {
                for(RestfulFile file : requests)
                {
                    file.setEmp(settingsManager.getAccount());
                    this.insertOrUpdateFile(file);
                }
            }

        }catch (Exception s)
        {
            Log.e("Error",s.getMessage());
        }
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


    public void deleteAllFiles(List<RestfulFile> deletableFiles)
    {
        if(deletableFiles != null && deletableFiles.size() >0)
        {
            for(RestfulFile currentFile :deletableFiles)
            {
                getSettingsManager().getFilesManager().getFilesDBManager().deleteFile(currentFile.getFileNumber());
            }
        }
    }




    public boolean operateOnFile(RestfulFile file, String newState, int Ready_File)
    {
        //now operate on the current file
        file.setState(newState);
        file.setReadyFile(Ready_File);
        file.setEmp(settingsManager.getAccount());
        //set the mobile device date
        file.setDeviceOperationDate(new Date().getTime());
        
        FilesDBManager filesDBManager = settingsManager.getFilesManager().getFilesDBManager();

        boolean result = filesDBManager.insertFile(file);

        if(result)
        {
            //now remove it from the requests
            settingsManager.getNewRequests().remove(file);
            settingsManager.getReceivedFiles().remove(file);
        }

        return result;


    }


    public Long getRecentServerTimeStamp(FileModelStates state)
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
                    .append(state.toString())
                    .append("'");

            List<RestfulFile> newFiles = filesDBManager.getFilesWhere(whereClause.toString(),AlfahresDBHelper.COL_OPERATION_DATE + " DESC");

            if(newFiles == null || newFiles.size() <=0) return Long.valueOf(-1);

            //Return the top element from the list
            RestfulFile firstOne = newFiles.get(0);


            //Get its Server Operation Date
            return firstOne.getOperationDate();





        }catch (Exception s)
        {
            s.printStackTrace();

            return Long.valueOf(-1);

        }
    }

    public List<RestfulFile> getFilesToBeDistributed()
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


    public RestfulFile getDistributableFile(String fileNumber)
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
                    .append("'")
                    .append(" AND ")
                    .append(AlfahresDBHelper.KEY_ID).append("=")
                    .append("'")
                    .append(fileNumber)
                    .append("'");

            List<RestfulFile> newFiles = filesDBManager.getFilesWhere(whereClause.toString());


            if(newFiles == null || newFiles.size() <= 0) return null;

            return newFiles.get(0);

        }catch (Exception s)
        {
            Log.e("Error", s.getMessage());
            return null;
        }
    }


    public RestfulFile getCollectableFile(String fileNumber,boolean multiple)
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
                    .append("'")
                    .append(" AND ")
                    .append(AlfahresDBHelper.KEY_ID).append("=")
                    .append("'")
                    .append(fileNumber)
                    .append("'").append(" AND ")
                    .append(AlfahresDBHelper.COL_TRANSFERRABLE_FIELD).append("=")
                    .append((multiple == true) ? 1 : 0);

            List<RestfulFile> newFiles = filesDBManager.getFilesWhere(whereClause.toString());


            if(newFiles == null || newFiles.size() <= 0) return null;

            return newFiles.get(0);

        }catch (Exception s)
        {
            Log.e("Error",s.getMessage());
            return null;
        }
    }

    public List<RestfulFile> getFilesReadyForCollection(boolean selected)
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
                    .append("'")
                    .append(" AND ").append(AlfahresDBHelper.COL_SELECTED_FILE)
                    .append("=").append("'").append((selected == true) ? 1 : 0).append("'");

            List<RestfulFile> newFiles = filesDBManager.getFilesWhere(whereClause.toString());


            if(newFiles == null) newFiles = new ArrayList<RestfulFile>();

            return newFiles;


        }catch (Exception s)
        {
            s.printStackTrace();
            return new ArrayList<RestfulFile>();
        }
    }

    public List<RestfulFile> getSelectedCollectFiles(boolean multiple)
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
                    .append(AlfahresDBHelper.COL_SELECTED_FILE)
                    .append("=")
                    .append(1)
                    .append(" AND ")
                    .append(AlfahresDBHelper.COL_STATE).append("=")
                    .append("'")
                    .append(FileModelStates.DISTRIBUTED.toString())
                    .append("'").append(" AND ")
                    .append(AlfahresDBHelper.COL_TRANSFERRABLE_FIELD).append("=")
                    .append(multiple? 1 : 0);

            List<RestfulFile> newFiles = filesDBManager.getFilesWhere(whereClause.toString());


            if(newFiles == null) newFiles = new ArrayList<RestfulFile>();

            return newFiles;


        }catch (Exception s)
        {
            s.printStackTrace();
            return new ArrayList<RestfulFile>();
        }
    }


    public RestfulFile getCollectFile(boolean multiple , String fileNumber)
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
                    .append(AlfahresDBHelper.KEY_ID)
                    .append("=")
                    .append("'")
                    .append(fileNumber)
                    .append("'")
                    .append(" AND ")
                    .append(AlfahresDBHelper.COL_STATE).append("=")
                    .append("'")
                    .append(FileModelStates.DISTRIBUTED.toString())
                    .append("'").append(" AND ")
                    .append(AlfahresDBHelper.COL_TRANSFERRABLE_FIELD).append("=")
                    .append(multiple? 1 : 0);

            List<RestfulFile> newFiles = filesDBManager.getFilesWhere(whereClause.toString());


            if(newFiles == null) newFiles = new ArrayList<RestfulFile>();

            if(newFiles.size() > 0 ) return newFiles.get(0);
            else return null;



        }catch (Exception s)
        {
            s.printStackTrace();
            return null;
        }
    }

    public List<RestfulFile> getCollectableFilesWithTransfer(boolean multiple)
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
                    .append("'").append(" AND ")
                    .append(AlfahresDBHelper.COL_TRANSFERRABLE_FIELD).append("=")
                    .append(multiple? 1 : 0);

            List<RestfulFile> newFiles = filesDBManager.getFilesWhere(whereClause.toString());


            if(newFiles == null) newFiles = new ArrayList<RestfulFile>();

            return newFiles;


        }catch (Exception s)
        {
            s.printStackTrace();
            return new ArrayList<RestfulFile>();
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
