package com.wadidejla;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.wadidejla.settings.SystemSettingsManager;

/**
 * Created by snouto on 23/05/15.
 */
public class SystemService extends Service {


    private SystemSettingsManager systemSettingsManager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        super.onStartCommand(intent, flags, startId);

        return handleCommand(intent,flags,startId);

    }

    private int handleCommand(Intent intent, int flags, int startId) {


        //TODO : Implement on start command service logic in here
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
