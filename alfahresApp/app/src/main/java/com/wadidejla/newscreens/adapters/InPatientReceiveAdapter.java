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
import com.wadidejla.newscreens.utils.NewViewUtils;
import com.wadidejla.utils.SoundUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import wadidejla.com.alfahresapp.R;

/**
 * Created by snouto on 06/10/15.
 */
public class InPatientReceiveAdapter extends ArrayAdapter<RestfulFile> {



    private int resourceId;
    private List<RestfulFile> files;


    public InPatientReceiveAdapter(Context context, int resource) {
        super(context, resource);
        this.resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());


        if(convertView == null)
        {
            convertView = inflater.inflate(this.resourceId,parent,false);
        }else
            NewViewUtils.returnToDefault(convertView, Color.WHITE, R.id.new_file_img);


        //begin assigning data to the current view based on the current Restful File
        final  RestfulFile file = files.get(position);

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
            convertView.setBackgroundColor(Color.WHITE);
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


        //add the click listener
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {





                final AlertDialog choiceDlg = new AlertDialog.Builder(getContext())
                        .setTitle(R.string.SINGLE_CHOICE_DLG_TITLE)

                        .setItems(new String[]{"Clear That File..."}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                DBStorageUtils storageUtils = new DBStorageUtils(getContext());

                                if (i == 0) // that means mark file as missing
                                {
                                    //access the files Manager from the settings
                                    try {


                                        //That means clear that file
                                        files.remove(file);

                                        SoundUtils.playSound(getContext());
                                        InPatientReceiveAdapter.this.notifyDataSetChanged();


                                    } catch (Exception s) {
                                        Log.w("FilesArrayAdapter", s.getMessage());

                                    } finally {

                                        dialogInterface.dismiss();
                                    }


                                }

                            }
                        }).create();

                choiceDlg.show();

                return true;
            }
        });

        return convertView;

    }



    public void addFile(RestfulFile file)
    {

        if(this.files == null) this.files = new ArrayList<RestfulFile>();

        if(!this.fileExists(file))
        {
            this.files.add(file);

        }
    }

    public RestfulFile getFileWithNumber(String fileNumber)
    {
        RestfulFile foundFile = null;

        if(this.files == null) return foundFile;

        for(RestfulFile file :this.files)
        {
            if(file.getFileNumber().equals(fileNumber))
            {
                foundFile = file;
                break;
            }
        }

        return foundFile;
    }

    private boolean fileExists(RestfulFile file)
    {
        boolean exists =false;

        for(RestfulFile current : files)
        {
            if(current.getFileNumber().equals(file.getFileNumber()))
            {
                exists = true;
                break;
            }
        }

        return exists;
    }

    public void refresh()
    {
        if(this.files == null) return;

        Collections.sort(this.files, new Comparator<RestfulFile>() {
            @Override
            public int compare(RestfulFile first, RestfulFile second) {

                return (second.getSelected() - first.getSelected());
            }
        });


        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {

        if(this.files == null) return 0;

        else return this.files.size();
    }



    public List<RestfulFile> getFiles() {
        return files;
    }

    public void setFiles(List<RestfulFile> files) {
        this.files = files;
    }
}
