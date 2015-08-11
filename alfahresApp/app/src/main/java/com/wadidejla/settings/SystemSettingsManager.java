package com.wadidejla.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.degla.restful.models.RestfulEmployee;
import com.degla.restful.models.RestfulFile;
import com.wadidejla.db.AlfahresDBHelper;
import com.wadidejla.network.AlfahresConnection;
import com.wadidejla.newscreens.adapters.TabDetailsArrayAdapter;
import com.wadidejla.newscreens.utils.NetworkUtils;
import com.wadidejla.utils.AlFahresFilesManager;
import com.wadidejla.utils.EmployeeUtils;
import com.wadidejla.utils.FilesManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by snouto on 22/05/15.
 */
public class SystemSettingsManager {

    public static final String TXT_SYSTEM_IP_KEY = "SYSTEM_IP";
    public static final String CHK_NOTIFICATIONS_NEW_MESSAGE= "notifications_new_message";
    public static final String RINGTONE_NEW_MESSAGE = "notifications_new_message_ringtone";
    public static final String SYNC_FREQUENCY = "sync_frequency";


    private String serverAddress = "100.43.100.115";
    private boolean receiveNotification;
    private String ringtoneName;
    private int sync_Frequency = 30;
    private Context context;
    private static SystemSettingsManager _instance;
    private boolean batteryLow = false;
    private boolean canContinue = true;
    private RestfulEmployee account;
    private List<RestfulFile> newRequests;
    private List<RestfulFile> receivedFiles;
    private AlFahresFilesManager filesManager;
    private List<RestfulFile> temporaryFiles;
    private List<RestfulFile> shippingFiles;
    private List<RestfulFile> transferrableFiles;
    private boolean collectingBegun =false;
    private static TabDetailsArrayAdapter tabDetailsArrayAdapter;

    private TabDetailsCounters counters;

    private SystemSettingsManager(Context con){

        this.context = con;
        this.newRequests = new ArrayList<RestfulFile>();
        this.receivedFiles = new ArrayList<RestfulFile>();
        this.shippingFiles = new ArrayList<RestfulFile>();
        counters = new TabDetailsCounters();
        this.initSettings();
        this.initManager();

    }

    public void updateMasterView()
    {
        if(tabDetailsArrayAdapter != null)
            tabDetailsArrayAdapter.notifyDataSetChanged();
    }

    public static TabDetailsArrayAdapter getTabDetailsArrayAdapter() {
        return tabDetailsArrayAdapter;
    }

    public static void setTabDetailsArrayAdapter(TabDetailsArrayAdapter tabDetailsArrayAdapter) {
        SystemSettingsManager.tabDetailsArrayAdapter = tabDetailsArrayAdapter;
    }

    public boolean removeTransferrableFile(RestfulFile file)
    {
        RestfulFile foundFile = null;

        if(this.getTransferrableFiles() != null && this.getTransferrableFiles().size() > 0)
        {
            for(RestfulFile currentfile : this.getTransferrableFiles())
            {
                if(currentfile.getFileNumber().equals(file.getFileNumber()))
                {
                    foundFile = currentfile;
                    break;
                }
            }
        }

        if(foundFile == null) return false;

        else return this.getTransferrableFiles().remove(foundFile);
    }

    public boolean safelyAddToCollection(List<RestfulFile> collection , RestfulFile file)
    {
        if(collection == null)
            collection = new ArrayList<RestfulFile>();

        RestfulFile foundFile = this.checkFileExistsInCollection(collection,file);

        if(foundFile == null)
            collection.add(file);

        return (foundFile == null ? true : false);
    }

    private RestfulFile checkFileExistsInCollection(List<RestfulFile> collection, RestfulFile file) {

        RestfulFile exists = null;

        for(RestfulFile found : collection)
        {
            if(found.getFileNumber().equals(file.getFileNumber()))
            {
                exists = found;
                break;
            }
        }

        return exists;
    }

    private void initManager(){

        AlfahresDBHelper helper = new AlfahresDBHelper(this.context,AlfahresDBHelper.DATABASE_NAME,null
                ,AlfahresDBHelper.DATABASE_VERSION);
        setFilesManager(new AlFahresFilesManager(helper, AlfahresDBHelper.DATABASE_TABLE_SYNC_FILES));
        getFilesManager().setFiles(this.getNewRequests());
        this.setCollectingBegun(false);


    }

    public boolean isEmptyRequests()
    {
        if (this.newRequests == null || this.newRequests.size() <=0)
            return true;
        else return false;
    }

    public void logOut()
    {
        try
        {
            //Schedule an immediate Synchronization
            this.setNewRequests(null);
            this.setReceivedFiles(null);
            this.setCollectingBegun(false);

        }catch (Exception s)
        {
            Log.e("Error",s.getMessage());
        }
    }


    public AlfahresConnection getConnection()
    {
        AlfahresConnection conn = new AlfahresConnection(getServerAddress(),"8080","alfahres","rest");
        return conn;
    }


    public synchronized  FilesManager getSyncFilesManager()
    {
        return this.getFilesManager();
    }

    public synchronized  FilesManager getReceivedSyncFilesManager()
    {
        AlfahresDBHelper helper = new AlfahresDBHelper(this.context,AlfahresDBHelper.DATABASE_NAME,null
                ,AlfahresDBHelper.DATABASE_VERSION);
        AlFahresFilesManager filesManager = new AlFahresFilesManager(helper,AlfahresDBHelper.DATABASE_TABLE_SYNC_FILES);
        filesManager.setFiles(this.getNewRequests());

        return filesManager;
    }


    public synchronized boolean canProceed()
    {
        return ((!isBatteryLow()) && canContinue);
    }

    private void initSettings() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.context);

        if(prefs != null)
        {
            String hostName = prefs.getString(TXT_SYSTEM_IP_KEY, "");
            this.setServerAddress(hostName);
            try
            {
                int syncFrequency = prefs.getInt(SYNC_FREQUENCY,5);

                this.setSync_Frequency(syncFrequency);

            }catch (Exception s)
            {

            }
        }
    }


    public static SystemSettingsManager createInstance(Context con)
    {
        if(_instance == null)
            _instance = new SystemSettingsManager(con);

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

    public boolean isBatteryLow() {
        return batteryLow;
    }

    public void setBatteryLow(boolean batteryLow) {
        this.batteryLow = batteryLow;
    }

    public boolean isCanContinue() {
        return canContinue;
    }

    public void setCanContinue(boolean canContinue) {
        this.canContinue = canContinue;
    }


    public List<RestfulFile> getNewRequests() {
        if(newRequests == null) newRequests = new ArrayList<RestfulFile>();
        return newRequests;
    }

    public void addToNewRequests(List<RestfulFile> newRequests)
    {
        if(newRequests != null)
        {
            //set the files to the filesManager
            for(RestfulFile file : newRequests)
            {
                file.setEmp(getAccount());

                if(this.newRequests.contains(file))continue;
                else this.newRequests.add(file);
            }

        }else newRequests = new ArrayList<RestfulFile>();


    }

    public void setNewRequests(List<RestfulFile> newRequests) {

        if(newRequests != null)
        {
            //set the files to the filesManager
            for(RestfulFile file : newRequests)
            {
                file.setEmp(getAccount());
            }

        }else newRequests = new ArrayList<RestfulFile>();

        this.newRequests = newRequests;

        getFilesManager().setFiles(this.newRequests);
    }

    public RestfulEmployee getAccount() {
        return account;
    }

    public void setAccount(RestfulEmployee account) {
        this.account = account;
    }


    public List<RestfulFile> getReceivedFiles() {
        if(this.receivedFiles == null)
            this.receivedFiles = new ArrayList<RestfulFile>();

        return receivedFiles;
    }

    public void addToReceivedFiles(RestfulFile file)
    {
        if(this.receivedFiles == null)
            this.receivedFiles = new ArrayList<RestfulFile>();

        if(!fileExists(file,this.receivedFiles))
            this.getReceivedFiles().add(file);

    }

    private boolean fileExists(RestfulFile file,List<RestfulFile> collection)
    {
        if(collection == null || collection.isEmpty()) return false;
        else
        {
            boolean exists = false;

            for(RestfulFile found : collection)
            {
                if(found.getFileNumber().equals(file.getFileNumber()))
                    exists = true;
            }

            return exists;
        }
    }

    public void setReceivedFiles(List<RestfulFile> receivedFiles) {
        this.receivedFiles = receivedFiles;

        if(this.receivedFiles != null)
        {
            for(RestfulFile file : this.receivedFiles)
            {
                file.setEmp(getAccount());
                //file.setState(EmployeeUtils.getStatesForFiles(getAccount(),EmployeeUtils.RECEIVE_FILES));
                file.setReadyFile(RestfulFile.NOT_READY_FILE);
            }
        }
    }

    public boolean isCollectingBegun() {
        return collectingBegun;
    }

    public void setCollectingBegun(boolean collectingBegun) {
        this.collectingBegun = collectingBegun;
    }

    public List<RestfulFile> getTemporaryFiles() {
        return temporaryFiles;
    }

    public void setTemporaryFiles(List<RestfulFile> temporaryFiles) {
        this.temporaryFiles = temporaryFiles;
    }

    public AlFahresFilesManager getFilesManager() {
        return filesManager;
    }

    public void setFilesManager(AlFahresFilesManager filesManager) {
        this.filesManager = filesManager;
    }

    public List<RestfulFile> getShippingFiles() {
        return shippingFiles;
    }

    public void setShippingFiles(List<RestfulFile> shippingFiles) {
        this.shippingFiles = shippingFiles;
    }

    public List<RestfulFile> getTransferrableFiles() {
        return transferrableFiles;
    }

    public void setTransferrableFiles(List<RestfulFile> transferrableFiles) {
        this.transferrableFiles = transferrableFiles;
    }


    public TabDetailsCounters getCounters() {
        return counters;
    }

    public void setCounters(TabDetailsCounters counters) {
        this.counters = counters;
    }
}
