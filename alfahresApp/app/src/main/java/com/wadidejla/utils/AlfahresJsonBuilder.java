package com.wadidejla.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by snouto on 22/05/15.
 */
public class AlfahresJsonBuilder {


    public static Gson createGson()
    {
        GsonBuilder builder = new GsonBuilder();

        builder = builder.excludeFieldsWithoutExposeAnnotation();
        builder = builder.setDateFormat(DateFormat.FULL);
        //builder = builder.registerTypeAdapter(Date.class,DateDeserializer.class);
        //add any additional properties for the builder
        //finally return the gson object
        return builder.create();
    }
}
