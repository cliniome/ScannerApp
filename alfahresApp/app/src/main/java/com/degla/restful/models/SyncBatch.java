package com.degla.restful.models;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by snouto on 22/05/15.
 */
public class SyncBatch extends BooleanResult implements Serializable {

    private List<RestfulFile> files;
    private Date createdAt;



    public SyncBatch(){}

    public SyncBatch(List<RestfulFile> files)
    {
        this.setFiles(files);
    }



    public boolean loaded()
    {
        if (files == null || files.size() <=0)
            return false;
        else return true;
    }


    public List<RestfulFile> getFiles() {
        return files;
    }

    public void setFiles(List<RestfulFile> files) {
        this.files = files;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
