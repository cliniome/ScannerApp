package com.wadidejla.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by snouto on 22/05/15.
 */
public class AlfahresJsonBuilder {


    public static Gson createGson()
    {
        GsonBuilder builder = new GsonBuilder();

        //add any additional properties for the builder

        //finally return the gson object
        return builder.create();
    }
}
