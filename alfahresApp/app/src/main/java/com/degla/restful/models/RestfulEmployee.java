package com.degla.restful.models;

import com.google.gson.annotations.Expose;
import com.wadidejla.utils.RoleTypes;

import java.io.Serializable;

/**
 * Created by snouto on 20/05/15.
 */
public class RestfulEmployee implements Serializable {


    @Expose
    private String firstName;
    @Expose
    private String lastName;
    @Expose
    private String password;
    @Expose
    private String userName;
    @Expose
    private String role;
    @Expose
    private int id;

    public boolean isKeeper()
    {
        if(this.role != null && this.getRole().equalsIgnoreCase(RoleTypes.KEEPER.toString()))
            return true;
        else return false;
    }

    public boolean isReceptionist()
    {
        if(this.role != null && this.getRole().equalsIgnoreCase(RoleTypes.RECEPTIONIST.toString()))
            return true;
        else return false;
    }

    public boolean isA(String role)
    {
        if(this.role != null && this.getRole().equalsIgnoreCase(role))
            return true;
        else return false;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
