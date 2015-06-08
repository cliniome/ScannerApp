package com.wadidejla.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.degla.restful.models.FileModelStates;
import com.degla.restful.models.RestfulFile;

import org.w3c.dom.Text;

import wadidejla.com.alfahresapp.R;

/**
 * Created by snouto on 30/05/15.
 */
public class ViewUtils {

    private static final String CLASS_NAME = "ViewUtils";
    private static boolean choice = false;



    public static View getDetailsTitleViewFor(RestfulFile file ,Context context)
    {
        try
        {
            LayoutInflater inflater = LayoutInflater.from(context);

            View rootView = inflater.inflate(R.layout.custom_file_title,null,false);

            if(rootView != null)
            {
                //set the image for the title according to the state of the file
                //if file has readyFile = 1 , it means it is complete
                //otherwise , it is incomplete, under preview
                //then , check if the file is missing or not
                ImageView titleImg = (ImageView)rootView.findViewById(R.id.fileimgtitle);

                if (file.getState() != null && file.getState().equalsIgnoreCase(FileModelStates.MISSING.toString()))
                {
                    //that means the file is missing so set the imageview to missing drawable from the resources folder
                    titleImg.setImageDrawable(context.getResources().getDrawable(R.drawable.missing));

                }else if (file.getReadyFile() != 0) // that means the file is ready
                {
                    //show the complete drawable
                    titleImg.setImageDrawable(context.getResources().getDrawable(R.drawable.complete));
                }else
                {
                    //it means the file is not ready at all , so display the preview icon
                    titleImg.setImageDrawable(context.getResources().getDrawable(R.drawable.preview));
                }

                //now set the file number
                TextView fileTxt = (TextView)rootView.findViewById(R.id.txt_filetitle);
                fileTxt.setTypeface(null, Typeface.BOLD);

                fileTxt.setText(file.getFileNumber());

            }

            return rootView;

        }catch (Exception s)
        {
            s.printStackTrace();
            return null;
        }
    }


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

                //Get the clinic name
                TextView txtClinicName = (TextView)rootView.findViewById(R.id.Txt_file_clinicName);
                txtClinicName.setText(file.getClinicName());

                //get The clinic Code
                TextView txtClinicCode = (TextView)rootView.findViewById(R.id.Txt_file_clinicCode);
                txtClinicCode.setText(file.getClinicCode());

                //Get the Doc Name
                TextView txtDocName = (TextView)rootView.findViewById(R.id.Txt_file_docName);
                txtDocName.setText(file.getClinicDocName());

                //Get the doc Code
                TextView txtDocCode = (TextView)rootView.findViewById(R.id.Txt_file_docCode);
                txtDocCode.setText(file.getClinicDocCode());


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
