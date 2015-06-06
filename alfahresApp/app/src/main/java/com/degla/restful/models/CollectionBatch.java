package com.degla.restful.models;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by snouto on 06/06/15.
 */
public class CollectionBatch implements Serializable {

    @Expose
    private List<RestfulClinic> clinics;

    @Expose
    private Long createdAt;



    public List<String> getCategories(){

        List<String> mainClinics = new ArrayList<String>();

        if(clinics != null && clinics.size() > 0)
        {
            for(RestfulClinic clinic : clinics)
            {
                String categoryTitle = String.format("%s(%s)",clinic.getClinicName(),clinic.getClinicCode());

                mainClinics.add(categoryTitle);
            }
        }

        return mainClinics;
    }


    public HashMap<String,List<RestfulFile>> getCategorizedData()
    {
        HashMap<String,List<RestfulFile>> categorizedData = new HashMap<String,List<RestfulFile>>();

        if(clinics != null && clinics.size() > 0)
        {
            for(RestfulClinic clinic : clinics)
            {
                String clinicTitle = String.format("%s(%s)",clinic.getClinicName(),clinic.getClinicCode());
                categorizedData.put(clinicTitle,clinic.getFiles());
            }
        }

        return categorizedData;
    }

    public CollectionBatch(){
        this.setClinics(new ArrayList<RestfulClinic>());
    }

    public List<RestfulClinic> getClinics() {
        return clinics;
    }

    public void setClinics(List<RestfulClinic> clinics) {
        this.clinics = clinics;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }
}