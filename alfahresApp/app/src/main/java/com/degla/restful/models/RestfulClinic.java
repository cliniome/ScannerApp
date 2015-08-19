package com.degla.restful.models;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by snouto on 06/06/15.
 */
public class RestfulClinic implements Serializable {

    @Expose
    private String clinicName;
    @Expose
    private String clinicCode;
    @Expose
    private List<RestfulFile> files;

    private transient int selected;


    public RestfulClinic(){

        this.setFiles(new ArrayList<RestfulFile>());
    }


    @Override
    public boolean equals(Object o) {


        if(o instanceof  RestfulClinic)
        {
            if(((RestfulClinic)o).getClinicCode().equalsIgnoreCase(this.getClinicCode()))
                return true;
            else return false;

        }else return false;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String getClinicCode() {
        return clinicCode;
    }

    public void setClinicCode(String clinicCode) {
        this.clinicCode = clinicCode;
    }

    public List<RestfulFile> getFiles() {
        return files;
    }

    public void setFiles(List<RestfulFile> files) {
        this.files = files;
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }
}
