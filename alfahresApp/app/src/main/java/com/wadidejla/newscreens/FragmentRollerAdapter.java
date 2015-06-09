package com.wadidejla.newscreens;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by snouto on 08/06/15.
 */
public class FragmentRollerAdapter extends FragmentStatePagerAdapter {


    private List<Fragment> availableFragments;


    public FragmentRollerAdapter(FragmentManager fm,Context context) {
        super(fm);
        setAvailableFragments(ScreenUtils.getFragments(context));
    }

    @Override
    public Fragment getItem(int position) {
        return getAvailableFragments().get(position);
    }

    @Override
    public int getCount() {
        return getAvailableFragments().size();
    }

    public List<Fragment> getAvailableFragments() {
        return availableFragments;
    }

    public void setAvailableFragments(List<Fragment> availableFragments) {
        this.availableFragments = availableFragments;
    }
}
