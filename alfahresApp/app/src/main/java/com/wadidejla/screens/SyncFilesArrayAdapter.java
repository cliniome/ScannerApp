package com.wadidejla.screens;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.degla.restful.models.FileModelStates;
import com.degla.restful.models.RestfulFile;
import com.wadidejla.settings.SystemSettingsManager;
import com.wadidejla.utils.FilesManager;
import com.wadidejla.utils.FilesUtils;

import java.util.List;

import wadidejla.com.alfahresapp.R;

/**
 * Created by snouto on 27/05/15.
 */
class SyncFilesArrayAdapter extends ArrayAdapter<RestfulFile> {

    private List<RestfulFile> files;
    private static final String[] items = {"Mark it as New","Show File Details"};

    public SyncFilesArrayAdapter(Context context , int resource , List<RestfulFile> localFiles)
    {
        super(context,resource,localFiles);
        FilesUtils.prepareFiles(localFiles);
        this.setFiles(localFiles);
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

        if(file != null && file.getState().equals(FileModelStates.MISSING.toString()))
        {
            ImageView imgView = (ImageView)rootView.findViewById(R.id.missing_file_img);

            imgView.setImageResource(R.drawable.missing);
        }

        //get the file id TextView
        TextView txtFileId = (TextView) rootView.findViewById(R.id.txt_fileNo);

        txtFileId.setText(file.getFileNumber());

        //get the  cabinet id
        TextView txtCabinId = (TextView) rootView.findViewById(R.id.txtcabinNo);

        txtCabinId.setText(file.getCabinetId());

        //get the shelf id
        TextView txtShelfId = (TextView) rootView.findViewById(R.id.txtshelfId);

        txtShelfId.setText(file.getShelfId());

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
                                    FilesManager filesManager = SystemSettingsManager.createInstance(getContext())
                                            .getSyncFilesManager();

                                    file.setState("");
                                    filesManager.getFilesDBManager().deleteFile(file.getFileNumber());
                                    SyncFilesArrayAdapter.this.getFiles().remove(file);
                                    SystemSettingsManager.createInstance(getContext()).getAvailableFiles().add(file);

                                    Uri ringToneUri = Uri.parse("android.resource://wadidejla.com.alfahresapp/"+R.raw.marked);

                                    Ringtone ringtone = RingtoneManager.getRingtone(getContext(), ringToneUri);
                                    ringtone.play();

                                    SyncFilesArrayAdapter.this.notifyDataSetChanged();

                                    dialogInterface.dismiss();

                                } else {
                                    //show details of the selected file
                                    //TODO : add show details window in here to show the details of the selected file.
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
