package com.wadidejla.newscreens.utils;

/**
 * Created by snouto on 08/06/15.
 */
public class TabDetails {

    private String title;
    private int icon;
    private String description;

    public TabDetails(String title , int icon , String description)
    {
        this.setTitle(title);
        this.setIcon(icon);
        this.setDescription(description);
    }

    public TabDetails(String title , int icon)
    {
        this.setTitle(title);
        this.setIcon(icon);
    }

    //Empty Constructor
    public TabDetails(){}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
