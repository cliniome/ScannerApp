package com.degla.restful.models;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by snouto on 30/07/15.
 */
public class FileBatchDetails {


    @Expose
    private RestfulFile file;


    @Expose
    private String employeeName;


    public FileBatchDetails(RestfulFile file , String empName)
    {
        this.setFile(file);
        this.setEmployeeName(empName);
    }

    public FileBatchDetails(){}



    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public RestfulFile getFile() {
        return file;
    }

    public void setFile(RestfulFile file) {
        this.file = file;
    }
}

