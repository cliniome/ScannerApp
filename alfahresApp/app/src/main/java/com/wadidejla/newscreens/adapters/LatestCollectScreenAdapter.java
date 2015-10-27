package com.wadidejla.newscreens.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.degla.restful.models.CollectionBatch;
import com.degla.restful.models.FileModelStates;
import com.degla.restful.models.RestfulFile;
import com.degla.restful.models.http.HttpResponse;
import com.wadidejla.network.AlfahresConnection;
import com.wadidejla.newscreens.IAdapterListener;
import com.wadidejla.newscreens.LatestCollectScreen;
import com.wadidejla.newscreens.utils.DBStorageUtils;
import com.wadidejla.newscreens.utils.NewViewUtils;
import com.wadidejla.settings.SystemSettingsManager;
import com.wadidejla.utils.RestfulTransferInfo;
import com.wadidejla.utils.SoundUtils;

import java.util.ArrayList;
import java.util.List;

import wadidejla.com.alfahresapp.R;

/**
 * Created by snouto on 27/10/15.
 */
public class LatestCollectScreenAdapter  extends ArrayAdapter<RestfulFile>{

    private int resourceId;
    private List<RestfulFile> availableFiles;
    private LayoutInflater inflater;
    private Fragment fragment;
    private boolean showMultipleAppointments;


    public LatestCollectScreenAdapter(Context context, int resource) {
        super(context, resource);
        this.setResourceId(resource);
        this.setAvailableFiles(new ArrayList<RestfulFile>());
        inflater = LayoutInflater.from(context);
    }


    @Override
    public void notifyDataSetChanged() {

        DBStorageUtils storageUtils = new DBStorageUtils(getContext());

        this.availableFiles = storageUtils.getCollectableFilesWithTransfer(isShowMultipleAppointments());

        if(getFragment() != null)
        {
            ((LatestCollectScreen)fragment).setTotalFiles(availableFiles.size());
            ((IAdapterListener)fragment).doUpdateFragment();
        }

        super.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null)
            {
                convertView = inflater.inflate(R.layout.new_single_file_view,null,false);

            }else
                NewViewUtils.returnToDefault(convertView, Color.WHITE, R.id.new_file_img);



        try
        {
            //get the child object which is a restful File
            final RestfulFile file = getAvailableFiles().get(position);

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
                                .setItems(new String[]{"Mark File as Missing..."},
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
                                                        //Order a new Load Data
                                                        //Now refresh the adapter
                                                        LatestCollectScreenAdapter
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
                                                        LatestCollectScreenAdapter
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
                                                        SoundUtils.PlayError(getContext());

                                                        AlertDialog dialog = NewViewUtils.getChoiceDialog(getContext(), "Warning", String.format("File : %s has another appointment , would you like " +
                                                                "to transfer Now  ?", file.getFileNumber()), new Runnable() {
                                                            @Override
                                                            public void run() {

                                                                DBStorageUtils storageUtils = new DBStorageUtils(getContext());

                                                                file.setTemporaryCabinetId("");

                                                                storageUtils.operateOnFile(file, FileModelStates.COORDINATOR_OUT.toString(),
                                                                        RestfulFile.READY_FILE);

                                                                //Play the sound
                                                                SoundUtils.playSound(getContext());

                                                                //Now refresh the adapter
                                                                LatestCollectScreenAdapter
                                                                        .this.notifyDataSetChanged();


                                                            }
                                                        }, new Runnable() {
                                                            @Override
                                                            public void run() {

                                                            }
                                                        });

                                                        dialog.show();

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


        }catch (Exception s)
        {
            Log.e("Coordinator",s.getMessage());
        }
        return convertView;



    }



    @Override
    public int getCount() {
        if(this.availableFiles == null) return 0;

        return this.availableFiles.size();
    }





    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }





    public List<RestfulFile> getAvailableFiles() {
        return availableFiles;
    }

    public void setAvailableFiles(List<RestfulFile> availableFiles) {
        this.availableFiles = availableFiles;
    }


    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public boolean isShowMultipleAppointments() {
        return showMultipleAppointments;
    }

    public void setShowMultipleAppointments(boolean showMultipleAppointments) {
        this.showMultipleAppointments = showMultipleAppointments;
    }
}
