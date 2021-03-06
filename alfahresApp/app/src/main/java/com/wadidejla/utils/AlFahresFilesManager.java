package com.wadidejla.utils;

import android.util.Log;

import com.degla.restful.models.CollectionBatch;
import com.degla.restful.models.FileModelStates;
import com.degla.restful.models.RestfulClinic;
import com.degla.restful.models.RestfulEmployee;
import com.degla.restful.models.RestfulFile;
import com.wadidejla.db.AlfahresDBHelper;
import com.wadidejla.db.EmployeeDBManager;
import com.wadidejla.db.FilesDBManager;

import java.util.ArrayList;
import java.util.Date;
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
    public synchronized boolean operateOnFile(String barcode,String state) {

        try
        {

            RestfulFile foundFile = getFileWithBarcode(barcode);



            if(foundFile != null)
            {
                foundFile.setState(state);
                foundFile.setReadyFile(RestfulFile.READY_FILE);
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
    public boolean operateOnFile(RestfulFile file,RestfulEmployee emp) {


        try
        {

            RestfulFile foundFile = getFileWithBarcode(file.getFileNumber());



            if(foundFile != null)
            {
                foundFile.setEmp(emp);
                foundFile.setState(file.getState());
                foundFile.setReadyFile(RestfulFile.READY_FILE);
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

    @Override
    public List<FilesOnChangeListener> getFilesListener() {
        return this.listeners;
    }

    @Override
    public boolean operateOnFiles(int flag,RestfulEmployee emp) {
        try
        {
            boolean result = true;

            if(getFiles() != null && getFiles().size() > 0)
            {
                for(RestfulFile file : getFiles())
                {
                    file.setReadyFile(flag);
                    file.setEmp(emp);
                   result = result && filesDBManager.insertFile(file);

                }


                if(result && getFilesListener() != null && getFilesListener().size() > 0)
                {
                    for(FilesOnChangeListener listener : getFilesListener())
                    {
                        listener.notifyChange();
                    }
                }
            }

            return result;

        }catch (Exception s)
        {
            Log.w("FilesManager",s.getMessage());
            return false;
        }
    }

    @Override
    public FilesDBManager getFilesDBManager() {

        return this.filesDBManager;
    }

    private RestfulClinic getClinic(String code , List<RestfulClinic> clinics)
    {
        RestfulClinic clinic = null;
        for(RestfulClinic current : clinics)
        {
            if(current.getClinicCode().equalsIgnoreCase(code))
            {
                clinic = current;
                break;
            }
        }

        return clinic;
    }

    @Override
    public CollectionBatch getCoordinatorFiles(int flag,RestfulEmployee emp) {

        try
        {
            CollectionBatch batch = new CollectionBatch();

            StringBuffer buffer = new StringBuffer();
            buffer.append(AlfahresDBHelper.COL_STATE.toString()).append("=").append("'")
                    .append(FileModelStates.COORDINATOR_IN.toString()).append("'").append(" AND ")
                    .append(AlfahresDBHelper.COL_READY_FILE).append("=").append("'")
                    .append(String.valueOf(flag)).append("'").append(" AND ")
                    .append(AlfahresDBHelper.EMP_ID).append("=").append("'").append(emp.getId())
            .append("'");

            String whereClause = buffer.toString();

            List<RestfulFile> availableFiles = filesDBManager.getFilesWhere(whereClause);

            if(availableFiles != null && availableFiles.size() > 0)
            {
                List<RestfulClinic> clinics = new ArrayList<RestfulClinic>();

                for(RestfulFile file : availableFiles)
                {
                    RestfulClinic currentClinic = getClinic(file.getClinicCode(),clinics);

                    if(currentClinic == null)
                    {
                        RestfulClinic newClinic = new RestfulClinic();
                        newClinic.setFiles(new ArrayList<RestfulFile>());
                        newClinic.setClinicCode(file.getClinicCode());
                        newClinic.setClinicName(file.getClinicName());

                        newClinic.getFiles().add(file);

                        clinics.add(newClinic);
                    }else
                    {
                        currentClinic.getFiles().add(file);
                    }
                }

                batch.setCreatedAt(new Date().getTime());
                batch.setClinics(clinics);

                return batch;


            }else throw new Exception("There are no files");

        }catch (Exception s)
        {
            s.printStackTrace();
            return new CollectionBatch();
        }
    }

    public void setOperatingTable(String operatingTable) {
        this.operatingTable = operatingTable;
    }

    public List<RestfulFile> getFiles() {

        if(this.files == null)
            this.files = new ArrayList<RestfulFile>();
        return files;
    }

    public void setFiles(List<RestfulFile> files) {


        this.files = files;
    }
}
