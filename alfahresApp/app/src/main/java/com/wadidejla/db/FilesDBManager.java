package com.wadidejla.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import static com.wadidejla.db.AlfahresDBHelper.*;
import com.degla.restful.models.RestfulFile;

import java.util.Date;
import java.util.List;

/**
 * Created by snouto on 23/05/15.
 */
public class FilesDBManager {

    private AlfahresDBHelper dbHelper;

    public FilesDBManager(AlfahresDBHelper helper)
    {
        this.dbHelper = helper;
    }

    //TODO : define the operations that must be done on the database for files and employees

    /**
     * This method will insert a new file into the database
     * @param file
     * @return
     */
    public boolean insertFile(RestfulFile file)
    {
        try
        {
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

            //now insert the current row into the database
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            long affectedRow = db.insert(AlfahresDBHelper.DATABASE_TABLE_FILES,null,values);

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
}
