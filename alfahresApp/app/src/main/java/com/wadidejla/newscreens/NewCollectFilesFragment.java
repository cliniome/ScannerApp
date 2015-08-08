package com.wadidejla.newscreens;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.PopupMenu;

import com.degla.restful.models.FileModelStates;
import com.degla.restful.models.RestfulFile;
import com.wadidejla.newscreens.adapters.NewCoordinatorExpandableAdapter;
import com.wadidejla.newscreens.utils.BarcodeUtils;
import com.wadidejla.newscreens.utils.DBStorageUtils;
import com.wadidejla.newscreens.utils.ExpandableListViewUtils;
import com.wadidejla.newscreens.utils.NetworkUtils;
import com.wadidejla.newscreens.utils.NewViewUtils;
import com.wadidejla.newscreens.utils.ScannerUtils;
import com.wadidejla.settings.SystemSettingsManager;
import com.wadidejla.tasks.CollectingTask;
import com.wadidejla.tasks.MarkingTask;
import com.wadidejla.utils.EmployeeUtils;
import com.wadidejla.utils.SoundUtils;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import wadidejla.com.alfahresapp.R;

import static com.wadidejla.newscreens.utils.ScannerUtils.SCANNER_TYPE_CAMERA;

/**
 * Created by snouto on 09/06/15.
 */
public class NewCollectFilesFragment extends Fragment implements IFragment , IAdapterListener
{

    private ExpandableListView expandableListView;
    private FragmentListener listener;
    private NewCoordinatorExpandableAdapter adapter;

    private int totalFiles = 0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.coordinator_main_list_view,container,false);

        this.initView(rootView);

        return rootView;
    }

    private void initView(View rootView) {

        try
        {
            this.expandableListView = (ExpandableListView)rootView.findViewById(R.id.coordinator_list_view);
            this.adapter = new NewCoordinatorExpandableAdapter(getActivity());
            this.adapter.setFragment(this);
            this.expandableListView.setAdapter(this.adapter);
            this.adapter.setListView(this.expandableListView);
            //Bind the actions in here



           /* //Bind the scan Button
            final Button scanButton = (Button)rootView.findViewById(R.id.new_files_layout_scan_btn);
            scanButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Scan for temporary container
                    ScannerUtils.ScanBarcode(getActivity(), SCANNER_TYPE_CAMERA,
                            NewCollectFilesFragment.this,false,null);
                }
            });
*/
            //Bind Do Actions Menu Button

            //Do action
            final Button DoActionsBtn = (Button)rootView.findViewById(R.id.new_receive_actions_btn);

            DoActionsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    PopupMenu menu = new PopupMenu(getActivity(),view);
                    menu.inflate(R.menu.new_coordinator_pop_menu);

                    SystemSettingsManager settingsManager = SystemSettingsManager.createInstance(getActivity());

                    if(settingsManager.getAccount().isKeeper() ||
                            settingsManager.getAccount().isReceptionist())
                    {
                        menu.getMenu().getItem(R.id.pop_mark_all_received).setVisible(false);
                    }

                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {

                            switch (menuItem.getItemId()) {

                                case R.id.pop_mark_all_missing:

                                {
                                    //Do the missing actions on all the available Files

                                    //Ask the user if he is going to mark all as missing
                                    AlertDialog dialog = NewViewUtils.getChoiceDialog(getActivity(),
                                            "Mark all Files as Missing", "Are you sure to mark all files as missing?",
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    //Mark them

                                                    DBStorageUtils storageUtils = new DBStorageUtils(getActivity());

                                                    List<RestfulFile> availableFiles = storageUtils.
                                                            getFilesReadyForCollection();

                                                    if (availableFiles != null) {
                                                        for (RestfulFile file : availableFiles) {
                                                            storageUtils.operateOnFile(file, FileModelStates.MISSING.toString(),
                                                                    RestfulFile.READY_FILE);
                                                        }

                                                        //Empty all files
                                                        storageUtils.getReceivedFiles().clear();

                                                        //now update
                                                        SoundUtils.playSound(getActivity());
                                                        NewCollectFilesFragment.this.refresh();
                                                    }
                                                }
                                            }, new Runnable() {
                                                @Override
                                                public void run() {
                                                    //do nothing in here
                                                }
                                            });

                                    dialog.show();
                                }
                                break;


                            }

                            return true;
                        }
                    });
                    menu.show();

                }
            });

            //Do the refresh
            this.refresh();


        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }

    @Override
    public String getTitle() {
         String title =  getResources().getString(R.string.ScreenUtils_Collect_Files);

        title = String.format("%s(%s)",title,this.getTotalFiles());

        return title;
    }

    @Override
    public void chainUpdate() {

        this.refresh();
    }

    @Override
    public void refresh() {

        if(this.adapter != null)
        {
            this.adapter.loadData();
            this.adapter.notifyDataSetChanged();
        }

        NetworkUtils.ScheduleSynchronization(getActivity());

    }

    @Override
    public void handleScanResults(String barcode) {

        try {

            if(barcode != null && !barcode.isEmpty())
            {
                BarcodeUtils barcodeUtils = new BarcodeUtils(barcode);
                //mark it as collected
                DBStorageUtils storageUtils = new DBStorageUtils(getActivity());

                         if(barcodeUtils.isMedicalFile())
                            {
                                NewCoordinatorExpandableAdapter adapter = (NewCoordinatorExpandableAdapter)this.adapter;

                                if(adapter != null)
                                {

                                    Map<String,List<RestfulFile>> categorizedData = adapter.getCategorizedData();

                                    RestfulFile foundFile = null;

                                    Collection<List<RestfulFile>> collectedFiles = categorizedData.values();

                                    if(collectedFiles != null && !collectedFiles.isEmpty())
                                    {
                                        Iterator<List<RestfulFile>> iterator = collectedFiles.iterator();

                                        while(iterator.hasNext())
                                        {
                                            List<RestfulFile> oneBatchFiles = iterator.next();

                                            if(oneBatchFiles != null && !oneBatchFiles.isEmpty())
                                            {
                                                for(RestfulFile file : oneBatchFiles)
                                                {
                                                    if(file.getFileNumber().equals(barcode))
                                                    {
                                                        foundFile = file;
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }


                                    if(foundFile != null)
                                    {

                                        //otherwise , toggle the selection of that file
                                        foundFile.toggleSelection();
                                        if(foundFile.getSelected() > 0 && foundFile.isMultipleClinics())
                                        {
                                            //Mark that file immediately as sent out
                                            storageUtils.operateOnFile(foundFile,FileModelStates.COORDINATOR_OUT.toString(),RestfulFile.READY_FILE);
                                            return;
                                        }
                                        this.getFilesReadyToBeCollected(foundFile);


                                        storageUtils.operateOnFile(foundFile,foundFile.getState(),RestfulFile.NOT_READY_FILE);
                                    }

                            }


                        }else if (barcodeUtils.isTrolley())
                         {
                             Map<String,List<RestfulFile>> categorizedData = adapter.getCategorizedData();

                             RestfulFile foundFile = null;

                             Collection<List<RestfulFile>> collectedFiles = categorizedData.values();

                             if(collectedFiles != null && !collectedFiles.isEmpty())
                             {
                                 Iterator<List<RestfulFile>> iterator = collectedFiles.iterator();

                                 while(iterator.hasNext())
                                 {
                                     List<RestfulFile> oneBatchFiles = iterator.next();

                                     if(oneBatchFiles != null && !oneBatchFiles.isEmpty())
                                     {
                                         for(RestfulFile file : oneBatchFiles)
                                         {
                                             if(file.getSelected() == 1) //grab the selected file
                                             {
                                                 foundFile = file;
                                                 break;
                                             }
                                         }
                                     }
                                 }
                             }

                             if(foundFile != null)
                             {
                                 //mark that file as coordinator out and mark it as ready file
                                 foundFile.setTemporaryCabinetId(barcode);

                                 foundFile.setSelected(0);

                                 storageUtils.operateOnFile(foundFile,FileModelStates.COORDINATOR_OUT.toString(),
                                         RestfulFile.READY_FILE);
                             }

                         }






            }

        }catch (Exception s)
        {

            Log.e("error",s.getMessage());
        }

        finally {
            //Then play the sound
            SoundUtils.playSound(getActivity());

            //Update the screen
            this.refresh();
        }

    }

    @Override
    public void setFragmentListener(FragmentListener listener) {


        this.listener = listener;

    }

    private void getFilesReadyToBeCollected(RestfulFile foundFile) {

        try
        {
            DBStorageUtils storageUtils = new DBStorageUtils(getActivity());

            List<RestfulFile> foundFiles = storageUtils.getFilesReadyForCollection();

            if(foundFiles != null && foundFiles.size() > 0)
            {
                for(RestfulFile current : foundFiles)
                {
                    if(current.getFileNumber().equals(foundFile.getFileNumber()))
                        continue;
                    else
                    {
                        current.setSelected(0); // unselect it

                        //then update it
                        //storageUtils.operateOnFile(current,current.getState(),current.getReadyFile());
                    }
                }
            }

        }catch (Exception s)
        {
            Log.e("Error",s.getMessage());
        }
    }

    @Override
    public void doUpdateFragment() {

        if(this.listener != null)
            this.listener.invalidate();

    }

    public int getTotalFiles() {
        return totalFiles;
    }

    public void setTotalFiles(int totalFiles) {
        this.totalFiles = totalFiles;
    }
}
