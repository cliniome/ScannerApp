package com.wadidejla.actions;

/**
 * Created by snouto on 02/08/15.
 */
public class Action {

    private String title;
    private ActionTypes type;

    private Runnable runnable;

    public Action(){

        this.setType(ActionTypes.NONE);
    }

    public Action(String msg)
    {
        this();
        this.setTitle(msg);
    }

    public Action(String msg,ActionTypes type)
    {
        this.setTitle(msg);
        this.setType(type);
    }

    public void attach(Runnable runnable)
    {
        this.runnable = runnable;
    }

    public void runAction()
    {
        runnable.run();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ActionTypes getType() {
        return type;
    }

    public void setType(ActionTypes type) {
        this.type = type;
    }

    @Override
    public String toString() {

        return this.getTitle();
    }
}
