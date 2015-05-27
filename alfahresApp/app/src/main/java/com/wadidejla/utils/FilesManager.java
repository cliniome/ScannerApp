package com.wadidejla.utils;

import com.degla.restful.models.RestfulFile;

import java.util.List;

/**
 * Created by snouto on 27/05/15.
 */
public interface FilesManager {

    public boolean operateOnFile(String barcode,String state);
    public int getCount();
    public String getOperatingTable();
    public boolean operateOnFile(RestfulFile file);
    public List<FilesOnChangeListener> getFilesListener();
}
