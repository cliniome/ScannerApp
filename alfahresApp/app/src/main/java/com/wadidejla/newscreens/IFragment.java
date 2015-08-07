package com.wadidejla.newscreens;

/**
 * Created by snouto on 08/06/15.
 */
public interface IFragment {

     String getTitle();

    void chainUpdate();

    void refresh();

    void handleScanResults(String barcode);

    void setFragmentListener(FragmentListener listener);


}
