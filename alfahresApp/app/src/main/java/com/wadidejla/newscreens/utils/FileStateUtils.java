package com.wadidejla.newscreens.utils;

import com.degla.restful.models.FileModelStates;

import java.io.Serializable;

/**
 * Created by snouto on 28/07/15.
 */
public class FileStateUtils implements Serializable {

    private FileModelStates state;

    private String[] states = {"Archived","Checked Out","Received by Coordinator","Sent out by Coordinator",
            "Received by Clinic","Received by Keeper","New File","Received by Receptionist","Prepared by Keeper",
            "Transferred by coordinator","Missing"};

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


        if(readableState.toLowerCase().equals("Archived".toLowerCase()))
            state = FileModelStates.CHECKED_IN;
        else if (readableState.toLowerCase().equals("Checked Out".toLowerCase()))
            state = FileModelStates.CHECKED_OUT;
        else if (readableState.toLowerCase().equals("Received by Coordinator".toLowerCase()))
            state = FileModelStates.COORDINATOR_IN;
        else if (readableState.toLowerCase().equals("Sent out by Coordinator".toLowerCase()))
            state = FileModelStates.COORDINATOR_OUT;
        else if (readableState.toLowerCase().equals("Received by Clinic".toLowerCase()))
            state = FileModelStates.DISTRIBUTED;
        else if (readableState.toLowerCase().equals("Received by Keeper".toLowerCase()))
            state = FileModelStates.KEEPER_IN;
        else if (readableState.toLowerCase().equals("New File".toLowerCase()))
            state = FileModelStates.NEW;
        else if (readableState.toLowerCase().equals("Received by Receptionist".toLowerCase()))
            state = FileModelStates.RECEPTIONIST_IN;
        else if (readableState.toLowerCase().equals("Sent out by Receptionist".toLowerCase()))
            state = FileModelStates.RECEPTIONIST_OUT;
        else if (readableState.toLowerCase().equals("Prepared by Keeper".toLowerCase()))
            state = FileModelStates.OUT_OF_CABIN;
        else if (readableState.toLowerCase().equals("Transferred by coordinator".toLowerCase()))
            state = FileModelStates.TRANSFERRED;


        return state;
    }


    public String getReadableState(String state)
    {
        String readableState = "Missing";

        setState(FileModelStates.valueOf(state));

        switch (getState())
        {
            case CHECKED_IN:
                return "Archived";
            case CHECKED_OUT:
                return "Checked Out";
            case COORDINATOR_IN:
                return "Received by Coordinator";
            case COORDINATOR_OUT:
                return "Sent out by Coordinator";
            case DISTRIBUTED:
                return "Received by Clinic";
            case KEEPER_IN:
                return "Received by Keeper";
            case NEW:
                return "New File";
            case RECEPTIONIST_IN:
                return "Received by Receptionist";
            case RECEPTIONIST_OUT:
                return "Sent out by Receptionist";
            case OUT_OF_CABIN:
                return "Prepared by Keeper";
            case TRANSFERRED:
                return "Transferred by coordinator";
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

