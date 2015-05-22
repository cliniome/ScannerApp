package com.wadidejla.settings;

/**
 * Created by snouto on 22/05/15.
 */
public class SystemSettingsManager {

    public static final String TXT_SYSTEM_IP_KEY = "SYSTEM_IP";
    public static final String CHK_NOTIFICATIONS_NEW_MESSAGE= "notifications_new_message";
    public static final String RINGTONE_NEW_MESSAGE = "notifications_new_message_ringtone";
    public static final String SYNC_FREQUENCY = "sync_frequency";


    private String serverAddress;
    private boolean receiveNotification;
    private String ringtoneName;
    private int sync_Frequency;
    private static SystemSettingsManager _instance;
    private SystemSettingsManager(){}



    public static SystemSettingsManager createInstance()
    {
        if(_instance == null)
            _instance = new SystemSettingsManager();

        return _instance;
    }


    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public boolean isReceiveNotification() {
        return receiveNotification;
    }

    public void setReceiveNotification(boolean receiveNotification) {
        this.receiveNotification = receiveNotification;
    }

    public String getRingtoneName() {
        return ringtoneName;
    }

    public void setRingtoneName(String ringtoneName) {
        this.ringtoneName = ringtoneName;
    }

    public int getSync_Frequency() {
        return sync_Frequency;
    }

    public void setSync_Frequency(int sync_Frequency) {
        this.sync_Frequency = sync_Frequency;
    }
}
