package com.degla.restful.models;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    @Expose
    private List<RestfulClinic> transferrableFiles;



    public List<String> getCategories(){

        List<String> mainClinics = new ArrayList<String>();

        if(clinics != null && clinics.size() > 0)
        {
            //Sort clinics based on selected
            Collections.sort(clinics, new Comparator<RestfulClinic>() {
                @Override
                public int compare(RestfulClinic first, RestfulClinic second) {

                    return second.getSelected() - first.getSelected();
                }
            });

            for(RestfulClinic clinic : clinics)
            {
                String categoryTitle = String.format("%s(%s)",clinic.getClinicName(),clinic.getClinicCode());

                mainClinics.add(categoryTitle);
            }
        }

        return mainClinics;
    }


    public List<String> getTransferrableCategories()
    {
        List<String> mainClinics = new ArrayList<String>();

        if(transferrableFiles != null && transferrableFiles.size() > 0)
        {
            for(RestfulClinic clinic : transferrableFiles)
            {
                String categoryTitle = String.format("%s(%s)",clinic.getClinicName(),clinic.getClinicCode());

                mainClinics.add(categoryTitle);
            }
        }

        return mainClinics;
    }

    public boolean addAllTransferrableFiles(List<RestfulFile> files)
    {
        if(files != null && files.size() > 0)
        {
            for(RestfulFile file : files)
            {
                this.addTransferrableRestfulFile(file);
            }

            return true;

        }

        return false;
    }

    public boolean addAllRestfulFiles(List<RestfulFile> files)
    {
        if(files != null && files.size() > 0)
        {
            for(RestfulFile file : files)
            {
                this.addRestFulFile(file);
            }

            return true;

        }

        return false;
    }


    public boolean addTransferrableRestfulFile(RestfulFile file)
    {
        RestfulClinic current = containsTransferrableClinic(file.getClinicCode());

        if(current == null)
        {
            //create a new Restful Clinic
            RestfulClinic newClinic = new RestfulClinic();
            newClinic.setClinicName(file.getClinicName());
            newClinic.setClinicCode(file.getClinicCode());
            newClinic.setFiles(new ArrayList<RestfulFile>());
            newClinic.getFiles().add(file);

            this.getTransferrableFiles().add(newClinic);
        }else
        {
            if(current.getFiles() != null)
                current.getFiles().add(file);
            else
            {
                current.setFiles(new ArrayList<RestfulFile>());
                current.getFiles().add(file);
            }
        }

        return true;
    }


    public boolean addRestFulFile(RestfulFile file)
    {
        RestfulClinic current = containsClinic(file.getClinicCode());

        if(current == null)
        {
            //create a new Restful Clinic
            RestfulClinic newClinic = new RestfulClinic();
            newClinic.setClinicName(file.getClinicName());
            newClinic.setClinicCode(file.getClinicCode());
            newClinic.setFiles(new ArrayList<RestfulFile>());
            newClinic.getFiles().add(file);
            newClinic.setSelected(file.getSelected());

            this.getClinics().add(newClinic);
        }else
        {
            if(current.getFiles() != null)
            {
                current.getFiles().add(file);

            }

            else
            {
                current.setFiles(new ArrayList<RestfulFile>());
                current.getFiles().add(file);
            }

            current.setSelected(file.getSelected());
        }

        return true;
    }

    public RestfulClinic containsTransferrableClinic(String clinicCode)
    {
        if(transferrableFiles == null || transferrableFiles.size() <=0) return null;
        else
        {
            RestfulClinic currentClinic = null;

            for(RestfulClinic clinic : getTransferrableFiles())
            {
                if(clinic.getClinicCode().equalsIgnoreCase(clinicCode))
                {
                    currentClinic = clinic;
                    break;
                }
            }

            return currentClinic;
        }
    }


    public RestfulClinic containsClinic(String clinicCode)
    {
        if(clinics == null || clinics.size() <=0) return null;
        else
        {
            RestfulClinic currentClinic = null;

            for(RestfulClinic clinic : getClinics())
            {
                if(clinic.getClinicCode().equalsIgnoreCase(clinicCode))
                {
                    currentClinic = clinic;
                    break;
                }
            }

            return currentClinic;
        }
    }

    public HashMap<String , List<RestfulFile>> getTransferrableCategorizedData()
    {
        HashMap<String,List<RestfulFile>> categorizedData = new HashMap<String,List<RestfulFile>>();

        if(transferrableFiles != null && transferrableFiles.size() > 0)
        {
            for(RestfulClinic clinic : transferrableFiles)
            {
                String clinicTitle = String.format("%s(%s)",clinic.getClinicName(),clinic.getClinicCode());
                categorizedData.put(clinicTitle,clinic.getFiles());
            }
        }

        return categorizedData;
    }


    public HashMap<String,List<RestfulFile>> getCategorizedData()
    {
        HashMap<String,List<RestfulFile>> categorizedData = new HashMap<String,List<RestfulFile>>();

        if(clinics != null && clinics.size() > 0)
        {
            for(RestfulClinic clinic : clinics)
            {
                String clinicTitle = String.format("%s(%s)",clinic.getClinicName(),clinic.getClinicCode());

                Collections.sort(clinic.getFiles(), new Comparator<RestfulFile>() {
                    @Override
                    public int compare(RestfulFile first, RestfulFile second) {

                        return second.getSelected()  - first.getSelected();
                    }
                });
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


    public List<RestfulClinic> getTransferrableFiles() {
        return transferrableFiles;
    }

    public void setTransferrableFiles(List<RestfulClinic> transferrableFiles) {
        this.transferrableFiles = transferrableFiles;
    }
}
