package com.wadidejla.screens;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.degla.restful.models.FileModelStates;
import com.degla.restful.models.RestfulFile;
import com.wadidejla.settings.SystemSettingsManager;
import com.wadidejla.utils.FilesManager;
import com.wadidejla.utils.FilesUtils;
import com.wadidejla.utils.ViewUtils;

import java.util.List;

import wadidejla.com.alfahresapp.R;


/**
 * Created by snouto on 23/05/15.
 */
public class FilesArrayAdapter extends ArrayAdapter<RestfulFile> {

    private List<RestfulFile> files;
    private static final String[] items = {"Mark File as Missing","Show File Details"};

    public FilesArrayAdapter(Context context , int resource , List<RestfulFile> availableFiles)
    {
        super(context,resource,availableFiles);

        if(availableFiles != null && availableFiles.size() > 0)
            FilesUtils.prepareFiles(availableFiles);
        this.files = availableFiles;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View rootView = convertView;

        if(rootView == null)
        {
            rootView = inflater.inflate(R.layout.single_file_view,parent,false);

        }

        //get the current Restful File to bind to
        final RestfulFile file = this.getFiles().get(position);

        //get the file id TextView
        TextView txtFileId = (TextView) rootView.findViewById(R.id.txt_fileNo);

        txtFileId.setText(file.getFileNumber());

        //get the  cabinet id
        TextView txtCabinId = (TextView) rootView.findViewById(R.id.txtcabinNo);

        txtCabinId.setText(file.getCabinetId());

        //get the shelf id
        TextView txtShelfId = (TextView) rootView.findViewById(R.id.txtshelfId);

        txtShelfId.setText(file.getShelfId());

        ImageView imgView = (ImageView)rootView.findViewById(R.id.missing_file_img);

        if (file.getState() != null && file.getState().equalsIgnoreCase(FileModelStates.MISSING.toString()))
        {
            //that means the file is missing so set the imageview to missing drawable from the resources folder
            imgView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.missing));

        }else if (file.getReadyFile() != 0 && file.getTemporaryCabinetId() != null &&
                file.getTemporaryCabinetId().length() >= 0) // that means the file is ready
        {
            //show the complete drawable
            imgView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.complete));
        }else
        {
            //it means the file is not ready at all , so display the preview icon
            imgView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.preview));
        }

        //attach a long click listener to the current view
        rootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {


                final AlertDialog choiceDlg = new AlertDialog.Builder(getContext())
                        .setTitle(R.string.SINGLE_CHOICE_DLG_TITLE)
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if (i == 0) // that means mark file as missing
                                {
                                    //access the files Manager from the settings
                                    try {
                                        FilesManager filesManager = SystemSettingsManager.createInstance(getContext())
                                                .getSyncFilesManager();

                                        file.setState(FileModelStates.MISSING.toString());
                                        filesManager.operateOnFile(file,
                                                SystemSettingsManager.createInstance(getContext()).getAccount());

                                    } catch (Exception s) {
                                        Log.w("FilesArrayAdapter", s.getMessage());

                                    } finally {

                                        dialogInterface.dismiss();
                                    }


                                } else {
                                    //show details of the selected file
                                    final AlertDialog detailsDialog = new AlertDialog.Builder(getContext())
                                            .setCustomTitle(ViewUtils.getDetailsTitleViewFor(file, getContext()))
                                            .setView(ViewUtils.getDetailsViewFor(file, getContext()))
                                            .setPositiveButton("Ok.", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            }).create();

                                    dialogInterface.dismiss();
                                    detailsDialog.show();
                                }

                            }
                        }).create();

                choiceDlg.show();


                return true;
            }
        });

        return rootView;

    }

    public List<RestfulFile> getFiles() {
        return files;
    }

    public void setFiles(List<RestfulFile> files) {
        this.files = files;
    }
}
