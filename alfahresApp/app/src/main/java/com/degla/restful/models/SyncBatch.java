package com.degla.restful.models;
import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by snouto on 22/05/15.
 */
public class SyncBatch extends BooleanResult implements Serializable {

    @Expose
    private List<RestfulFile> files;
    @Expose
    private Long createdAt;



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

    public boolean containsFile(RestfulFile file)
    {
        boolean result = false;

        if(this.getFiles() == null || this.getFiles().size() <=0) return result;

        for(RestfulFile currentFile : this.getFiles())
        {
            if(currentFile.getFileNumber().equals(file.getFileNumber()))
            {
                result = true;
                break;
            }
        }


        return result;
    }


    public List<RestfulFile> getFiles() {
        return files;
    }

    public void setFiles(List<RestfulFile> files) {
        this.files = files;
    }


    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }
}
