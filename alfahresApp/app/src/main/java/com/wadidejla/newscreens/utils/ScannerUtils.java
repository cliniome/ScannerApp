package com.wadidejla.newscreens.utils;

import android.app.Activity;
import android.content.Context;

import com.wadidejla.barcode.IntentIntegrator;
import com.wadidejla.newscreens.IFragment;

import wadidejla.com.alfahresapp.MainTabbedActivity;

/**
 * Created by snouto on 09/06/15.
 */
public class ScannerUtils {


    public static final String ARCHIVER_KEY="KEEPER_ARCHIVER";
    public static final String ARCHIVER_FILE_NUMBER="FILENUMBER";

    public static final int ARCHIVER_VALUE = 1;

    public static final int SCANNER_TYPE_CAMERA = 1;
    public static final int SCANNER_TYPE_SENSOR = 2;

    public static void ScanBarcode(Context context,int scannerType,IFragment fragment,boolean archiver,
                                   String fileNumber)
    {

        switch (scannerType)
        {
            case SCANNER_TYPE_CAMERA:
            {
                IntentIntegrator integrator = new IntentIntegrator((Activity)context);

                if(archiver)
                {

                    integrator.addExtra(ARCHIVER_KEY,ARCHIVER_VALUE);
                    MainTabbedActivity.SCANNED_ARCHIVER_FILE = fileNumber;
                    integrator.addExtra(ARCHIVER_FILE_NUMBER,fileNumber);
                }

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
