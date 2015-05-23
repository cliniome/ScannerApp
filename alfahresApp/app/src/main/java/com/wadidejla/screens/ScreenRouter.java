package com.wadidejla.screens;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.wadidejla.settings.SystemSettingsManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by snouto on 23/05/15.
 */
public class ScreenRouter {

    //RECEPTIONIST, KEEPER, COORDINATOR,ADMIN;

    private static final String KEEPER_ROLE="KEEPER";
    private static final String RECEPTIONIST_ROLE="RECEPTIONIST";
    private static final String COORDINATOR_ROLE="COORDINATOR";
    private static final String ADMIN_ROLE="ADMIN";

    public static List<Fragment> getFragments(Context con)
    {
        SystemSettingsManager settingsManager = SystemSettingsManager.createInstance(con);

        List<Fragment> fragments = new ArrayList<Fragment>();

        if(settingsManager.getAccount() != null)
        {
            if(settingsManager.getAccount().getRole().equals(KEEPER_ROLE))
            {
                //that is the keeper
                fragments.add(new MainFilesScreenFragment());

            }else if (settingsManager.getAccount().getRole().equals(RECEPTIONIST_ROLE))
            {
                //that is the receptionist
            }
            else if (settingsManager.getAccount().getRole().equals(COORDINATOR_ROLE))
            {
                //that is the coordinator
            }else if (settingsManager.getAccount().getRole().equals(ADMIN_ROLE))
            {
                //that is the admin role
            }
        }
        return fragments;

    }
}
