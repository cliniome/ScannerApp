package com.wadidejla.newscreens.utils;

import com.wadidejla.screens.ScanAndReceiveFragment;

/**
 * Created by snouto on 09/06/15.
 */
public class BarcodeUtils {


    public static final String TROLLEY_OBJECTID = "02";
    public static final String SHELF_OBJECTID ="00";
    public static final String FILE_OBJECTID = "01";

    private String barcode;

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
        if(this.barcode != null && this.barcode.startsWith(identifier,0))
            return true;
        return false;
    }

    public boolean isShelf()
    {
        return this.isA(SHELF_OBJECTID) && this.barcode.length() >=9;
    }

    public boolean isMedicalFile()
    {
        return this.isA(FILE_OBJECTID);
    }
}
