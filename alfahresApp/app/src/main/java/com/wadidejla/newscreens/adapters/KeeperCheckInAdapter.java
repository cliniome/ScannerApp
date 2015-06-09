package com.wadidejla.newscreens.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.degla.restful.models.FileModelStates;
import com.degla.restful.models.RestfulFile;
import com.wadidejla.newscreens.IFragment;
import com.wadidejla.newscreens.NewArchiveFilesFragment;
import com.wadidejla.newscreens.utils.DBStorageUtils;
import com.wadidejla.newscreens.utils.ScannerUtils;
import com.wadidejla.utils.SoundUtils;

import java.util.List;

import wadidejla.com.alfahresapp.R;

import static com.wadidejla.newscreens.utils.ScannerUtils.SCANNER_TYPE_CAMERA;

/**
 * Created by snouto on 09/06/15.
 */
public class KeeperCheckInAdapter extends ArrayAdapter<RestfulFile> {

    private List<RestfulFile> checkInFiles;

    private int resourceId;

    private IFragment parentFragment;



    public KeeperCheckInAdapter(Context context, int resource,List<RestfulFile> files) {
        super(context, resource);
        this.resourceId = resource;
        this.checkInFiles = files;
    }

    @Override
    public int getCount() {

        return checkInFiles.size();
    }

    @Override
    public void notifyDataSetChanged() {

        this.loadData();
        super.notifyDataSetChanged();
    }

    private void loadData() {


        try
        {
            DBStorageUtils storageUtils  = new DBStorageUtils(getContext());
            this.checkInFiles = storageUtils.getReceivedFiles();



        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null)
        {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.new_single_file_view,parent,false);
        }

        final RestfulFile file = checkInFiles.get(position);
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

        //bind the convert view to the onLong click listeners in here

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {


                final AlertDialog choiceDlg = new AlertDialog.Builder(getContext())
                        .setTitle(R.string.SINGLE_CHOICE_DLG_TITLE)
                        .setItems(new String[]{"Mark File as Missing...",
                        "Scan Shelf for This File"}, new DialogInterface.OnClickListener() {
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
                                        KeeperCheckInAdapter.this.notifyDataSetChanged();

                                    } catch (Exception s) {
                                        Log.w("FilesArrayAdapter", s.getMessage());

                                    } finally {

                                        dialogInterface.dismiss();
                                    }

                                }else
                                {
                                    //that means scan shelf for that file
                                    ScannerUtils.ScanBarcode(getContext(), SCANNER_TYPE_CAMERA,
                                            getParentFragment(),true,file.getFileNumber());

                                }

                            }
                        }).create();

                choiceDlg.show();

                return true;
            }
        });

        return convertView;
    }

    public List<RestfulFile> getCheckInFiles() {
        return checkInFiles;
    }

    public void setCheckInFiles(List<RestfulFile> checkInFiles) {
        this.checkInFiles = checkInFiles;
    }

    public IFragment getParentFragment() {
        return parentFragment;
    }

    public void setParentFragment(IFragment parentFragment) {
        this.parentFragment = parentFragment;
    }
}
