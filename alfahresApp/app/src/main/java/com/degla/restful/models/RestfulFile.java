package com.degla.restful.models;

import com.google.gson.annotations.Expose;
import com.wadidejla.newscreens.utils.BarcodeUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by snouto on 18/05/15.
 */
public class RestfulFile implements Serializable {


    public static final int READY_FILE = 1;
    public static final int NOT_READY_FILE = 0;

    @Expose
    private int appointmentId;
    @Expose
    private String fileNumber;
    @Expose
    private Long operationDate;

    @Expose
    private Long deviceOperationDate;
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
    private String clinicCode;

    @Expose
    private String clinicDocCode;
    @Expose
    private String batchRequestNumber;

    @Expose
    private String patientName;
    @Expose
    private String patientNumber;


    //New Requests
    @Expose
    private String appointmentType;
    @Expose
    private String appointmentDate;
    @Expose
    private String appointmentDateH;
    @Expose
    private String appointmentTime;
    @Expose
    private String appointmentMadeBy;

    @Expose
    private boolean processed;

    @Expose
    private boolean inpatient;

    private int readyFile;

    private int selected;

    //This property is used only in CheckFile Status Screen to show the last Employee
    //has seen that file
    private String lastEmployeeName;

    @Expose
    private boolean multipleClinics = false;

    @Expose
    private  RestfulEmployee emp;


    public RestfulFile(){

        this.setEmp( new RestfulEmployee());

    }


    public String getColumnId()
    {
        BarcodeUtils barcodeUtils  = new BarcodeUtils(this.getFileNumber());
        return barcodeUtils.getColumnNo();
    }

    public void toggleSelection()
    {
        int selected = this.getSelected();

        if(selected == 0)
            this.setSelected(1);
        else
            this.setSelected(0);
    }


    public boolean isReadyForSync()
    {
       /* if(this.getState() != null && (this.getState().equalsIgnoreCase(FileModelStates.MISSING.toString()) ||
        this.getState().equalsIgnoreCase(FileModelStates.OUT_OF_CABIN.toString()) ||
        this.getState().equalsIgnoreCase(FileModelStates.COORDINATOR_IN.toString()) ||
                this.isMultipleClinics() ||
                this.isInpatient() ||
        this.getState().equalsIgnoreCase(FileModelStates.DISTRIBUTED.toString())))
            return true;
        else
        {
            return(this.getEmp() != null && this.getEmp().getUserName() != null
                    &&
                    this.getTemporaryCabinetId() != null && this.getTemporaryCabinetId().length() > 0);
        }*/

        return ((this.getReadyFile() > 0) ? true : false);
    }


    public String getFileNumber() {
        return fileNumber;
    }

    public void setFileNumber(String fileNumber) {
        this.fileNumber = fileNumber;
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
            BarcodeUtils barcodeUtils  = new BarcodeUtils(this.getFileNumber());

            String cabinId = barcodeUtils.getCabinID();
            String columnId = barcodeUtils.getColumnNo();
            this.setCabinetId(cabinId);
            this.setShelfId(this.getShelfId());
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


    public Long getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(Long operationDate) {
        this.operationDate = operationDate;
    }

    public String getAppointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(String appointmentType) {
        this.appointmentType = appointmentType;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentDateH() {
        return appointmentDateH;
    }

    public void setAppointmentDateH(String appointmentDateH) {
        this.appointmentDateH = appointmentDateH;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getAppointmentMadeBy() {
        return appointmentMadeBy;
    }

    public void setAppointmentMadeBy(String appointmentMadeBy) {
        this.appointmentMadeBy = appointmentMadeBy;
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

    public String getClinicCode() {
        return clinicCode;
    }

    public void setClinicCode(String clinicCode) {
        this.clinicCode = clinicCode;
    }

    public String getClinicDocCode() {
        return clinicDocCode;
    }

    public void setClinicDocCode(String clinicDocCode) {
        this.clinicDocCode = clinicDocCode;
    }

    public int getReadyFile() {
        return readyFile;
    }

    public void setReadyFile(int readyFile) {
        this.readyFile = readyFile;
    }


    public boolean isMultipleClinics() {
        return multipleClinics;
    }

    public void setMultipleClinics(boolean multipleClinics) {
        this.multipleClinics = multipleClinics;
    }


    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public boolean isInpatient() {
        return inpatient;
    }

    public void setInpatient(boolean inpatient) {
        this.inpatient = inpatient;
    }

    public String getLastEmployeeName() {
        return lastEmployeeName;
    }

    public void setLastEmployeeName(String lastEmployeeName) {
        this.lastEmployeeName = lastEmployeeName;
    }

    public Long getDeviceOperationDate() {

        if(deviceOperationDate == null) return Long.valueOf(-1);
        return deviceOperationDate;
    }

    public void setDeviceOperationDate(Long deviceOperationDate) {
        this.deviceOperationDate = deviceOperationDate;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

}
