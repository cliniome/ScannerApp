package com.degla.restful.models.http;

import java.io.Serializable;

/**
 * Created by snouto on 22/05/15.
 */
public class HttpResponse implements Serializable {

    private String responseCode;
    private Object payload;

    public HttpResponse(){}
    public HttpResponse(String code,Object payload)
    {
        this.setResponseCode(code);
        this.setPayload(payload);
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
