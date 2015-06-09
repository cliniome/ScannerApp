package com.wadidejla.newscreens.utils;

import android.app.Activity;
import android.content.Context;

import com.wadidejla.barcode.IntentIntegrator;
import com.wadidejla.newscreens.IFragment;

/**
 * Created by snouto on 09/06/15.
 */
public class ScannerUtils {



    public static final int SCANNER_TYPE_CAMERA = 1;
    public static final int SCANNER_TYPE_SENSOR = 2;

    public static void ScanBarcode(Context context,int scannerType,IFragment fragment)
    {
        //TODO : Implement the actual barcode scanning process in here
        switch (scannerType)
        {
            case SCANNER_TYPE_CAMERA:
            {
                IntentIntegrator integrator = new IntentIntegrator((Activity)context);
                integrator.initiateScan();
            }
            break;
            case SCANNER_TYPE_SENSOR:
            {
                //TODO : Call the sensor barcode reader in here
                String barcode = "XXXXXXX";

                if(fragment != null)
                    fragment.handleScanResults(barcode);
            }
            break;
        }


    }

}
