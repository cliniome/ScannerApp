package com.wadidejla.network;

import com.degla.restful.models.http.HttpResponse;
import com.degla.restful.models.http.Parameter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by snouto on 22/05/15.
 */
public class AlfahresConnection {


    private String hostName;
    private String port="8080";
    private String basePath="alfahres";
    private String restfulPath = "rest";
    private StringBuffer appendablePath;
    private List<Parameter> headers;
    private String methodType;

    public AlfahresConnection(String hostName,String port , String basePath,String restfulPath){

        this.setHostName(hostName);
        this.setPort(port);
        this.setBasePath(basePath);
        this.setRestfulPath(restfulPath);

    }

    private String getCompleteUrl()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append("http://").append(hostName).append(":")
                .append(port).append("/").append(basePath)
                .append("/").append(restfulPath).append("/");

        return buffer.toString();
    }


    public AlfahresConnection path(String path)
    {
        if(appendablePath == null)
        {
            //that means the string buffer is empty
            //so initialize it and append the complete Url to it
            appendablePath = new StringBuffer();
            appendablePath.append(getCompleteUrl());
        }

        appendablePath.append(path);

        return this;
    }

    public AlfahresConnection setHeaders(Parameter[] headers)
    {
        if(this.headers == null)
            this.headers = new ArrayList<Parameter>();

        for(Parameter param : headers)
        {
            this.headers.add(param);
        }

        return this;
    }

    public AlfahresConnection addHeader(Parameter header)
    {
        if(this.headers == null)
            this.headers = new ArrayList<Parameter>();

        this.headers.add(header);

        return this;

    }

    public AlfahresConnection method(String type)
    {
        this.setMethodType(type);
        return this;

    }


    public HttpResponse call()
    {
        try
        {

            return null;

        }catch (Exception s)
        {
            s.printStackTrace();
            return null;
        }
    }








    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getRestfulPath() {
        return restfulPath;
    }

    public void setRestfulPath(String restfulPath) {
        this.restfulPath = restfulPath;
    }

    public String getMethodType() {
        return methodType;
    }

    public void setMethodType(String methodType) {
        this.methodType = methodType;
    }
}
