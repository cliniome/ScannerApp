package com.degla.restful.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by snouto on 18/05/15.
 */
public class RestfulFile implements Serializable {


    private String fileNumber;
    private Date operationDate;
    private String description;
    private String state;
    private String cabinetId;
    private String shelfId;
    private String temporaryCabinetId;




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
}
