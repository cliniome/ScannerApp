package com.wadidejla.newscreens.utils;

/**
 * Created by snouto on 08/06/15.
 */
public class TabDetails {

    private String title;
    private int icon;
    private String description;
    private TabTypes type;

    public TabDetails(String title , int icon , String description , TabTypes type)
    {
        this.setTitle(title);
        this.setIcon(icon);
        this.setDescription(description);
        this.setType(type);
    }

    public TabDetails(String title , int icon,TabTypes type)
    {
        this.setTitle(title);
        this.setIcon(icon);
        this.setType(type);
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

    public TabTypes getType() {
        return type;
    }

    public void setType(TabTypes type) {
        this.type = type;
    }
}
