package com.wadidejla.screens;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

import com.wadidejla.utils.FilesOnChangeListener;

import java.util.List;
import java.util.Locale;

import wadidejla.com.alfahresapp.R;

/**
 * Created by snouto on 23/05/15.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private Activity parentActivity;

    private List<Fragment> screens;


    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public SectionsPagerAdapter(FragmentManager fm , final List<Fragment> screens)
    {
        this(fm);
        this.screens = screens;
        fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {

                if(screens != null && screens.size() > 0)
                {
                    for(Fragment listener : screens)
                    {
                        if(listener instanceof  FilesOnChangeListener)
                        {
                            FilesOnChangeListener onFilesListener = (FilesOnChangeListener)listener;
                            onFilesListener.notifyChange();
                        }
                    }
                }

            }
        });
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).

        Fragment currentFragment = screens.get(position);

        return currentFragment;
    }

    @Override
    public void setPrimaryItem(View container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return screens.size();
    }


    public Activity getParentActivity() {
        return parentActivity;
    }

    public void setParentActivity(Activity parentActivity) {
        this.parentActivity = parentActivity;
    }
}
