package com.wadidejla.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.widget.Toast;

import com.wadidejla.settings.SystemSettingsManager;

/**
 * Created by snouto on 22/05/15.
 */
public class AlfahresPreferenceManager implements OnSharedPreferenceChangeListener {

    private Context context;

    private SystemSettingsManager settingsManager;

    public AlfahresPreferenceManager(Context con)
    {
        this.setContext(con);
        settingsManager = SystemSettingsManager.createInstance();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

        if(s != null)
        {
            if (s.equals(SystemSettingsManager.CHK_NOTIFICATIONS_NEW_MESSAGE))
                settingsManager.setReceiveNotification(sharedPreferences.getBoolean(s,false));
            else if (s.equals(SystemSettingsManager.TXT_SYSTEM_IP_KEY))
                settingsManager.setServerAddress(sharedPreferences.getString(s,""));
            else if (s.equals(SystemSettingsManager.RINGTONE_NEW_MESSAGE))
                settingsManager.setRingtoneName(sharedPreferences.getString(s,""));
            else if (s.equals(SystemSettingsManager.SYNC_FREQUENCY))
                settingsManager.setSync_Frequency(sharedPreferences.getInt(s,5));

        }

    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
