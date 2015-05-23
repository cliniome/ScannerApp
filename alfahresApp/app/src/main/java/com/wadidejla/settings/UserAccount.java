package com.wadidejla.settings;

import android.util.Base64;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

/**
 * Created by snouto on 23/05/15.
 */
public class UserAccount implements Serializable {

    private String userName;
    private String password;

    public UserAccount(){}
    public UserAccount(String username , String password)
    {
        this.setUserName(username);
        this.setPassword(password);
    }

    public String getAuthorization() throws UnsupportedEncodingException
    {
        StringBuffer buff = new StringBuffer();
        buff.append(this.getUserName()).append(":").append(this.getPassword());

        String encoded = Base64.encodeToString(buff.toString().getBytes("UTF-8"),Base64.DEFAULT);

        return encoded;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
