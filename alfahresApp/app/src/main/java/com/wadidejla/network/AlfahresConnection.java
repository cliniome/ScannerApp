package com.wadidejla.network;

import android.util.Base64;
import android.util.Log;

import com.degla.restful.models.http.HttpResponse;
import com.degla.restful.models.http.Parameter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wadidejla.utils.AlfahresJsonBuilder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by snouto on 22/05/15.
 */
public class AlfahresConnection {


    public static final String GET_HTTP_METHOD="GET";
    public static final String POST_HTTP_METHOD="POST";
    public static final String DEFAULT_PORT = "8080";
    public static final String DEFAULT_BASEPATH="alfahres";
    public static final String DEFAULT_RESTFUL_PATH="rest";
    private String hostName="192.168.1.7";
    private String port="8080";
    private String basePath="alfahres";
    private String restfulPath = "rest";
    private StringBuffer appendablePath;
    private List<Parameter> headers;
    private String methodType = "GET";
    private HttpURLConnection connection;
    private Object dataPayload;

    public AlfahresConnection(String hostName,String port , String basePath,String restfulPath){

        this.setHostName(hostName);
        this.setPort(port);
        this.setBasePath(basePath);
        this.setRestfulPath(restfulPath);

    }

    public AlfahresConnection(){}

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

    private void addHeaders()
    {
        if(this.headers != null && this.headers.size() > 0)
        {
            for(Parameter param : this.headers)
            {
                connection.setRequestProperty(param.getName(),param.getValue().toString());
            }
        }
    }

    public AlfahresConnection method(String type)
    {
        this.setMethodType(type);
        return this;

    }

    public AlfahresConnection setAuthorization(String userName , String password)
    {
        Parameter param = new Parameter("Authorization",setAuth(userName,password));

        this.addHeader(param);

        return this;
    }

    private Object setAuth(String userName, String password) {

       try
       {
           String combined = String.format("%s:%s",userName,password);

           String encoded = Base64.encodeToString(combined.getBytes("UTF-8"),Base64.DEFAULT);


           StringBuffer buff = new StringBuffer();
           buff.append("Basic").append(" ").append(encoded);

           return buff.toString();

       }catch (Exception s)
       {
           Log.e("AlfahresConnection",s.getMessage());

           return "";

       }


    }

    public AlfahresConnection setBody(Object data)
    {
        this.dataPayload = data;
        return this;
    }


    public HttpResponse call(Class<?> entity)
    {
        HttpResponse response = new HttpResponse();

        try
        {
            URL connectionUrl = new URL(appendablePath.toString());

            connection = (HttpURLConnection)connectionUrl.openConnection();

            connection.setRequestMethod(getMethodType());


            //Add the body payload

            if(this.dataPayload != null)
            {
                //Add the content-type flag
                this.addHeader(new Parameter("content-type","application/json;charset=UTF-8"));
                this.addHeaders();
                //convert the payload into json string
                Gson gson = AlfahresJsonBuilder.createGson();

                String jsonPayload = gson.toJson(this.dataPayload);

                OutputStream outputStream = connection.getOutputStream();

                byte[] binaryPayload = jsonPayload.getBytes("UTF-8");

                outputStream.write(binaryPayload);

            }else
            this.addHeaders();


            int responseCode = connection.getResponseCode();

            response.setResponseCode(String.valueOf(responseCode));

            if(responseCode == HttpURLConnection.HTTP_OK) {
                    //parse the response Stream
                    Object result = this.parseResponseStream(connection.getInputStream(),entity);

                    response.setPayload(result);

            }else
            {
                response.setPayload(connection.getResponseMessage());
            }

            connection.disconnect();

            this.clearInstance();

            return response;


        }catch (Exception s)
        {
            s.printStackTrace();
            return null;
        }
    }




    public HttpResponse call(Type entity)
    {
        HttpResponse response = new HttpResponse();

        try
        {
            URL connectionUrl = new URL(appendablePath.toString());

            connection = (HttpURLConnection)connectionUrl.openConnection();

            connection.setRequestMethod(getMethodType());
            //Add the body payload

            if(this.dataPayload != null)
            {
                //Add the content-type flag
                this.addHeader(new Parameter("Content-Type","application/json"));
                //End of the body payload
                this.addHeaders();
                //convert the payload into json string
                Gson gson = AlfahresJsonBuilder.createGson();

                String jsonPayload = gson.toJson(this.dataPayload);

                OutputStream outputStream = connection.getOutputStream();

                byte[] binaryPayload = jsonPayload.getBytes("UTF-8");

                outputStream.write(binaryPayload);


            }else

             this.addHeaders();


            int responseCode = connection.getResponseCode();

            response.setResponseCode(String.valueOf(responseCode));

            if(responseCode == HttpURLConnection.HTTP_OK) {
                //parse the response Stream
                Object result = this.parseResponseStream(connection.getInputStream(),entity);

                response.setPayload(result);

            }else
            {
                response.setPayload(connection.getResponseMessage());
            }

            connection.disconnect();

            this.clearInstance();

            return response;


        }catch (Exception s)
        {
            s.printStackTrace();
            return null;
        }
    }

    private void clearInstance()
    {
        this.setMethodType(GET_HTTP_METHOD);
        this.headers = null;
        this.connection = null;
        this.appendablePath = new StringBuffer();
        this.setRestfulPath(DEFAULT_RESTFUL_PATH);
        this.setBasePath(DEFAULT_BASEPATH);
        this.setPort(DEFAULT_PORT);
    }

    private Object parseResponseStream(InputStream inputStream,Class<?> entity) {

        try
        {
            Gson gson = AlfahresJsonBuilder.createGson();

            InputStreamReader reader = new InputStreamReader(inputStream);

            BufferedReader bReader = new BufferedReader(reader);

            StringBuffer buff = new StringBuffer();

            String line = null;

            while((line=bReader.readLine()) != null)
            {
                buff.append(line);
            }

            String json = buff.toString();

            return gson.fromJson(json,entity);


        }catch (Exception s)
        {
            Log.e("AlfahresConnection",s.getMessage());
            return null;
        }

    }


    private Object parseResponseStream(InputStream inputStream,Type entity) {

        try
        {
            Gson gson = AlfahresJsonBuilder.createGson();

            InputStreamReader reader = new InputStreamReader(inputStream);

            BufferedReader bReader = new BufferedReader(reader);

            StringBuffer buff = new StringBuffer();

            String line = null;

            while((line=bReader.readLine()) != null)
            {
                buff.append(line);
            }

            String json = buff.toString();

            return gson.fromJson(json,entity);


        }catch (Exception s)
        {
            Log.e("AlfahresConnection",s.getMessage());
            return null;
        }

    }


    public String getHostName() {
        return hostName;
    }

    public AlfahresConnection setHostName(String hostName) {
        this.hostName = hostName;
        return this;
    }

    public String getPort() {
        return port;
    }

    public AlfahresConnection setPort(String port) {
        this.port = port;
        return this;
    }

    public String getBasePath() {
        return basePath;
    }

    public AlfahresConnection setBasePath(String basePath) {
        this.basePath = basePath;
        return this;
    }

    public String getRestfulPath() {
        return restfulPath;
    }

    public AlfahresConnection setRestfulPath(String restfulPath) {
        this.restfulPath = restfulPath;
        return this;
    }

    public String getMethodType() {
        return methodType;
    }

    public AlfahresConnection setMethodType(String methodType) {
        this.methodType = methodType;
        return this;
    }
}
