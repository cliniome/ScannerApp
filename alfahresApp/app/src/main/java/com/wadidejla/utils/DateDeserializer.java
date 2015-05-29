package com.wadidejla.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * Created by snouto on 29/05/15.
 */
public class DateDeserializer implements JsonDeserializer<Date>
{
    @Override
    public Date deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException
    {
        String myDate = je.getAsString();
        // inspect string using regexes
        // convert string to Date
        // return Date object

        if(myDate == null || myDate.length() <=0)
            return new Date();

        Date currentDate = new Date(myDate);

        return currentDate;
    }

}
