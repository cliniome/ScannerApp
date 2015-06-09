package com.wadidejla.newscreens;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.wadidejla.newscreens.utils.DBStorageUtils;
import com.wadidejla.newscreens.utils.NewViewUtils;
import com.wadidejla.newscreens.utils.ScannerUtils;
import com.wadidejla.settings.SystemSettingsManager;
import com.wadidejla.tasks.CollectingTask;
import com.wadidejla.tasks.MarkingTask;
import com.wadidejla.utils.EmployeeUtils;
import com.wadidejla.utils.SoundUtils;

import java.util.List;

import wadidejla.com.alfahresapp.R;

import static com.wadidejla.newscreens.utils.ScannerUtils.SCANNER_TYPE_CAMERA;

/**
 * Created by snouto on 09/06/15.
 */
public class NewCollectFilesFragment extends Fragment implements IFragment
{

    private ExpandableListView expandableListView;
    private NewCoordinatorExpandableAdapter adapter;


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
            this.expandableListView.setAdapter(this.adapter);
            //Bind the actions in here

            //Bind the refresh button in here
            Button refreshAction = (Button)rootView.findViewById(R.id.new_files_layout_refresh_btn);

            refreshAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    ProgressDialog dialog = NewViewUtils.getWaitingDialog(getActivity());
                    dialog.show();

                    NewCollectFilesFragment.this.refresh();

                    dialog.dismiss();
                }
            });

            //Bind the scan Button
            final Button scanButton = (Button)rootView.findViewById(R.id.new_files_layout_scan_btn);
            scanButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Scan for temporary container
                    ScannerUtils.ScanBarcode(getActivity(), SCANNER_TYPE_CAMERA,
                            NewCollectFilesFragment.this);
                }
            });

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
                                    //TODO : mark all current files as missing
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

                                case R.id.pop_mark_all_collect: {

                                    final AlertDialog choiceDialog = NewViewUtils.getChoiceDialog(getActivity(),
                                            "Are you Sure ?", "Did you Collect all these Files from Clinics ?",
                                            new Runnable() {
                                                @Override
                                                public void run() {

                                                    ProgressDialog dialog = NewViewUtils.getWaitingDialog(getActivity());
                                                    dialog.show();
                                                    //start The marking Task in the background
                                                    CollectingTask collectingTask = new CollectingTask(getActivity()
                                                            ,EmployeeUtils.SEND_FILES);
                                                    collectingTask.setDialog(dialog);
                                                    collectingTask.setFragment(NewCollectFilesFragment.this);

                                                    //now encapsulate the task into a thread
                                                    Thread markingThread = new Thread(collectingTask);
                                                    markingThread.start();
                                                }
                                            }, new Runnable() {
                                                @Override
                                                public void run() {

                                                    //Do nohing in here
                                                }
                                            });

                                    choiceDialog.show();
                                }

                                break;
                            }

                            return true;
                        }
                    });
                    menu.show();

                }
            });


        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }

    @Override
    public String getTitle() {
         return getResources().getString(R.string.ScreenUtils_Collect_Files);
    }

    @Override
    public void chainUpdate() {

        this.refresh();
    }

    @Override
    public void refresh() {

        if(this.adapter != null)
            this.adapter.notifyDataSetChanged();

    }

    @Override
    public void handleScanResults(String barcode) {

    }
}
