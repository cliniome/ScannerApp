package com.wadidejla.screens;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.degla.restful.models.RestfulFile;
import com.wadidejla.listeners.KeeperOnClickListener;

import java.util.HashMap;
import java.util.List;

import wadidejla.com.alfahresapp.R;

/**
 * Created by snouto on 06/06/15.
 */
public class CoordinatorExpandableAdapter extends BaseExpandableListAdapter {


    private Context context;
    private List<String> mainCategories;
    private HashMap<String,List<RestfulFile>> categorizedData;

    private KeeperOnClickListener<BaseExpandableListAdapter> listener;


    public CoordinatorExpandableAdapter(Context ctx , List<String> categories ,
                                        HashMap<String,List<RestfulFile>> categorizedData)
    {
        this.setContext(ctx);
        this.setMainCategories(categories);
        this.setCategorizedData(categorizedData);

    }


    public boolean removeFile(RestfulFile file)
    {
        List<RestfulFile> containedList = null;

        for(String category : categorizedData.keySet())
        {
            List<RestfulFile> list = categorizedData.get(category);

            if(list != null && list.size() > 0)
            {
                for(RestfulFile currentFile : list)
                {
                    if(currentFile.getFileNumber().equalsIgnoreCase(file.getFileNumber()))
                    {
                        containedList = list;
                        break;
                    }
                }
            }
        }

        if(containedList != null)
            containedList.remove(file);

        //then notify
        this.notifyDataSetChanged();

        return true;
    }


    @Override
    public int getGroupCount() {
        return mainCategories.size();
    }

    @Override
    public int getChildrenCount(int parent) {
        return categorizedData.get(mainCategories.get(parent)).size();
    }

    @Override
    public Object getGroup(int position) {
        return mainCategories.get(position);
    }

    @Override
    public Object getChild(int parent, int child) {
        return categorizedData.get(mainCategories.get(parent)).get(child);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int parent, int child) {
        return child;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int parent, boolean lastChild, View convertView, ViewGroup viewGroup) {

        if(convertView == null)
        {
            LayoutInflater inflater = LayoutInflater.from(this.getContext());

            convertView = inflater.inflate(R.layout.coordinator_list_parent_layout,viewGroup,false);
        }

        //Get the text view to set the group title
        String groupTitle = (String) getGroup(parent);

        TextView groupView = (TextView)convertView.findViewById(R.id.coordinator_parent_item_txt);
        groupView.setTypeface(null, Typeface.BOLD);
        groupView.setText(groupTitle);

        return convertView;
    }

    @Override
    public View getChildView(int parent, int child, boolean lastChild, View convertView, ViewGroup viewGroup) {

        if(convertView == null)
        {
            LayoutInflater inflater = LayoutInflater.from(this.getContext());

            convertView = inflater.inflate(R.layout.coordinator_list_child_layout,viewGroup,false);
        }

        //get the child object which is a restful File
        RestfulFile file = (RestfulFile) getChild(parent,child);

        View rootView = convertView;
        rootView.setTag(file);

        //get the file id TextView
        TextView txtFileId = (TextView) rootView.findViewById(R.id.txt_fileNo);

        txtFileId.setText(file.getFileNumber());

        //get the  cabinet id
        TextView txtCabinId = (TextView) rootView.findViewById(R.id.txtcabinNo);

        txtCabinId.setText(file.getCabinetId());

        //get the shelf id
        TextView txtShelfId = (TextView) rootView.findViewById(R.id.txtshelfId);

        txtShelfId.setText(file.getShelfId());

        if(getListener() != null)
            rootView.setOnLongClickListener(getListener());


        //finally return the convert View
        return convertView;

    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }




    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<String> getMainCategories() {
        return mainCategories;
    }

    public void setMainCategories(List<String> mainCategories) {
        this.mainCategories = mainCategories;
    }

    public HashMap<String, List<RestfulFile>> getCategorizedData() {
        return categorizedData;
    }

    public void setCategorizedData(HashMap<String, List<RestfulFile>> categorizedData) {
        this.categorizedData = categorizedData;
    }


    public KeeperOnClickListener<BaseExpandableListAdapter> getListener() {
        return listener;
    }

    public void setListener(KeeperOnClickListener<BaseExpandableListAdapter> listener) {
        this.listener = listener;
    }
}
