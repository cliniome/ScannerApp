package com.wadidejla.newscreens.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.degla.restful.models.FileModelStates;
import com.degla.restful.models.RestfulFile;
import com.wadidejla.newscreens.utils.FileStateUtils;

import java.util.ArrayList;
import java.util.List;

import wadidejla.com.alfahresapp.R;


/**
 * Created by snouto on 28/07/15.
 */
public class CheckFileStatusArrayAdapter extends ArrayAdapter<RestfulFile> {


    private List<RestfulFile> files;

    private int resourceId;

    public CheckFileStatusArrayAdapter(Context context, int resource) {
        super(context, resource);
        this.setResourceId(resource);
        files = new ArrayList<RestfulFile>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());

        try
        {
            convertView = inflater.inflate(getResourceId(),parent,false);
            RestfulFile file = files.get(position);
            //get the file Number Text
            TextView txtFileNumber = (TextView)convertView.findViewById(R.id.new_file_FileNumber);
            txtFileNumber.setText(file.getFileNumber());

            //Get the patient Number
            TextView txtPatientNumber = (TextView)convertView.findViewById(R.id.new_file_PatientNumber);
            txtPatientNumber.setText(file.getPatientNumber());

            //Get the status
            TextView txtFileStatus = (TextView)convertView.findViewById(R.id.file_status_Txt);

             FileStateUtils stateUtils = new FileStateUtils();

            txtFileStatus.setText(stateUtils.getReadableState(file.getState()));

            //Get the employee ID
            TextView txtEmpName = (TextView)convertView.findViewById(R.id.file_emp_seen_txt);
            txtEmpName.setText(file.getLastEmployeeName());


            //Get the clinic Name
            TextView txtClinicName = (TextView) convertView.findViewById(R.id.txtClinicNameText);
            txtClinicName.setText(file.getClinicName());

            //Get the clinic Code
            TextView txtClinicCode = (TextView)convertView.findViewById(R.id.txtClinicCode);
            txtClinicCode.setText(file.getClinicCode());


            //is it outpatient
            //0541003629

            if(file.isInpatient())
            {
                ImageView outputPatient = (ImageView)convertView.findViewById(R.id.ImgFileType);

                outputPatient.setImageResource(R.drawable.inpatient32);
            }






        }catch (Exception s)
        {
            Log.e("Error",s.getMessage());
        }

        return convertView;

    }

    @Override
    public int getCount() {

        return getFiles().size();
    }


    public List<RestfulFile> getFiles() {
        return files;
    }

    public void setFiles(List<RestfulFile> files) {
        this.files = files;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }
}
