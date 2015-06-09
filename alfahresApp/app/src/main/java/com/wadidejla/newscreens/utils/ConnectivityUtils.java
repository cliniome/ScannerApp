package com.wadidejla.newscreens.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by snouto on 09/06/15.
 */
public class ConnectivityUtils {


    public static boolean isConnected(Context context)
    {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(manager == null) return false;
        else
        {
            NetworkInfo info = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            return info.isConnected();
        }
    }
}
