package com.degla.restful.models.http;

import java.io.Serializable;

/**
 * Created by snouto on 22/05/15.
 */
public class HttpResponse implements Serializable {


    public static final int OK_HTTP_CODE = 200;
    public static final int UNAUTHORIZED_HTTP_CODE = 401;

    private String responseCode;
    private Object payload;

    public HttpResponse(){}
    public HttpResponse(String code,Object payload)
    {
        this.setResponseCode(code);
        this.setPayload(payload);
    }

    @Override
    public String toString() {

        return String.format("Response Code : %s , Response Body : \n%s\n"
                ,this.getResponseCode(),this.getPayload().toString());

    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}
