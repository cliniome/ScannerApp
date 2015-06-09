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
import android.widget.ListView;
import android.widget.PopupMenu;

import com.degla.restful.models.FileModelStates;
import com.degla.restful.models.RestfulFile;
import com.wadidejla.newscreens.adapters.NewDistributeFilesAdapter;
import com.wadidejla.newscreens.utils.DBStorageUtils;
import com.wadidejla.newscreens.utils.NewViewUtils;
import com.wadidejla.newscreens.utils.ScannerUtils;
import com.wadidejla.settings.SystemSettingsManager;
import com.wadidejla.tasks.MarkingTask;
import com.wadidejla.utils.EmployeeUtils;
import com.wadidejla.utils.SoundUtils;

import java.util.List;

import wadidejla.com.alfahresapp.R;

import static com.wadidejla.newscreens.utils.ScannerUtils.SCANNER_TYPE_CAMERA;

/**
 * Created by snouto on 09/06/15.
 */
public class NewDistributeFilesFragment extends Fragment implements IFragment {


    private ListView distributeFilesList;
    private NewDistributeFilesAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.new_receive_files_layout,container,false);

        this.initView(rootView);

        return rootView;
    }

    private void initView(View rootView) {
        try
        {
            //Begin the view initialization in here
            this.distributeFilesList = (ListView)rootView.findViewById(R.id.mainFilesList);
            this.adapter = new NewDistributeFilesAdapter(getActivity(),R.layout.new_single_file_view);
            this.distributeFilesList.setAdapter(this.adapter);
            //Bind refresh, scan , actions buttons
            Button refreshAction = (Button)rootView.findViewById(R.id.new_files_layout_refresh_btn);

            refreshAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    ProgressDialog dialog = NewViewUtils.getWaitingDialog(getActivity());
                    dialog.show();

                    NewDistributeFilesFragment.this.refresh();

                    dialog.dismiss();
                }
            });

            //Bind the actions buttons
            final Button scanButton = (Button)rootView.findViewById(R.id.new_files_layout_scan_btn);

            scanButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    //Scan for temporary container
                    ScannerUtils.ScanBarcode(getActivity(), SCANNER_TYPE_CAMERA
                            , NewDistributeFilesFragment.this);



                }
            });

            final Button DoActionsBtn = (Button)rootView.findViewById(R.id.new_receive_actions_btn);

            DoActionsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    PopupMenu menu = new PopupMenu(getActivity(),view);
                    menu.inflate(R.menu.new_distribute_pop_menu);



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

                                                    List<RestfulFile> availableFiles = storageUtils.getFilesReadyForDistribution();

                                                    if (availableFiles != null) {
                                                        for (RestfulFile file : availableFiles) {
                                                            storageUtils.operateOnFile(file, FileModelStates.MISSING.toString(),
                                                                    RestfulFile.READY_FILE);
                                                        }

                                                        //Empty all files
                                                        storageUtils.getReceivedFiles().clear();

                                                        //now update
                                                        SoundUtils.playSound(getActivity());
                                                        NewDistributeFilesFragment.this.refresh();
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

                                case R.id.pop_mark_all_distribute:
                                {
                                    AlertDialog dialog = NewViewUtils.getChoiceDialog(getActivity(),
                                            "Mark all Files as Distributed",
                                            "Are you sure to mark all files as Distributed?",
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    //Mark them

                                                    DBStorageUtils storageUtils = new DBStorageUtils(getActivity());

                                                    List<RestfulFile> availableFiles = storageUtils.getFilesReadyForDistribution();

                                                    if (availableFiles != null) {
                                                        for (RestfulFile file : availableFiles) {
                                                            storageUtils.operateOnFile(file, FileModelStates.DISTRIBUTED.toString(),
                                                                    RestfulFile.READY_FILE);
                                                        }

                                                        //Empty all files
                                                        storageUtils.getReceivedFiles().clear();

                                                        //now update
                                                        SoundUtils.playSound(getActivity());
                                                        NewDistributeFilesFragment.this.refresh();
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



        }catch (Exception s)
        {
            s.printStackTrace();
        }

    }

    @Override
    public String getTitle() {
        return getResources().getString(R.string.ScreenUtils_Distribute_Files);
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

        try
        {
            //access the restful file that matches the current barcode and distribute it to clinics
            //then remove it from the list with sound
            DBStorageUtils storageUtils = new DBStorageUtils(getActivity());
            RestfulFile file = storageUtils.getSettingsManager().getFilesManager().getFilesDBManager()
                    .getFileByNumber(barcode);

            if(file != null)
            {
                file.setState(FileModelStates.DISTRIBUTED.toString());
                file.setEmp(storageUtils.getSettingsManager().getAccount());
                file.setReadyFile(RestfulFile.READY_FILE);
                //now save the current file
                storageUtils.getSettingsManager().getFilesManager().getFilesDBManager()
                        .insertFile(file);
                SoundUtils.playSound(getActivity());

                //now refresh the current fragment
                this.refresh();
            }


        }catch (Exception s)
        {
            s.printStackTrace();
        }

    }
}
