package com.wadidejla.newscreens.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.degla.restful.models.RestfulFile;
import com.wadidejla.utils.RestfulTransferInfo;

import org.w3c.dom.Text;

import java.util.List;

import wadidejla.com.alfahresapp.R;

/**
 * Created by snouto on 08/06/15.
 */
public class NewViewUtils {




    public static ProgressDialog getWaitingDialog(Context context)
    {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle(R.string.main_files_alertDlg_Title);
        dialog.setMessage(context.getResources().getString(R.string.main_loading_title));
        dialog.setCancelable(false);
        return dialog;
    }


    public static View getTransferView(RestfulFile file,RestfulTransferInfo info,Context context)
    {
        try
        {
            LayoutInflater inflater = LayoutInflater.from(context);

            View transferView = inflater.inflate(R.layout.new_single_transfer_view,null,false);

            if(transferView == null) return null;

            //Coordinator Name
            TextView txtCoordinatorName = (TextView)transferView.findViewById(R.id.new_file_coordinatorName);
            txtCoordinatorName.setText(info.getCoordinatorName());

            //File Number
            TextView txtFileNumber = (TextView) transferView.findViewById(R.id.new_file_FileNumber);
            txtFileNumber.setText(file.getFileNumber());
            //Requesting Doc Name
            TextView txtDocName = (TextView)transferView.findViewById(R.id.new_file_RequestingDocName);
            txtDocName.setText(info.getClinicDocName());

            //Requesting Clinic Name
            TextView txtClinicName = (TextView)transferView.findViewById(R.id.new_file_RequestingClinic);
            txtClinicName.setText(info.getClinicName());

            //Requesting Clinic Code
            TextView txtClinicCode = (TextView)transferView.findViewById(R.id.new_file_RequestingClinicCode);
            txtClinicCode.setText(info.getClinicCode());

            return transferView;



        }catch (Exception s)
        {
            Log.e("Error",s.getMessage());
            return null;
        }
    }

    public static  ProgressDialog getDeterminateDialog(Context context)
    {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setTitle(R.string.main_files_alertDlg_Title);
        //dialog.setMessage(context.getResources().getString(R.string.main_loading_title));
        dialog.setCancelable(false);
        dialog.setIndeterminate(false);

        return dialog;
    }

    public static AlertDialog getAlertDialog(Context context, String title, String message)
    {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok !", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();


                    }
                })
                .setCancelable(false)
                .create();

        return dialog;
    }



    public static AlertDialog getChoiceDialog(Context context,
                                              String title , String message , final Runnable okFunc,
                                              final Runnable noFunc)
    {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if(okFunc != null)
                            okFunc.run();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();
                        if(noFunc != null)
                            noFunc.run();

                    }
                }).setCancelable(false)
                .create();

        return dialog;
    }
}
