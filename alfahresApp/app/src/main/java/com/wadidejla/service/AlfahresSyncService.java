package com.wadidejla.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.wadidejla.tasks.SyncTask;

/**
 * Created by snouto on 27/05/15.
 */
public class AlfahresSyncService extends Service {


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Thread syncTaskThread = new Thread(new SyncTask(this));

        //now start the sync Task Thread
        syncTaskThread.start();

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
}
