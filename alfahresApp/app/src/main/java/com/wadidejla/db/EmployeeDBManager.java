package com.wadidejla.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.degla.restful.models.RestfulEmployee;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by snouto on 27/05/15.
 */
public class EmployeeDBManager  {


    private AlfahresDBHelper dbHelper;

    public EmployeeDBManager(AlfahresDBHelper dbHelper)
    {
        this.dbHelper = dbHelper;
    }

    public RestfulEmployee getEmployeeBy(String columnName , String value)
    {
        try
        {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String[] selectableFields = dbHelper.getAllEmployeesColumns();
            String whereClause = columnName + "=" + value;
            String[] whereArgs = null;
            String groupBy = null;
            String having = null;
            String order = null;

            Cursor cursor = db.query(AlfahresDBHelper.DATABASE_TABLE_EMPLOYEE,selectableFields,whereClause,whereArgs,
                    groupBy,having,order);

            List<RestfulEmployee> employees = new ArrayList<RestfulEmployee>();
            //read from the cursor
            while(cursor.moveToNext())
            {
                RestfulEmployee emp = new RestfulEmployee();
                int columnIndex = cursor.getColumnIndex(AlfahresDBHelper.EMP_FIRSTNAME);
                emp.setFirstName(cursor.getString(columnIndex));
                columnIndex= cursor.getColumnIndex(AlfahresDBHelper.EMP_LASTNAME);
                emp.setLastName(cursor.getString(columnIndex));
                columnIndex = cursor.getColumnIndex(AlfahresDBHelper.EMP_ID);
                emp.setId(cursor.getInt(columnIndex));
                columnIndex = cursor.getColumnIndex(AlfahresDBHelper.EMP_ROLE_NAME);
                emp.setRole(cursor.getString(columnIndex));
                columnIndex = cursor.getColumnIndex(AlfahresDBHelper.EMP_USERNAME);
                emp.setUserName(cursor.getString(columnIndex));

                employees.add(emp);

            }

            //now close the database
            db.close();

            if(employees.size() > 0)
                return employees.get(0);
            else return null;

        }catch (Exception s)
        {
            Log.w("EmployeeDBManager",s.getMessage());
            return null;
        }
    }

    public RestfulEmployee getEmployeeByID(String empID)
    {
        try
        {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String[] selectableFields = dbHelper.getAllEmployeesColumns();
            String whereClause = AlfahresDBHelper.EMP_ID + "=" + empID;
            String[] whereArgs = null;
            String groupBy = null;
            String having = null;
            String order = null;

            Cursor cursor = db.query(AlfahresDBHelper.DATABASE_TABLE_EMPLOYEE,selectableFields,whereClause,whereArgs,
                    groupBy,having,order);

            List<RestfulEmployee> employees = new ArrayList<RestfulEmployee>();
            //read from the cursor
            while(cursor.moveToNext())
            {
                RestfulEmployee emp = new RestfulEmployee();
                int columnIndex = cursor.getColumnIndex(AlfahresDBHelper.EMP_FIRSTNAME);
                emp.setFirstName(cursor.getString(columnIndex));
                columnIndex= cursor.getColumnIndex(AlfahresDBHelper.EMP_LASTNAME);
                emp.setLastName(cursor.getString(columnIndex));
                emp.setId(Integer.parseInt(empID));
                columnIndex = cursor.getColumnIndex(AlfahresDBHelper.EMP_ROLE_NAME);
                emp.setRole(cursor.getString(columnIndex));
                columnIndex = cursor.getColumnIndex(AlfahresDBHelper.EMP_USERNAME);
                emp.setUserName(cursor.getString(columnIndex));

                employees.add(emp);

            }

            //now close the database connection
            db.close();

            if(employees.size() > 0)
                return employees.get(0);
            else return null;


        }catch (Exception s)
        {
            Log.w("EmployeeDBManager",s.getMessage());
            return null;
        }
    }


    public boolean insertEmployee(RestfulEmployee employee)
    {
        try
        {
            ContentValues values = new ContentValues();
            values.put(AlfahresDBHelper.EMP_FIRSTNAME,employee.getFirstName());
            values.put(AlfahresDBHelper.EMP_LASTNAME,employee.getLastName());
            values.put(AlfahresDBHelper.EMP_USERNAME,employee.getUserName());
            values.put(AlfahresDBHelper.EMP_ROLE_NAME,employee.getRole());

            //now insert it into the database
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            long affectedRow = db.insert(AlfahresDBHelper.DATABASE_TABLE_EMPLOYEE,null,values);

            db.close();

            if (affectedRow > 0)
                return true;
            else return false;


        }catch (Exception s)
        {
            Log.w("EmployeeDBManager",s.getMessage());
            return false;
        }
    }

}
