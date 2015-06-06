package com.wadidejla.listeners;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import com.wadidejla.utils.ActionItem;

import java.util.ArrayList;
import java.util.List;

import wadidejla.com.alfahresapp.R;

/**
 * Created by snouto on 29/05/15.
 */
public class KeeperOnClickListener<T> implements View.OnLongClickListener {

    private Context context;

    private List<ActionItem<T>> actionItems;

    private T parentAdapter;


    public KeeperOnClickListener(Context con)
    {
        this.setContext(con);
        this.setActionItems(new ArrayList<ActionItem<T>>());
    }


    private CharSequence[] toStringItems()
    {
        List<CharSequence> sequences = new ArrayList<CharSequence>();

        for(ActionItem item : getActionItems())
        {
            sequences.add(item.getActionName());
        }


        return sequences.toArray(new CharSequence[]{});
    }



    @Override
    public boolean onLongClick(final View view) {
        try
        {
            final AlertDialog choiceDlg = new AlertDialog.Builder(getContext())
                    .setTitle(R.string.SINGLE_CHOICE_DLG_TITLE)
                    .setItems(toStringItems(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            //get the action item
                            ActionItem item = getActionItems().get(i);

                            Object tag = view.getTag();

                            if (item != null) {
                                item.getAction().doAction(tag,getParentAdapter());
                            }

                        }
                    }).create();

            choiceDlg.show();




            return true;

        }catch (Exception s)
        {
            Log.w("LongClickListener",s.getMessage());
            return false;
        }
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }


    public List<ActionItem<T>> getActionItems() {
        return actionItems;
    }

    public void setActionItems(List<ActionItem<T>> actionItems) {
        this.actionItems = actionItems;
    }

    public T getParentAdapter() {
        return parentAdapter;
    }

    public void setParentAdapter(T parentAdapter) {
        this.parentAdapter = parentAdapter;
    }
}
