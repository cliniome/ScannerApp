package com.wadidejla.actions;

import android.util.Log;

import com.degla.restful.models.FileModelStates;
import com.degla.restful.models.RestfulEmployee;
import com.wadidejla.utils.RoleTypes;

import java.util.ArrayList;
import java.util.List;
import static com.wadidejla.actions.ActionTypes.*;
/**
 * Created by snouto on 02/08/15.
 */
public class ActionUtils {


    private static Action MissingAction = new Action("Mark File as Missing",ActionTypes.MISSING);
    private static Action ClearAction = new Action("Clear That File",ActionTypes.CLEARFILE);
    private static Action CollectAction = new Action("Collect That File",ActionTypes.COLLECT);
    private static Action TransferAction = new Action("View Transfer Info",ActionTypes.TRANSFERINFO);
    private static Action DistributeAction = new Action("Distribute That File",ActionTypes.DISTRIBUTE);



    public static List<Action> getAllActions(FileModelStates state,ActionTypes[] actionTypes) {


        List<Action> actions = new ArrayList<Action>();
        try
        {
            if(actionTypes == null || actionTypes.length <=0)
                throw new Exception("Required Actions should not be empty");

            for(ActionTypes type : actionTypes)
            {
                switch (type)
                {
                    case MISSING:
                        actions.add(MissingAction);
                        break;
                    case CLEARFILE:
                        actions.add(ClearAction);
                        break;
                    case COLLECT:
                        actions.add(CollectAction);
                        break;
                    case DISTRIBUTE:
                        actions.add(DistributeAction);
                        break;
                    case TRANSFERINFO:
                        actions.add(TransferAction);
                        break;

                }
            }


            checkActionsBasedOnState(actions,state);

        }catch (Exception s)
        {
            Log.e("Error",s.getMessage());
            return new ArrayList<Action>();
        }

        finally {
            return actions;
        }

    }

    private static void checkActionsBasedOnState(List<Action> actions, FileModelStates state) {

        if(state == FileModelStates.MISSING)
            actions.remove(MissingAction);
    }

    public static String[] convertIntoStringArray(List<Action> actions)
    {
        String[] titles = new String[actions.size()];

        for(int i=0;i<actions.size();i++)
        {
            titles[i] = actions.get(i).toString();
        }

        return titles;
    }
}
