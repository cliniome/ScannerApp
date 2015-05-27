package com.degla.restful.models;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by snouto on 18/05/15.
 */
public class RestfulFile implements Serializable {


    @Expose
    private String fileNumber;
    @Expose
    private Date operationDate;
    @Expose
    private String description;
    @Expose
    private String state;
    @Expose
    private String cabinetId;
    @Expose
    private String shelfId;
    @Expose
    private String temporaryCabinetId;

    @Expose
    private String clinicName;
    @Expose
    private String clinicDocName;
    @Expose
    private String batchRequestNumber;

    private transient RestfulEmployee emp;


    public RestfulFile(){

        this.setEmp( new RestfulEmployee());

    }



    public String getFileNumber() {
        return fileNumber;
    }

    public void setFileNumber(String fileNumber) {
        this.fileNumber = fileNumber;
    }

    public Date getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(Date operationDate) {
        this.operationDate = operationDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getCabinetId() {
        return cabinetId;
    }

    public void setCabinetId(String cabinetId) {
        this.cabinetId = cabinetId;
    }

    public String getShelfId() {
        return shelfId;
    }

    public void setShelfId(String shelfId) {
        this.shelfId = shelfId;
    }

    public String getTemporaryCabinetId() {
        return temporaryCabinetId;
    }

    public void setTemporaryCabinetId(String temporaryCabinetId) {
        this.temporaryCabinetId = temporaryCabinetId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    /**
     * The first three digits refers to the cabin id
     * the last two digits , refer to the shelf id
     */
    public void prepare()
    {
        if(this.getFileNumber() != null && this.getFileNumber().length() > 0)
        {
            String cabinId = this.getFileNumber().substring(0,3);
            String shelfId = this.getFileNumber().substring(3,6);
            this.setCabinetId(cabinId);
            this.setShelfId(shelfId.substring(shelfId.length() - 1));
        }
    }


    @Override
    public boolean equals(Object o) {

        if(o instanceof RestfulFile)
        {
            RestfulFile passedFile = (RestfulFile)o;

            if(passedFile.getFileNumber().equals(this.getFileNumber()))
                return true;
            else return false;

        }

        return false;
    }

    public RestfulEmployee getEmp() {
        return emp;
    }

    public void setEmp(RestfulEmployee emp) {
        this.emp = emp;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String getClinicDocName() {
        return clinicDocName;
    }

    public void setClinicDocName(String clinicDocName) {
        this.clinicDocName = clinicDocName;
    }

    public String getBatchRequestNumber() {
        return batchRequestNumber;
    }

    public void setBatchRequestNumber(String batchRequestNumber) {
        this.batchRequestNumber = batchRequestNumber;
    }
}
