package com.degla.restful.models.http;

import java.io.Serializable;

/**
 * Created by snouto on 22/05/15.
 */
public class Parameter implements Serializable {

    private String name;
    private Object value;

    public Parameter(){}

    public Parameter(String name , Object value)
    {
        this.setName(name);
        this.setValue(value);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
