package com.wadidejla.utils;

/**
 * Created by snouto on 29/05/15.
 */
public class ActionItem {

    private String actionName;

    private Actionable action;


    public ActionItem(String actionName)
    {
        this.setActionName(actionName);
    }


    @Override
    public String toString() {

        return this.actionName;
    }

    public ActionItem(String actionName,Actionable action)
    {
        this.setActionName(actionName);
        this.setAction(action);
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public Actionable getAction() {
        return action;
    }

    public void setAction(Actionable action) {
        this.action = action;
    }
}
