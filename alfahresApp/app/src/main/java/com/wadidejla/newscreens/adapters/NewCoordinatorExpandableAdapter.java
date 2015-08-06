package com.wadidejla.newscreens.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.degla.restful.models.CollectionBatch;
import com.degla.restful.models.FileModelStates;
import com.degla.restful.models.RestfulClinic;
import com.degla.restful.models.RestfulEmployee;
import com.degla.restful.models.RestfulFile;
import com.degla.restful.models.http.HttpResponse;
import com.wadidejla.listeners.KeeperOnClickListener;
import com.wadidejla.network.AlfahresConnection;
import com.wadidejla.newscreens.utils.ConnectivityUtils;
import com.wadidejla.newscreens.utils.DBStorageUtils;
import com.wadidejla.newscreens.utils.NewViewUtils;
import com.wadidejla.settings.SystemSettingsManager;
import com.wadidejla.utils.RestfulTransferInfo;
import com.wadidejla.utils.SoundUtils;

import java.net.HttpRetryException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import wadidejla.com.alfahresapp.R;

/**
 * Created by snouto on 09/06/15.
 */
public class NewCoordinatorExpandableAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> mainCategories;
    private HashMap<String,List<RestfulFile>> categorizedData;

    private KeeperOnClickListener<BaseExpandableListAdapter> listener;


    public NewCoordinatorExpandableAdapter(Context ctx)
    {
        this.setContext(ctx);
        this.loadData();
    }

    @Override
    public void notifyDataSetChanged() {
        //this.loadData();
        super.notifyDataSetChanged();

    }

    public void loadData() {

        try
        {

            final DBStorageUtils storageUtils = new DBStorageUtils(getContext());




            if(ConnectivityUtils.isConnected(getContext()))
            {

                Runnable networkingTask = new Runnable() {
                    @Override
                    public void run() {

                        AlfahresConnection connection = storageUtils.getSettingsManager().getConnection();

                        HttpResponse response = connection.path("files/collect").setMethodType(AlfahresConnection.POST_HTTP_METHOD)
                                .setAuthorization(storageUtils.getSettingsManager().getAccount().getUserName(),
                                        storageUtils.getSettingsManager().getAccount().getPassword())
                                .call(CollectionBatch.class);

                        if(response != null && Integer.parseInt(response.getResponseCode())==
                                HttpResponse.OK_HTTP_CODE)
                        {
                            //Get the collection Batch payload
                           CollectionBatch batch = (CollectionBatch)response.getPayload();

                            //save the current files into the local database
                            List<RestfulFile> allFiles = new ArrayList<RestfulFile>();

                            if(batch != null && batch
                                    .getClinics() != null && batch.getClinics().size() > 0)
                            {
                                //that means there are some data ,
                                for(RestfulClinic clinic : batch.getClinics())
                                {
                                    if(clinic.getFiles() != null)
                                    {
                                        for(RestfulFile tempFile : clinic.getFiles())
                                        {

                                            boolean containsFile = storageUtils.getSettingsManager().getFilesManager().getFilesDBManager()
                                                    .getFileByEmployeeAndNumber(storageUtils.getSettingsManager().getAccount().getUserName(),
                                                            tempFile.getFileNumber()) != null;

                                            if(!containsFile)
                                                allFiles.add(tempFile);

                                        }
                                    }
                                }
                            }

                            //now save all these files
                            if(allFiles.size() > 0)
                            {
                                for(RestfulFile file : allFiles)
                                {
                                    file.setEmp(storageUtils.getSettingsManager().getAccount());
                                    file.setReadyFile(RestfulFile.NOT_READY_FILE);
                                    //now save the current file into the database
                                    storageUtils.operateOnFile(file,FileModelStates.DISTRIBUTED.toString(),RestfulFile.NOT_READY_FILE);
                                }

                                CollectionBatch mybatch = new CollectionBatch();
                                //get all local files ready to be collected if any
                                List<RestfulFile> localFiles = storageUtils.getFilesReadyForCollection();
                                mybatch.addAllRestfulFiles(localFiles);
                                NewCoordinatorExpandableAdapter.this.setMainCategories(mybatch.getCategories());
                                NewCoordinatorExpandableAdapter.this.setCategorizedData(mybatch.getCategorizedData());

                                ((Activity)getContext()).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        NewCoordinatorExpandableAdapter.this.notifyDataSetChanged();
                                    }
                                });
                            }
                        }

                    }
                };

                // start the network thread
                Thread networkingThread =new Thread(networkingTask);
                networkingThread.start();


            }

            CollectionBatch batch = new CollectionBatch();
            //get all local files ready to be collected if any
            List<RestfulFile> localFiles = storageUtils.getFilesReadyForCollection();
            batch.addAllRestfulFiles(localFiles);
            this.setMainCategories(batch.getCategories());
            this.setCategorizedData(batch.getCategorizedData());

        }catch (Exception s)
        {
            s.printStackTrace();
        }

    }


    public boolean removeFile(RestfulFile file)
    {
        List<RestfulFile> containedList = null;

        for(String category : categorizedData.keySet())
        {
            List<RestfulFile> list = categorizedData.get(category);

            if(list != null && list.size() > 0)
            {
                for(RestfulFile currentFile : list)
                {
                    if(currentFile.getFileNumber().equalsIgnoreCase(file.getFileNumber()))
                    {
                        containedList = list;
                        break;
                    }
                }
            }
        }

        if(containedList != null)
            containedList.remove(file);

        //then notify
        this.notifyDataSetChanged();

        return true;
    }


    @Override
    public int getGroupCount() {
        return mainCategories.size();
    }

    @Override
    public int getChildrenCount(int parent) {

        return categorizedData.get(mainCategories.get(parent)).size();
    }

    @Override
    public Object getGroup(int position) {
        return mainCategories.get(position);
    }

    @Override
    public Object getChild(int parent, int child) {
        return categorizedData.get(mainCategories.get(parent)).get(child);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int parent, int child) {
        return child;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int parent, boolean lastChild, View convertView, ViewGroup viewGroup) {

        if(convertView == null)
        {
            LayoutInflater inflater = LayoutInflater.from(this.getContext());

            convertView = inflater.inflate(R.layout.coordinator_list_parent_layout,viewGroup,false);
        }

        //Get the text view to set the group title
        String groupTitle = (String) getGroup(parent);

        TextView groupView = (TextView)convertView.findViewById(R.id.coordinator_parent_item_txt);
        groupView.setTypeface(null, Typeface.BOLD);
        groupView.setText(groupTitle);

        return convertView;
    }

    @Override
    public View getChildView(int parent, int child, boolean lastChild, View convertView, ViewGroup viewGroup) {

        LayoutInflater inflater = LayoutInflater.from(this.getContext());

        if(convertView == null)
        {
            convertView = inflater.inflate(R.layout.new_single_file_view,viewGroup,false);
        }else
                NewViewUtils.returnToDefault(convertView,Color.WHITE,R.id.new_file_img);


        //get the child object which is a restful File
        final RestfulFile file = (RestfulFile) getChild(parent, child);

        convertView.setTag(file);



        if(file.isMultipleClinics())
        {
            ImageView imgView = (ImageView)convertView.findViewById(R.id.new_file_img);
            imgView.setImageResource(R.drawable.transferrable);
            convertView.setBackgroundColor(Color.MAGENTA);
        }

        if(file.isInpatient())
        {
            ImageView imgView = (ImageView)convertView.findViewById(R.id.new_file_img);
            imgView.setImageResource(R.drawable.inpatient);
            convertView.setBackgroundColor(Color.DKGRAY);
        }

        if(file.getSelected() == 1)
        {
            ImageView imgView = (ImageView)convertView.findViewById(R.id.new_file_img);
            imgView.setImageResource(R.drawable.complete);
            convertView.setBackgroundColor(Color.CYAN);
        }



        //File Number
        TextView fileNumberView = (TextView)convertView.findViewById(R.id.new_file_FileNumber);
        fileNumberView.setText(file.getFileNumber());

        //Patient Number
        TextView patientNumberView = (TextView)convertView.findViewById(R.id.new_file_PatientNumber);
        patientNumberView.setText(file.getPatientNumber());

        //Patient Name
        TextView patientNameView = (TextView)convertView.findViewById(R.id.new_file_PatientName);
        patientNameView.setText(file.getPatientName());


        //Batch Number
        TextView batchNumberView = (TextView)convertView.findViewById(R.id.new_file_BatchNumber);
        batchNumberView.setText(file.getBatchRequestNumber());

        //Doc Name
        TextView docNameView = (TextView)convertView.findViewById(R.id.new_file_RequestingDocName);
        docNameView.setText(file.getClinicDocName());

        //Clinic Name
        TextView clinicNameView = (TextView)convertView.findViewById(R.id.new_file_RequestingClinic);
        clinicNameView.setText(file.getClinicName());

        //Clinic Code
        TextView clinicCodeView = (TextView)convertView.findViewById(R.id.new_file_RequestingClinicCode);
        clinicCodeView.setText(file.getClinicCode());


        //Cabin Number
        TextView cabinetIdView = (TextView)convertView.findViewById(R.id.new_file_cabinetId);
        cabinetIdView.setText(file.getCabinetId());

        //Shelf Id
        TextView shelfIdView = (TextView)convertView.findViewById(R.id.new_file_ShelfId);
        shelfIdView.setText(file.getShelfId());

        //Column Id
        TextView columnIdView = (TextView)convertView.findViewById(R.id.new_file_FileColumnId);
        columnIdView.setText(file.getColumnId());

        //Img View
        ImageView imgView = (ImageView)convertView.findViewById(R.id.new_file_status_img);



        if (file.getState() != null && file.getState().equalsIgnoreCase(FileModelStates.MISSING.toString()))
        {
            //that means the file is missing so set the imageview to missing drawable from the resources folder
            imgView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.missing));

        }else if (file.getState() != null && file.getState().equalsIgnoreCase(FileModelStates.NEW.toString()))
        {
            imgView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.newrequests));
        }
        else if (file.getReadyFile() != 0 && file.getTemporaryCabinetId() != null &&
                file.getTemporaryCabinetId().length() >= 0) // that means the file is ready
        {
            //show the complete drawable
            imgView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.complete));
        }else
        {
            //it means the file is not ready at all , so display the preview icon
            imgView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.preview));
        }



        //finally return the convert View

        //add the click listener
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {


                if(!file.isMultipleClinics())
                {
                    final AlertDialog choiceDlg = new AlertDialog.Builder(getContext())
                            .setTitle(R.string.SINGLE_CHOICE_DLG_TITLE)
                            .setItems(new String[]{"Mark File as Missing...","Collect That File..."},
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            if (i == 0) // that means mark file as missing
                                            {
                                                //access the files Manager from the settings
                                                try {
                                                    DBStorageUtils storageUtils = new DBStorageUtils(getContext());

                                                    storageUtils.operateOnFile(file, FileModelStates.MISSING.toString(),
                                                            RestfulFile.READY_FILE);

                                                    //Play the sound
                                                    SoundUtils.playSound(getContext());
                                                    //Now refresh the adapter
                                                    NewCoordinatorExpandableAdapter
                                                            .this.notifyDataSetChanged();

                                                } catch (Exception s) {
                                                    Log.w("FilesArrayAdapter", s.getMessage());

                                                } finally {

                                                    dialogInterface.dismiss();
                                                }


                                            }else
                                            {
                                                //That means Collect that file manually.
                                                try {
                                                    DBStorageUtils storageUtils = new DBStorageUtils(getContext());

                                                    file.setTemporaryCabinetId("");

                                                    storageUtils.operateOnFile(file, FileModelStates.COORDINATOR_OUT.toString(),
                                                            RestfulFile.READY_FILE);

                                                    //Play the sound
                                                    SoundUtils.playSound(getContext());
                                                    //Now refresh the adapter
                                                    NewCoordinatorExpandableAdapter
                                                            .this.notifyDataSetChanged();

                                                } catch (Exception s) {
                                                    Log.w("FilesArrayAdapter", s.getMessage());

                                                } finally {

                                                    dialogInterface.dismiss();
                                                }

                                            }

                                        }
                                    }).create();

                    choiceDlg.show();

                }else // that means the file has a transfer, so view the file
                {
                    final AlertDialog choiceDlg = new AlertDialog.Builder(getContext())
                            .setTitle(R.string.SINGLE_CHOICE_DLG_TITLE)
                            .setItems(new String[]{"Mark File as Missing...","Collect That File...",
                                    "View Transfer Info..."},
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            if (i == 0) // that means mark file as missing
                                            {
                                                //access the files Manager from the settings
                                                try {
                                                    DBStorageUtils storageUtils = new DBStorageUtils(getContext());

                                                    storageUtils.operateOnFile(file, FileModelStates.MISSING.toString(),
                                                            RestfulFile.READY_FILE);

                                                    //Play the sound
                                                    SoundUtils.playSound(getContext());
                                                    //Now refresh the adapter
                                                    NewCoordinatorExpandableAdapter
                                                            .this.notifyDataSetChanged();

                                                } catch (Exception s) {
                                                    Log.w("FilesArrayAdapter", s.getMessage());

                                                } finally {

                                                    dialogInterface.dismiss();
                                                }


                                            }else if(i==1)
                                            {
                                                //That means Collect that file manually.
                                                try {
                                                    DBStorageUtils storageUtils = new DBStorageUtils(getContext());

                                                    file.setTemporaryCabinetId("");

                                                    storageUtils.operateOnFile(file, FileModelStates.COORDINATOR_OUT.toString(),
                                                            RestfulFile.READY_FILE);

                                                    //Play the sound
                                                    SoundUtils.playSound(getContext());
                                                    //Now refresh the adapter
                                                    NewCoordinatorExpandableAdapter
                                                            .this.notifyDataSetChanged();

                                                } catch (Exception s) {
                                                    Log.w("FilesArrayAdapter", s.getMessage());

                                                } finally {

                                                    dialogInterface.dismiss();
                                                }

                                            }else
                                            {
                                                //dismiss the dialog
                                                dialogInterface.dismiss();

                                                final ProgressDialog trWaitingDialog = NewViewUtils.getWaitingDialog(getContext());
                                                trWaitingDialog.setTitle("Please Wait...");
                                                trWaitingDialog.setMessage("Contacting Server....");
                                                trWaitingDialog.show();

                                                Runnable getting_Transfer_Details = new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        try
                                                        {
                                                            SystemSettingsManager settingsManager = SystemSettingsManager.createInstance(getContext());

                                                            AlfahresConnection connection = settingsManager.getConnection();
                                                            HttpResponse response = connection.setMethodType(AlfahresConnection.GET_HTTP_METHOD)
                                                                    .setAuthorization(settingsManager.getAccount())
                                                                    .path(String.format("files/transferInfo?fileNumber=%s",
                                                                            file.getFileNumber()))
                                                                    .call(RestfulTransferInfo.class);

                                                            if(response != null &&
                                                                    response.getResponseCode().equals(String.valueOf(HttpResponse.OK_HTTP_CODE)))
                                                            {

                                                                //That means the connection was successful
                                                                RestfulTransferInfo transferInfo = (RestfulTransferInfo) response
                                                                        .getPayload();

                                                                //Get the view
                                                                final View transferView = NewViewUtils.getTransferView(file,transferInfo,getContext());

                                                                if(transferView != null)
                                                                {
                                                                    //dismiss the dialog first
                                                                    trWaitingDialog.dismiss();

                                                                    Activity currentActivity = (Activity)getContext();

                                                                    if(currentActivity != null)
                                                                    {
                                                                        currentActivity.runOnUiThread(new Runnable() {
                                                                            @Override
                                                                            public void run() {

                                                                                AlertDialog dialog = new AlertDialog.Builder(getContext())
                                                                                        .setView(transferView)
                                                                                        .setIcon(R.drawable.transferrable)
                                                                                        .setTitle("Transfer Info")
                                                                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                                                            @Override
                                                                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                                                                dialogInterface.dismiss();
                                                                                            }
                                                                                        }).create();

                                                                                dialog.show();


                                                                            }
                                                                        });
                                                                    }
                                                                }

                                                            }


                                                        }catch (Exception s)
                                                        {
                                                            Log.e("Error",s.getMessage());
                                                        }
                                                        finally {

                                                            if(trWaitingDialog.isShowing())
                                                                trWaitingDialog.dismiss();
                                                        }


                                                    }
                                                };

                                                Thread transferThread = new Thread(getting_Transfer_Details);
                                                transferThread.start();




                                            }

                                        }
                                    }).create();

                    choiceDlg.show();
                }

                return true;
            }
        });

        return convertView;

    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }




    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<String> getMainCategories() {
        return mainCategories;
    }

    public void setMainCategories(List<String> mainCategories) {
        this.mainCategories = mainCategories;
    }

    public HashMap<String, List<RestfulFile>> getCategorizedData() {
        return categorizedData;
    }

    public void setCategorizedData(HashMap<String, List<RestfulFile>> categorizedData) {
        this.categorizedData = categorizedData;
    }


    public KeeperOnClickListener<BaseExpandableListAdapter> getListener() {
        return listener;
    }

    public void setListener(KeeperOnClickListener<BaseExpandableListAdapter> listener) {
        this.listener = listener;
    }

}
