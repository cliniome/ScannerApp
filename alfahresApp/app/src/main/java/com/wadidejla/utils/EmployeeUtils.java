package com.wadidejla.utils;

import com.degla.restful.models.FileModelStates;
import com.degla.restful.models.RestfulEmployee;
import com.degla.restful.models.RestfulFile;

/**
 * Created by snouto on 30/05/15.
 */
public class EmployeeUtils {

    public static final String[] MARK_FILES_ITEMS = {"Receive Files..","Send Out Files.."};

    public static final int RECEIVE_FILES = 0;
    public static final int SEND_FILES = 1;


    public static String getStatesForFiles(RestfulFile file,RestfulEmployee account,int operationType)
    {
        String role = account.getRole();

        switch (operationType)
        {
            case RECEIVE_FILES:
            {
                if(role.equalsIgnoreCase(RoleTypes.KEEPER.toString()))
                    return file.getState();
                else if (role.equalsIgnoreCase(RoleTypes.RECEPTIONIST.toString()))
                    return file.getState();
                else if (role.equalsIgnoreCase(RoleTypes.COORDINATOR.toString()))
                    return FileModelStates.COORDINATOR_IN.toString();
                else return "";
            }


            case SEND_FILES:
            {

                if(role.equalsIgnoreCase(RoleTypes.KEEPER.toString()))
                    return FileModelStates.CHECKED_OUT.toString();
                else if (role.equalsIgnoreCase(RoleTypes.RECEPTIONIST.toString()))
                    return file.getState();
                else if (role.equalsIgnoreCase(RoleTypes.COORDINATOR.toString()))
                    return FileModelStates.COORDINATOR_OUT.toString();
                else return "";
            }

            default:
                return "";
        }

    }
}
