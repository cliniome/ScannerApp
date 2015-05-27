package com.wadidejla.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import static com.wadidejla.db.AlfahresDBHelper.*;

import com.degla.restful.models.RestfulEmployee;
import com.degla.restful.models.RestfulFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by snouto on 23/05/15.
 */
public class FilesDBManager {

    private static final String CLASS_NAME="FilesDBManager";


    private AlfahresDBHelper dbHelper;
    private String tableName;

    /**
     *
     * @param helper - The database helper
     * @param tableName - the table name from which records will be retreived
     */
    public FilesDBManager(AlfahresDBHelper helper,String tableName)
    {
        this.dbHelper = helper;
        this.setTableName(tableName);
    }


    public List<RestfulFile> getFilesByColumnValue(String columnName,String value)
    {
        try
        {
            String[] selectableFields = dbHelper.getAllSyncFilesColumns();
            String whereClause = columnName +"='" + value+"'";
            String[] whereArgs = null;
            String groupBy = null;
            String having = null;
            String order = null;

            SQLiteDatabase db = dbHelper.getWritableDatabase();

            List<RestfulFile> files = new ArrayList<RestfulFile>();

            Cursor cursor = db.query(this.getTableName(),selectableFields,
                    whereClause,whereArgs,groupBy,having,order
            );

            while(cursor.moveToNext())
            {
                RestfulFile file = new RestfulFile();
                int columnIndex = cursor.getColumnIndex(AlfahresDBHelper.COL_CABINETID);
                file.setCabinetId(cursor.getString(columnIndex));
                columnIndex = cursor.getColumnIndex(AlfahresDBHelper.COL_DESCRIPTION);
                file.setDescription(cursor.getString(columnIndex));
                columnIndex = cursor.getColumnIndex(AlfahresDBHelper.COL_OPERATION_DATE);
                file.setOperationDate(new Date(cursor.getString(columnIndex)));
                columnIndex = cursor.getColumnIndex(AlfahresDBHelper.COL_SHELFID);
                file.setShelfId(cursor.getString(columnIndex));
                columnIndex = cursor.getColumnIndex(AlfahresDBHelper.COL_STATE);
                file.setState(cursor.getString(columnIndex));
                columnIndex = cursor.getColumnIndex(AlfahresDBHelper.COL_TEMP_CABINID);
                file.setTemporaryCabinetId(cursor.getString(columnIndex));
                columnIndex = cursor.getColumnIndex(AlfahresDBHelper.KEY_ID);
                file.setFileNumber(cursor.getString(columnIndex));
                columnIndex = cursor.getColumnIndex(AlfahresDBHelper.COL_CLINIC_DOC_NAME);
                file.setClinicDocName(cursor.getString(columnIndex));
                columnIndex = cursor.getColumnIndex(AlfahresDBHelper.COL_CLINIC_NAME);
                file.setClinicName(cursor.getString(columnIndex));
                columnIndex = cursor.getColumnIndex(AlfahresDBHelper.COL_BATCH_REQUEST_NUMBER);
                file.setBatchRequestNumber(cursor.getString(columnIndex));
                columnIndex = cursor.getColumnIndex(AlfahresDBHelper.COL_EMP_USERNAME);
                file.getEmp().setUserName(cursor.getString(columnIndex));
                columnIndex = cursor.getColumnIndex(AlfahresDBHelper.EMP_ID);
                file.getEmp().setId(Integer.parseInt(cursor.getString(columnIndex)));

                files.add(file);

            }

            //now close the connection to the database
            db.close();

            return files;

        }catch (Exception s)
        {
            Log.w("FilesDBManager",s.getMessage());
            return null;
        }
    }

    public List<RestfulFile> getAllFilesForEmployee(String empID)
    {
        try
        {
            String[] selectableFields = dbHelper.getAllFilesColumns();
            String whereClause = AlfahresDBHelper.EMP_ID+"='"+empID+"'";
            String[] whereArgs = null;
            String groupBy = null;
            String having = null;
            String order = null;

            SQLiteDatabase db = dbHelper.getReadableDatabase();

            List<RestfulFile> files = new ArrayList<RestfulFile>();

            Cursor cursor = db.query(this.getTableName()
                    ,selectableFields,whereClause,whereArgs,groupBy,having,order
            );

            while(cursor.moveToNext())
            {
                RestfulFile file = new RestfulFile();
                int columnIndex = cursor.getColumnIndex(AlfahresDBHelper.COL_CABINETID);
                file.setCabinetId(cursor.getString(columnIndex));
                columnIndex = cursor.getColumnIndex(AlfahresDBHelper.COL_DESCRIPTION);
                file.setDescription(cursor.getString(columnIndex));
                columnIndex = cursor.getColumnIndex(AlfahresDBHelper.COL_OPERATION_DATE);
                file.setOperationDate(new Date(cursor.getString(columnIndex)));
                columnIndex = cursor.getColumnIndex(AlfahresDBHelper.COL_SHELFID);
                file.setShelfId(cursor.getString(columnIndex));
                columnIndex = cursor.getColumnIndex(AlfahresDBHelper.COL_STATE);
                file.setState(cursor.getString(columnIndex));
                columnIndex = cursor.getColumnIndex(AlfahresDBHelper.COL_TEMP_CABINID);
                file.setTemporaryCabinetId(cursor.getString(columnIndex));
                columnIndex = cursor.getColumnIndex(AlfahresDBHelper.KEY_ID);
                file.setFileNumber(cursor.getString(columnIndex));
                columnIndex = cursor.getColumnIndex(AlfahresDBHelper.COL_CLINIC_DOC_NAME);
                file.setClinicDocName(cursor.getString(columnIndex));
                columnIndex = cursor.getColumnIndex(AlfahresDBHelper.COL_CLINIC_NAME);
                file.setClinicName(cursor.getString(columnIndex));
                columnIndex = cursor.getColumnIndex(AlfahresDBHelper.COL_BATCH_REQUEST_NUMBER);
                file.setBatchRequestNumber(cursor.getString(columnIndex));
                columnIndex = cursor.getColumnIndex(AlfahresDBHelper.COL_EMP_USERNAME);
                file.getEmp().setUserName(cursor.getString(columnIndex));
                columnIndex = cursor.getColumnIndex(AlfahresDBHelper.EMP_ID);
                file.getEmp().setId(Integer.parseInt(cursor.getString(columnIndex)));

                files.add(file);

            }

            //now close the connection to the database
            db.close();

            return files;

        }catch (Exception s)
        {
            Log.w(AlfahresDBHelper.DATABASE_NAME,s.getMessage());

            return null;
        }
    }

    public List<RestfulFile> getAllFiles()
    {
        try
        {
            String[] selectableFields = dbHelper.getAllFilesColumns();
            String whereClause = null;
            String[] whereArgs = null;
            String groupBy = null;
            String having = null;
            String order = null;

            SQLiteDatabase db = dbHelper.getReadableDatabase();

            List<RestfulFile> files = new ArrayList<RestfulFile>();

            Cursor cursor = db.query(this.getTableName()
                    ,selectableFields,whereClause,whereArgs,groupBy,having,order
            );

            while(cursor.moveToNext())
            {
                RestfulFile file = new RestfulFile();
                int columnIndex = cursor.getColumnIndex(AlfahresDBHelper.COL_CABINETID);
                file.setCabinetId(cursor.getString(columnIndex));
                columnIndex = cursor.getColumnIndex(AlfahresDBHelper.COL_DESCRIPTION);
                file.setDescription(cursor.getString(columnIndex));
                columnIndex = cursor.getColumnIndex(AlfahresDBHelper.COL_OPERATION_DATE);
                file.setOperationDate(new Date(cursor.getString(columnIndex)));
                columnIndex = cursor.getColumnIndex(AlfahresDBHelper.COL_SHELFID);
                file.setShelfId(cursor.getString(columnIndex));
                columnIndex = cursor.getColumnIndex(AlfahresDBHelper.COL_STATE);
                file.setState(cursor.getString(columnIndex));
                columnIndex = cursor.getColumnIndex(AlfahresDBHelper.COL_TEMP_CABINID);
                file.setTemporaryCabinetId(cursor.getString(columnIndex));
                columnIndex = cursor.getColumnIndex(AlfahresDBHelper.KEY_ID);
                file.setFileNumber(cursor.getString(columnIndex));
                columnIndex = cursor.getColumnIndex(AlfahresDBHelper.COL_CLINIC_DOC_NAME);
                file.setClinicDocName(cursor.getString(columnIndex));
                columnIndex = cursor.getColumnIndex(AlfahresDBHelper.COL_CLINIC_NAME);
                file.setClinicName(cursor.getString(columnIndex));
                columnIndex = cursor.getColumnIndex(AlfahresDBHelper.COL_BATCH_REQUEST_NUMBER);
                file.setBatchRequestNumber(cursor.getString(columnIndex));
                columnIndex = cursor.getColumnIndex(AlfahresDBHelper.COL_EMP_USERNAME);
                file.getEmp().setUserName(cursor.getString(columnIndex));
                columnIndex = cursor.getColumnIndex(AlfahresDBHelper.EMP_ID);
                file.getEmp().setId(Integer.parseInt(cursor.getString(columnIndex)));

                files.add(file);

            }

            //now close the connection to the database
            db.close();

            return files;

        }catch (Exception s)
        {
            Log.w(AlfahresDBHelper.DATABASE_NAME,s.getMessage());

            return null;
        }
    }


    public RestfulFile getFileByNumber(String fileNumber)
    {
        try
        {
            List<RestfulFile> existingFiles = getFilesByColumnValue(AlfahresDBHelper.KEY_ID,fileNumber);

            if(existingFiles != null && existingFiles.size() > 0)
            {
                return existingFiles.get(0); // get the first one in the results.

            }else return null;

        }catch (Exception s)
        {
            Log.w("FilesDBManager",s.getMessage());
            return null;
        }
    }

    public boolean deleteFile(String fileNumber)
    {
        try
        {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            String whereClause = AlfahresDBHelper.KEY_ID + "='" + fileNumber + "'";
            String[] whereArgs = null;

            int rows = db.delete(AlfahresDBHelper.DATABASE_TABLE_SYNC_FILES, whereClause, whereArgs);

            db.close();

            if(rows > 0) return true;
            else return false;


        }catch (Exception s)
        {
            Log.w(CLASS_NAME,s.getMessage());
            return false;
        }
    }

    public boolean updateFile(RestfulFile file)
    {
        //delete the file first
        boolean result = this.deleteFile(file.getFileNumber());

        if(result)
        {
            //add it again
            return this.insertFile(file);

        }else return false;

    }

    public boolean updateAllFilesFor(String empID , String temporaryCabinetId)
    {
        try
        {
            List<RestfulFile> availableFiles = this.getAllFilesForEmployee(empID);
            boolean result = true;

            if(availableFiles != null && availableFiles.size() > 0)
            {
                for(RestfulFile file : availableFiles)
                {
                    file.setTemporaryCabinetId(temporaryCabinetId);
                    result &= this.updateFile(file);
                }

            }else
            {
                result = false;
            }


            return result;



        }catch (Exception s)
        {
            Log.w(CLASS_NAME,s.getMessage());
            return false;
        }
    }
    /**
     * This method will insert a new file into the database
     * @param file
     * @return
     */
    public boolean insertFile(RestfulFile file)
    {
        try
        {
            //check to see if the file already exists
            RestfulFile existingFile = getFileByNumber(file.getFileNumber());
            if(existingFile != null)
            {
                //delete it to replace it
                this.deleteFile(existingFile.getFileNumber());
            }

            ContentValues values = new ContentValues();
            values.put(KEY_ID,file.getFileNumber());
            values.put(COL_CABINETID,file.getCabinetId());
            values.put(COL_DESCRIPTION,file.getDescription());
            if(file.getOperationDate() == null)
                file.setOperationDate(new Date());
            values.put(COL_OPERATION_DATE,file.getOperationDate().toString());
            values.put(COL_SHELFID,file.getShelfId());
            values.put(COL_STATE,file.getState());
            values.put(COL_TEMP_CABINID,file.getTemporaryCabinetId());
            values.put(EMP_ID,String.valueOf(file.getEmp().getId()));
            values.put(COL_EMP_USERNAME,file.getEmp().getUserName());
            values.put(COL_CLINIC_DOC_NAME,file.getClinicDocName());
            values.put(COL_CLINIC_NAME,file.getClinicName());
            values.put(COL_BATCH_REQUEST_NUMBER,file.getBatchRequestNumber());


            //now insert the current row into the database
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            long affectedRow = db.insert(this.getTableName(),null,values);

            db.close();

            return (affectedRow > 0);

        }catch (Exception s)
        {
            Log.w(dbHelper.DATABASE_NAME,s.getMessage());
            return false;
        }

    }


    public boolean  insertFiles(List<RestfulFile> files)
    {
        try
        {
            boolean result = true;

            if(files == null || files.size() <=0) return false;

            for(RestfulFile file : files)
            {
                result &= insertFile(file);
            }

            return result;

        }catch (Exception s)
        {
            Log.w(AlfahresDBHelper.DATABASE_NAME,s.getMessage());
            return false;
        }
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
