package com.wadidejla.newscreens.adapters;

import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.readystatesoftware.viewbadger.BadgeView;
import com.wadidejla.newscreens.utils.TabDetails;
import com.wadidejla.newscreens.utils.TabTypes;
import com.wadidejla.settings.SystemSettingsManager;

import static com.wadidejla.newscreens.utils.TabTypes.*;
import java.util.Currency;
import java.util.List;

import wadidejla.com.alfahresapp.R;


/**
 * Created by snouto on 23/07/15.
 */
public class TabDetailsArrayAdapter extends ArrayAdapter<TabDetails> {

    private List<TabDetails> tabDetailsList;
    private int resourceId;

    public TabDetailsArrayAdapter(Context context, int resource) {
        super(context, resource);
        this.resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null)
        {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(resourceId,null);
        }

        //get the current Tab Details
        TabDetails currentDetails = tabDetailsList.get(position);
        //get the image view
        ImageView tabDetailsImg = (ImageView)convertView.findViewById(R.id.tabDetailImg);
        tabDetailsImg.setImageResource(currentDetails.getIcon());

        //get the tab Details Text
        TextView tabDetailsText = (TextView)convertView.findViewById(R.id.tabDetailText);
        tabDetailsText.setText(currentDetails.getTitle());

        return convertView;

    }



    @Override
    public int getCount() {

        return getTabDetailsList().size();
    }


    public List<TabDetails> getTabDetailsList() {
        return tabDetailsList;
    }

    public void setTabDetailsList(List<TabDetails> tabDetailsList) {
        this.tabDetailsList = tabDetailsList;
    }
}
