package com.degla.restful.models;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by snouto on 18/05/15.
 */
public class RestfulRequest implements Serializable {


    @Expose
    private String appointment_Date;
    @Expose
    private String appointment_Type;
    @Expose
    private String fileNumber;
    @Expose
    private String patientName;
    @Expose
    private String patientNumber;
    @Expose
    private String userName;



    public String getAppointment_Date() {
        return appointment_Date;
    }

    public void setAppointment_Date(String appointment_Date) {
        this.appointment_Date = appointment_Date;
    }

    public String getAppointment_Type() {
        return appointment_Type;
    }

    public void setAppointment_Type(String appointment_Type) {
        this.appointment_Type = appointment_Type;
    }

    public String getFileNumber() {
        return fileNumber;
    }

    public void setFileNumber(String fileNumber) {
        this.fileNumber = fileNumber;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientNumber() {
        return patientNumber;
    }

    public void setPatientNumber(String patientNumber) {
        this.patientNumber = patientNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
