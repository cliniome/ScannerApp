package com.wadidejla.newscreens;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.wadidejla.screens.ScreenRouter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by snouto on 08/06/15.
 */
public class FragmentRollerAdapter extends FragmentStatePagerAdapter {


    private List<Fragment> availableFragments;


    public FragmentRollerAdapter(FragmentManager fm,Context context) {
        super(fm);
        availableFragments = ScreenUtils.getFragments(context);
    }

    @Override
    public Fragment getItem(int position) {
        return availableFragments.get(position);
    }

    @Override
    public int getCount() {
        return availableFragments.size();
    }
}
