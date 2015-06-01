package com.wadidejla.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by snouto on 23/05/15.
 */
public class AlfahresDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME="alfahres.db";
    public static final int DATABASE_VERSION=2;
    public static final String DATABASE_TABLE_FILES="files";
    public static final String DATABASE_TABLE_SYNC_FILES="syncfiles";
    public static final String DATABASE_TABLE_EMPLOYEE = "employees";
    //define the columns
    public static final String KEY_ID="fileNumber";
    public static final String COL_OPERATION_DATE="operationDate";
    public static final String COL_DESCRIPTION="description";
    public static final String COL_STATE="state";
    public static final String COL_CABINETID="cabinetId";
    public static final String COL_SHELFID="shelfId";
    public static final String COL_TEMP_CABINID="temporaryCabinId";
    public static final String COL_CLINIC_NAME="clinicName";
    public static final String COL_CLINIC_DOC_NAME="clinicDocName";
    public static final String COL_BATCH_REQUEST_NUMBER ="batchRequestNumber";
    public static final String COL_EMP_USERNAME = "EmpUserName";

    //New Fields
    public static final String COL_APPOINTMENT_TYPE="AppointmentType";
    public static final String COL_APPOINTMENT_DATE = "AppointmentDate";
    public static final String COL_APPOINTMENT_DATE_H="AppointmentDateHijri";
    public static final String COL_APPOINTMENT_TIME="AppointmentTime";
    public static final String COL_APPOINTMENT_MADE_BY="AppointmentMadeBy";
    public static final String COL_PATIENTNUMBER ="patientNumber";
    public static final String COL_PATIENTNAME = "patientName";
    public static final String COL_CLINIC_CODE = "clinicCode";
    public static final String COL_CLINIC_DOC_CODE = "clinicDocCode";
    public static final String COL_READY_FILE = "readyFile";

    //Definition of employees Table's Columns
    public static final String EMP_ID = "Id";
    public static final String EMP_FIRSTNAME="firstName";
    public static final String EMP_LASTNAME ="lastName";
    public static final String EMP_USERNAME = "username";
    public static final String EMP_ROLE_NAME="role";


    private static final String DATABASE_TABLE_EMP_CREATE = "create table " +
            DATABASE_TABLE_EMPLOYEE +" ( " + EMP_ID +" text primary key, " +
            EMP_FIRSTNAME +" text not null," + EMP_LASTNAME +" text not null,"
            + EMP_ROLE_NAME +" text not null," + EMP_USERNAME + " text not null"
            +" );";


    //Database Create Statements for both tables
    private static final String DATABASE_TABLE_FILES_CREATE = "create table "
            + DATABASE_TABLE_FILES +" ( " + KEY_ID + " text primary key, " +
            COL_READY_FILE + " int null," +
            COL_CLINIC_DOC_CODE + " text null," + COL_CLINIC_CODE + " text null,"+
            COL_CABINETID +" text null," + COL_DESCRIPTION +" text null," +
            COL_APPOINTMENT_DATE +" text null , " + COL_APPOINTMENT_DATE_H + " text null, " +
            COL_PATIENTNAME + " text null," + COL_PATIENTNUMBER + " text null," +
            COL_APPOINTMENT_TIME + " text null, " + COL_APPOINTMENT_MADE_BY + " text null , "
            + COL_APPOINTMENT_TYPE + " text null , "
            + COL_OPERATION_DATE +" text null," + COL_SHELFID + " text null," +
            COL_STATE + " text null," + COL_TEMP_CABINID + " text null," +
            EMP_ID +" text not null,"+ COL_EMP_USERNAME +" text not null," +COL_CLINIC_NAME +" text null," +
            COL_CLINIC_DOC_NAME + " text null," + COL_BATCH_REQUEST_NUMBER + " text null"+
            " );";


    private static final String DATABASE_TABLE_SYNC_FILES_CREATE ="create table "
            + DATABASE_TABLE_SYNC_FILES +" ( " + KEY_ID + " text primary key, " +
            COL_READY_FILE + " int null," +
            COL_CLINIC_DOC_CODE + " text null," + COL_CLINIC_CODE + " text null,"+
            COL_CABINETID +" text null," + COL_DESCRIPTION +" text null," +
            COL_APPOINTMENT_DATE +" text null , " + COL_APPOINTMENT_DATE_H + " text null, " +
            COL_PATIENTNAME + " text null," + COL_PATIENTNUMBER + " text null," +
            COL_APPOINTMENT_TIME + " text null, " + COL_APPOINTMENT_MADE_BY + " text null , "
            + COL_APPOINTMENT_TYPE + " text null , "
            + COL_OPERATION_DATE +" text null," + COL_SHELFID + " text null," +
            COL_STATE + " text null," + COL_TEMP_CABINID + " text null," +
            EMP_ID +" text not null,"+ COL_EMP_USERNAME +" text not null," +COL_CLINIC_NAME +" text null," +
            COL_CLINIC_DOC_NAME + " text null," + COL_BATCH_REQUEST_NUMBER + " text null"+
            " );";



    public String[] getAllFilesColumns()
    {
        return new String[]
           {KEY_ID,COL_TEMP_CABINID,COL_STATE,COL_SHELFID,COL_OPERATION_DATE,COL_CABINETID,COL_DESCRIPTION,
           COL_CLINIC_DOC_NAME,COL_CLINIC_NAME,COL_BATCH_REQUEST_NUMBER,EMP_ID,COL_EMP_USERNAME,
           COL_APPOINTMENT_TYPE,COL_APPOINTMENT_MADE_BY,COL_APPOINTMENT_TIME,COL_APPOINTMENT_DATE_H,
           COL_APPOINTMENT_DATE,COL_PATIENTNUMBER,COL_PATIENTNAME,COL_CLINIC_CODE,COL_CLINIC_DOC_CODE
                   ,COL_READY_FILE};

    }

    public String[] getAllSyncFilesColumns()
    {
        return getAllFilesColumns();
    }

    public String[] getAllEmployeesColumns()
    {
        return new String[] {

                EMP_ID,EMP_LASTNAME,EMP_USERNAME,EMP_ROLE_NAME,EMP_FIRSTNAME
        };
    }

    //default constructor
    public AlfahresDBHelper(Context conn , String name ,  SQLiteDatabase.CursorFactory factory,int version)
    {
        super(conn,name,factory,version);
    }



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(DATABASE_TABLE_FILES_CREATE);
        sqLiteDatabase.execSQL(DATABASE_TABLE_SYNC_FILES_CREATE);
        sqLiteDatabase.execSQL(DATABASE_TABLE_EMP_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        Log.w(DATABASE_NAME,"Upgrading the database from version " + String.valueOf(oldVersion) + " to version : " +
        String.valueOf(newVersion));

        sqLiteDatabase.execSQL("drop table if exists " + DATABASE_TABLE_FILES);
        sqLiteDatabase.execSQL("drop table if exists " + DATABASE_TABLE_SYNC_FILES);
        sqLiteDatabase.execSQL("drop table if exists " + DATABASE_TABLE_EMPLOYEE);

        //create the new one
        onCreate(sqLiteDatabase);
    }
}
