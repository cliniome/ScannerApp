package com.wadidejla.utils;

import android.widget.ArrayAdapter;

/**
 * Created by snouto on 29/05/15.
 */
public interface Actionable<T> {

    public void doAction(Object OnItem , T adapter);
}
