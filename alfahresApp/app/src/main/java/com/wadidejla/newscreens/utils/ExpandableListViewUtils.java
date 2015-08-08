package com.wadidejla.newscreens.utils;

import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;

import com.degla.restful.models.RestfulFile;

import java.util.Iterator;
import java.util.List;

/**
 * Created by snouto on 09/08/15.
 */
public class ExpandableListViewUtils {


    public static void scrollToFile(RestfulFile file , IExpandableAdapter adapter , ExpandableListView listview)
    {
        if(listview != null && adapter != null)
        {
            if(adapter.getMainData() != null)
            {
                int parent = -1;
                int child = -1;
                Iterator<String> iterator = adapter.getMainData().keySet().iterator();

                while(iterator.hasNext())
                {
                    //Increment the parent
                    ++parent;

                    String key = iterator.next();

                    List<RestfulFile> files = adapter.getMainData().get(key);

                    if(files != null && files.size() > 0)
                    {
                        for(RestfulFile current : files)
                        {
                            ++child;

                            if(current.getFileNumber().equals(file.getFileNumber()))
                            {
                                //scroll to that position in here
                                listview.setSelectedChild(parent,child,true);
                                break;
                            }
                        }


                    }

                }
            }
        }
    }
}
