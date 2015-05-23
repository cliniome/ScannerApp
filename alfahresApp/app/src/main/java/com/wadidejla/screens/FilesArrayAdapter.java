package com.wadidejla.screens;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.degla.restful.models.RestfulFile;

import java.util.List;

import wadidejla.com.alfahresapp.R;

/**
 * Created by snouto on 23/05/15.
 */
public class FilesArrayAdapter extends ArrayAdapter<RestfulFile> {

    private List<RestfulFile> files;

    public FilesArrayAdapter(Context context , int resource , List<RestfulFile> availableFiles)
    {
        super(context,resource,availableFiles);
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
        RestfulFile file = this.getFiles().get(position);

        //get the file id TextView
        TextView txtFileId = (TextView) rootView.findViewById(R.id.txt_fileNo);

        txtFileId.setText(file.getFileNumber());

        //get the  cabinet id
        TextView txtCabinId = (TextView) rootView.findViewById(R.id.txtcabinNo);

        txtCabinId.setText(file.getCabinetId());

        //get the shelf id
        TextView txtShelfId = (TextView) rootView.findViewById(R.id.txtshelfId);

        txtShelfId.setText(file.getShelfId());



        return rootView;

    }

    public List<RestfulFile> getFiles() {
        return files;
    }

    public void setFiles(List<RestfulFile> files) {
        this.files = files;
    }
}
