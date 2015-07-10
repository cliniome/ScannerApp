package com.wadidejla.utils;

import com.google.gson.annotations.Expose;

/**
 * Created by snouto on 10/07/15.
 */
public class RestfulTransferInfo {

    @Expose
    private String coordinatorName;
    @Expose
    private String clinicName;
    @Expose
    private String clinicCode;
    @Expose
    private String appointmentTime;
    @Expose
    private String appointmentDate;
    @Expose
    private String clinicDocName;
    @Expose
    private String clinicDocCode;

    public String getCoordinatorName() {
        return coordinatorName;
    }

    public void setCoordinatorName(String coordinatorName) {
        this.coordinatorName = coordinatorName;
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

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getClinicDocName() {
        return clinicDocName;
    }

    public void setClinicDocName(String clinicDocName) {
        this.clinicDocName = clinicDocName;
    }

    public String getClinicDocCode() {
        return clinicDocCode;
    }

    public void setClinicDocCode(String clinicDocCode) {
        this.clinicDocCode = clinicDocCode;
    }
}
