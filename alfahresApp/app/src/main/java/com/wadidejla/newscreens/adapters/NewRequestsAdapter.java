package com.wadidejla.newscreens.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.degla.restful.models.FileModelStates;
import com.degla.restful.models.RestfulFile;
import com.wadidejla.newscreens.utils.DBStorageUtils;
import com.wadidejla.settings.SystemSettingsManager;
import com.wadidejla.utils.SoundUtils;

import java.util.List;

import wadidejla.com.alfahresapp.R;

/**
 * Created by snouto on 08/06/15.
 */
public class NewRequestsAdapter extends ArrayAdapter<RestfulFile> {


    private List<RestfulFile> availableFiles;
    private int resourceId;


    public NewRequestsAdapter(Context context, int resource,List<RestfulFile> files) {
        super(context, resource);
        this.availableFiles = files;
        this.resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(this.resourceId,parent,false);

        //begin assigning data to the current view based on the current Restful File
       final  RestfulFile file = availableFiles.get(position);

        if(file.isInpatient())
        {
            ImageView mainImgView = (ImageView)convertView.findViewById(R.id.new_file_img);
            mainImgView.setImageResource(R.drawable.inpatient);
            convertView.setBackgroundColor(Color.MAGENTA);
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


        //add the click listener
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {





                final AlertDialog choiceDlg = new AlertDialog.Builder(getContext())
                        .setTitle(R.string.SINGLE_CHOICE_DLG_TITLE)

                        .setItems(new String[]{"Mark File as Missing...", "Clear That File..."}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                DBStorageUtils storageUtils = new DBStorageUtils(getContext());

                                if (i == 0) // that means mark file as missing
                                {
                                    //access the files Manager from the settings
                                    try {


                                        storageUtils.operateOnFile(file, FileModelStates.MISSING.toString(),
                                                RestfulFile.READY_FILE);

                                        //Play the sound
                                        SoundUtils.playSound(getContext());
                                        //Now refresh the adapter
                                        NewRequestsAdapter.this.notifyDataSetChanged();

                                    } catch (Exception s) {
                                        Log.w("FilesArrayAdapter", s.getMessage());

                                    } finally {

                                        dialogInterface.dismiss();
                                    }


                                } else {
                                    //That means clear that file
                                    availableFiles.remove(file);
                                    storageUtils.deleteReceivedFile(file);
                                    storageUtils.getSettingsManager().getFilesManager().getFilesDBManager()
                                            .deleteFile(file.getFileNumber());

                                    SoundUtils.playSound(getContext());
                                    NewRequestsAdapter.this.notifyDataSetChanged();
                                }

                            }
                        }).create();

                choiceDlg.show();

                return true;
            }
        });

        return convertView;

    }

    @Override
    public int getCount() {

        return availableFiles.size();
    }
}
