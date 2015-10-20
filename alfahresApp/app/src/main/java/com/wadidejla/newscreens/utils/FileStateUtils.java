package com.wadidejla.newscreens.utils;

import com.degla.restful.models.FileModelStates;

import java.io.Serializable;

/**
 * Created by snouto on 28/07/15.
 */
public class FileStateUtils implements Serializable {

    private FileModelStates state;

    private String[] states = {"Stored in Cabin","Checked Out","Received by Coordinator","Sent out by Coordinator",
            "Received by Clinic","Received by Receptionist","Prepared by Clerk",
            "Transferred by coordinator","Missing","Processing Coordinator","Analysis Coordinator","Incomplete Coordinator","Coding Coordinator",
    "Temporary Stored","InPatient Completed"};

    public FileStateUtils(FileModelStates state) {
        this.setState(state);
    }
    //The default constructor
    public FileStateUtils(){}


    public FileModelStates getState(String readableState)
    {
        FileModelStates state = FileModelStates.MISSING;

        if(readableState == null || readableState.length() <=0)
            return state;


        if(readableState.toLowerCase().equals("Stored In Cabin".toLowerCase()))
            state = FileModelStates.CHECKED_IN;
        else if (readableState.toLowerCase().equals("Checked Out".toLowerCase()))
            state = FileModelStates.CHECKED_OUT;
        else if (readableState.toLowerCase().equals("Received by Coordinator".toLowerCase()))
            state = FileModelStates.COORDINATOR_IN;
        else if (readableState.toLowerCase().equals("Sent out by Coordinator".toLowerCase()))
            state = FileModelStates.COORDINATOR_OUT;
        else if (readableState.toLowerCase().equals("Distributed To Clinic".toLowerCase()))
            state = FileModelStates.DISTRIBUTED;
        else if (readableState.toLowerCase().equals("New File".toLowerCase()))
            state = FileModelStates.NEW;
        else if (readableState.toLowerCase().equals("Received by Receptionist".toLowerCase()))
            state = FileModelStates.RECEPTIONIST_IN;
        else if (readableState.toLowerCase().equals("Sent out by Receptionist".toLowerCase()))
            state = FileModelStates.RECEPTIONIST_OUT;
        else if (readableState.toLowerCase().equals("Prepared by Clerk".toLowerCase()))
            state = FileModelStates.OUT_OF_CABIN;
        else if (readableState.toLowerCase().equals("Transferred by coordinator".toLowerCase()))
            state = FileModelStates.TRANSFERRED;
        else if (readableState.toLowerCase().equals("Analysis Coordinator".toLowerCase()))
            state = FileModelStates.ANALYSIS_COORDINATOR;
        else if (readableState.toLowerCase().equals("Processing Coordinator".toLowerCase()))
            state = FileModelStates.PROCESSING_COORDINATOR;
        else if (readableState.toLowerCase().equals("Incomplete Coordinator".toLowerCase()))
            state = FileModelStates.INCOMPLETE_COORDINATOR;
        else if (readableState.toLowerCase().equals("Coding Coordinator".toLowerCase()))
            state = FileModelStates.CODING_COORDINATOR;
        else if (readableState.toLowerCase().equals("Temporary Stored".toLowerCase()))
            state = FileModelStates.TEMPORARY_STORED;
        else if (readableState.toLowerCase().equals("InPatient Completed".toLowerCase()))
            state = FileModelStates.INPATIENT_COMPLETED;









        return state;
    }


    public String getReadableState(String state)
    {
        String readableState = "Missing";

        setState(FileModelStates.valueOf(state));

        switch (getState())
        {
            case CHECKED_IN:
                return "Stored In Cabin";
            case CHECKED_OUT:
                return "Checked Out";
            case COORDINATOR_IN:
                return "Received by Coordinator";
            case COORDINATOR_OUT:
                return "Sent out by Coordinator";
            case DISTRIBUTED:
                return "Distributed To Clinic";
            case NEW:
                return "New File";
            case RECEPTIONIST_IN:
                return "Received by Receptionist";
            case RECEPTIONIST_OUT:
                return "Sent out by Receptionist";
            case OUT_OF_CABIN:
                return "Prepared by Clerk";
            case TRANSFERRED:
                return "Transferred by coordinator";
            case ANALYSIS_COORDINATOR:
                return "Analysis Coordinator";
            case INCOMPLETE_COORDINATOR:
                return "Incomplete Coordinator";
            case CODING_COORDINATOR:
                return "Coding Coordinator";
            case PROCESSING_COORDINATOR:
                return "Processing Coordinator";
            case TEMPORARY_STORED:
                return "Temporary Stored";
            case INPATIENT_COMPLETED:
                return "InPatient Completed";
            default:
                return readableState;
        }
    }

    public String[] getStates() {
        return states;
    }

    public void setStates(String[] states) {
        this.states = states;
    }

    public FileModelStates getState() {
        return state;
    }

    public void setState(FileModelStates state) {
        this.state = state;
    }
}

