package com.wadidejla.utils;

import com.degla.restful.models.CollectionBatch;
import com.degla.restful.models.RestfulEmployee;
import com.degla.restful.models.RestfulFile;
import com.wadidejla.db.FilesDBManager;

import java.util.Collection;
import java.util.List;

/**
 * Created by snouto on 27/05/15.
 */
public interface FilesManager {

    public boolean operateOnFile(String barcode,String state);
    public int getCount();
    public String getOperatingTable();
    public boolean operateOnFile(RestfulFile file,RestfulEmployee emp);
    public List<FilesOnChangeListener> getFilesListener();
    public boolean operateOnFiles(int flag,RestfulEmployee emp);
    public FilesDBManager getFilesDBManager();
    public CollectionBatch getCoordinatorFiles(int flag,RestfulEmployee emp);
}
