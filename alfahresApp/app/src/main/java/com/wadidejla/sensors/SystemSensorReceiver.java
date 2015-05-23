package com.wadidejla.sensors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wadidejla.settings.SystemSettingsManager;

import wadidejla.com.alfahresapp.LoginScreen;

/**
 * Created by snouto on 23/05/15.
 */
public class SystemSensorReceiver extends BroadcastReceiver {

    private static final String BOOT_COMPLETED_ACTION="android.intent.action.BOOT_COMPLETED";
    private static final String BATTERY_LOW_ACTION="android.intent.action.BATTERY_LOW";
    private static final String BATTERY_OK_ACTION="android.intent.action.BATTERY_OKAY";
    private static final String REBOOT_ACTION="android.intent.action.REBOOT";

    private SystemSettingsManager settingsManager;


    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction() != null && intent.getAction().equals(BOOT_COMPLETED_ACTION))
        {
            //now try to start the main activity of the current application
            Intent startIntent = new Intent(context, LoginScreen.class);
            startIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //now try to start the current activity
            if(context != null)
                context.startActivity(startIntent);
        }else if (intent.getAction() != null && intent.getAction().equals(BATTERY_LOW_ACTION))
        {
            if(settingsManager == null)
                settingsManager = SystemSettingsManager.createInstance(context);

            settingsManager.setBatteryLow(true);
            settingsManager.setCanContinue(false);

        }else if (intent.getAction() != null && intent.getAction().equals(BATTERY_OK_ACTION))
        {
            if(settingsManager == null)
                settingsManager = SystemSettingsManager.createInstance(context);

            settingsManager.setBatteryLow(false);
            settingsManager.setCanContinue(true);
        }else if (intent.getAction() != null && intent.getAction().equals(REBOOT_ACTION))
        {
            if(settingsManager == null)
                settingsManager = SystemSettingsManager.createInstance(context);

            settingsManager.setCanContinue(false);

        }

    }
}
