package com.wadidejla.utils;

import java.util.List;

/**
 * Created by snouto on 27/05/15.
 */
public interface FilesManager {

    public boolean operateOnFile(String barcode);
    public int getCount();
    public String getOperatingTable();

    public List<FilesOnChangeListener> getFilesListener();
}
