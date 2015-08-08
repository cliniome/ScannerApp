package com.wadidejla.newscreens.utils;

import android.widget.ExpandableListView;

import com.degla.restful.models.RestfulFile;

import java.util.HashMap;
import java.util.List;

/**
 * Created by snouto on 09/08/15.
 */
public interface IExpandableAdapter {

    public List<String> getMain_Categories();
    public HashMap<String,List<RestfulFile>> getMainData();

    public void setListView(ExpandableListView listView);

    public ExpandableListView getExpandableList();
}
