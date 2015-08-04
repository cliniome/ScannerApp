package com.wadidejla.newscreens.utils;

import com.wadidejla.screens.ScanAndReceiveFragment;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by snouto on 09/06/15.
 */
public class BarcodeUtils {


    public static final String TROLLEY_OBJECTID = "02";
    public static final String SHELF_OBJECTID ="00";
    public static final String FILE_OBJECTID = "01";
    public static final String SYMBOL = "-";
    public static final String TROLLEY_REGEX = TROLLEY_OBJECTID + "-"+"[0-9]{6,8}$";
    public static final String SHELF_REGEX = SHELF_OBJECTID +"-"+"[0-9]{6,8}$";
    public static final String FILE_REGEX = FILE_OBJECTID +"-" +"[0-9]{6,8}$";


    private String barcode;

    private static Map<String,String> patterns = null;

    public BarcodeUtils(String barcode)
    {
        this.barcode = barcode;
    }

    public boolean isTrolley()
    {
       return this.isA(TROLLEY_OBJECTID);
    }

    private boolean isA(String identifier)
    {
        String pattern = patterns.get(identifier);
        Pattern regex = Pattern.compile(pattern);

        if(this.barcode != null && regex.matcher(barcode).matches())
            return true;
        return false;
    }

    public boolean isShelf()
    {
        return this.isA(SHELF_OBJECTID);
    }

    public String getColumnNo()
    {
        if(isMedicalFile())
        {
            String[] splitted = this.barcode.split(SYMBOL);

            if(splitted.length <  2) return "";

            String fileNo = splitted[1];

            return String.valueOf(fileNo.charAt(3));

        }else return "";
    }


    public String getCabinID()
    {
        if(isMedicalFile())
        {
            String[] splitted = this.barcode.split(SYMBOL);

            if(splitted.length < 2) return "";

            String fileNo = splitted[1];

            String cabinNo = fileNo.substring(6,fileNo.length());
            if(cabinNo != null && cabinNo.length() > 0)
            {
                return  cabinNo;
                /*String reverse = "";

                for(int i = cabinNo.length()-1;i>=0;i--)
                {
                    reverse += cabinNo.charAt(i);
                }

                return reverse;*/

            }else return "";
//            return fileNo.substring(6,fileNo.length());

        }else return "";
    }

    public boolean isMedicalFile()
    {
        return this.isA(FILE_OBJECTID);
    }

    static
    {
        patterns = new HashMap<String,String>();
        patterns.put(TROLLEY_OBJECTID,TROLLEY_REGEX);
        patterns.put(SHELF_OBJECTID,SHELF_REGEX);
        patterns.put(FILE_OBJECTID,FILE_REGEX);
    }
}
