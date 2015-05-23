package com.degla.restful.models;


import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by snouto on 18/05/15.
 */
public class BooleanResult implements Serializable {


    @Expose
    private boolean state;
    @Expose
    private String message;

    public BooleanResult(){}

    public BooleanResult(boolean state, String message)
    {
        this.setState(state);
        this.setMessage(message);
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
