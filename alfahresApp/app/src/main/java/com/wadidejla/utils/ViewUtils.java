package com.wadidejla.utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.degla.restful.models.RestfulFile;

import org.w3c.dom.Text;

import wadidejla.com.alfahresapp.R;

/**
 * Created by snouto on 30/05/15.
 */
public class ViewUtils {

    private static final String CLASS_NAME = "ViewUtils";


    public static View getDetailsViewFor(RestfulFile file,Context context)
    {
        try
        {
            LayoutInflater inflater = LayoutInflater.from(context);
            View rootView = inflater.inflate(R.layout.detailed_file_view,null);

            //now bind the elements of Restful File to the view
            if(rootView != null)
            {
                //get the TextView of file number
                TextView txtFileNumber = (TextView)rootView.findViewById(R.id.Txt_file_fileNumber);

                txtFileNumber.setText(file.getFileNumber());

                //get the patient Number
                TextView txtPatientNumber = (TextView)rootView.findViewById(R.id.Txt_file_patientNumber);
                txtPatientNumber.setText(file.getPatientNumber());

                //get the patient Name
                TextView txtPatientName = (TextView)rootView.findViewById(R.id.Txt_file_patientName);

                txtPatientName.setText(file.getPatientName());

                //get the appointment date and appointment time
                TextView txtAppointmentDate = (TextView)rootView.findViewById(R.id.Txt_file_appointment_Hijri);
                txtAppointmentDate.setText(file.getAppointmentDateH());

                //get the appointment type
                TextView txtAppointmentType = (TextView)rootView.findViewById(R.id.Txt_file_appointment_type);
                txtAppointmentType.setText(file.getAppointmentType());

                //get the batch Number
                TextView txtbatchNumber = (TextView)rootView.findViewById(R.id.Txt_file_batchNumber);
                txtbatchNumber.setText(file.getBatchRequestNumber());


                return rootView;

            }

            return null;

        }catch (Exception s)
        {
            Log.w(CLASS_NAME,s.getMessage());
            return null;
        }
    }
}
