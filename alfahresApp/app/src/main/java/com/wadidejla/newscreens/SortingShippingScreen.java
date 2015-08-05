package com.wadidejla.newscreens;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.degla.restful.models.BooleanResult;
import com.degla.restful.models.FileModelStates;
import com.degla.restful.models.RestfulFile;
import com.degla.restful.models.SyncBatch;
import com.degla.restful.models.http.HttpResponse;
import com.wadidejla.network.AlfahresConnection;
import com.wadidejla.newscreens.adapters.NewRequestsAdapter;
import com.wadidejla.newscreens.adapters.ShippingRequestsAdapter;
import com.wadidejla.newscreens.utils.BarcodeUtils;
import com.wadidejla.newscreens.utils.NewViewUtils;
import com.wadidejla.newscreens.utils.ScannerUtils;
import com.wadidejla.settings.SystemSettingsManager;
import com.wadidejla.tasks.ManualSyncTask;
import com.wadidejla.utils.SoundUtils;
import com.wadidejla.utils.ViewUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import wadidejla.com.alfahresapp.R;

/**
 * Created by snouto on 20/06/15.
 */
public class SortingShippingScreen extends Fragment implements IFragment {


    private ListView listView;
    private ShippingRequestsAdapter adapter;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.new_shipping_screen_layout,container,false);

        this.initView(rootView);

        return rootView;
    }

    private void initView(View rootView) {

        try
        {
            SystemSettingsManager settingsManager = SystemSettingsManager.createInstance(getActivity());

            //build the root View in here
            this.listView = (ListView)rootView.findViewById(R.id.mainFilesList);
            this.adapter = new ShippingRequestsAdapter(getActivity(),R.layout.new_single_file_view,
                    settingsManager.getShippingFiles());

            this.listView.setAdapter(this.adapter);

            //bind the buttons

            //Bind the refresh button
            Button refreshBtn = (Button)rootView.findViewById(R.id.new_files_layout_refresh_btn);

            refreshBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    SortingShippingScreen.this.refresh();

                }
            });


           /* //Bind the scan now button
            Button scanBtn = (Button)rootView.findViewById(R.id.new_files_layout_scan_btn);
            scanBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ScannerUtils.ScanBarcode(getActivity(),ScannerUtils.SCANNER_TYPE_CAMERA
                            ,SortingShippingScreen.this,false,null);


                }
            });
*/





        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }

    @Override
    public String getTitle() {
        return getResources().getString(R.string.SHIPPING_SCREEN_TITLE);
    }

    @Override
    public void chainUpdate() {

    }

    @Override
    public void refresh() {

        if(this.adapter != null && this.listView != null)
        {
            SystemSettingsManager settingsManager = SystemSettingsManager.createInstance(getActivity());
            this.adapter = new ShippingRequestsAdapter(getActivity(),R.layout.new_single_file_view,settingsManager.getShippingFiles());
            this.listView.setAdapter(this.adapter);
            this.adapter.notifyDataSetChanged();
        }

    }

    @Override
    public void handleScanResults(final String barcode) {

        try
        {
           final SystemSettingsManager settingsManager = SystemSettingsManager.createInstance(getActivity());

            if(barcode != null && !barcode.isEmpty())
            {
                //check to see if the current barcode is a true file number
                BarcodeUtils utils = new BarcodeUtils(barcode);

                if(utils.isMedicalFile())
                {
                    final AlertDialog waitingDialog = NewViewUtils.getWaitingDialog(getActivity());
                    waitingDialog.show();
                    Runnable networkThread = new Runnable() {
                        @Override
                        public void run() {

                           try
                           {


                               //if only medical file , process it
                               AlfahresConnection connection = settingsManager
                                       .getConnection();
                               HttpResponse response= connection.setAuthorization(settingsManager.getAccount().getUserName(), settingsManager.getAccount().getPassword())
                                       .setMethodType(AlfahresConnection.GET_HTTP_METHOD)
                                       .path(String.format("files/sortFile?fileNumber=%s",barcode))
                                       .call(SyncBatch.class);


                               if(response != null && Integer.parseInt(response.getResponseCode()) ==
                                       HttpResponse.OK_HTTP_CODE)
                               {
                                   //get the sync Batch
                                   BooleanResult boolResult = (BooleanResult)response.getPayload();

                                   if(!boolResult.isState())
                                   {
                                       //get the message
                                       final String msg = boolResult.getMessage();

                                       getActivity().runOnUiThread(new Runnable() {
                                           @Override
                                           public void run() {

                                               AlertDialog dialog = NewViewUtils.getAlertDialog(getActivity(),"Warning"
                                               ,msg);

                                               dialog.show();
                                           }
                                       });

                                   }else
                                   {
                                       SyncBatch batch = (SyncBatch)response.getPayload();

                                       if(batch != null)
                                       {
                                           //get the file
                                           List<RestfulFile> foundFiles = batch.getFiles();

                                           //get the first file
                                           if(foundFiles != null && foundFiles.size() > 0)
                                           {
                                               RestfulFile foundFile = foundFiles.get(0);

                                              final boolean exists = settingsManager.safelyAddToCollection(
                                                       settingsManager.getShippingFiles(),
                                                       foundFile
                                               );

                                               getActivity().runOnUiThread(new Runnable() {
                                                   @Override
                                                   public void run() {


                                                       //Play the sound
                                                       SoundUtils.playSound(getActivity());
                                                       SortingShippingScreen.this.refresh();
                                                   }
                                               });
                                           }
                                       }
                                   }
                               }else
                               {
                                   getActivity().runOnUiThread(new Runnable() {
                                       @Override
                                       public void run() {

                                           AlertDialog dialog = NewViewUtils.getAlertDialog(getActivity(),"Warning"
                                                   ,"The Patient File does not exist or you are not authorized to scan " +
                                                   "that file");

                                           dialog.show();
                                       }
                                   });
                               }

                           }catch (Exception s)
                           {
                               s.printStackTrace();
                           }
                            finally {
                               waitingDialog.dismiss();
                           }
                        }
                    };

                    Thread retrieveFileThread = new Thread(networkThread);
                    retrieveFileThread.start();

                }else if (utils.isTrolley())
                {
                    //now assign that trolley to all shipping files
                    String trolleyId = barcode;

                    if(settingsManager.getShippingFiles() != null && settingsManager.getShippingFiles().size() > 0)
                    {
                        List<RestfulFile> markedFiles = new ArrayList<RestfulFile>();

                        //assign that trolley to all existing files
                        for(RestfulFile shippingFile : settingsManager.getShippingFiles())
                        {
                           if(shippingFile.getSelected() == 1)
                           {
                               shippingFile.setTemporaryCabinetId(trolleyId);
                               shippingFile.setReadyFile(RestfulFile.READY_FILE); // convert it into ready files
                               shippingFile.setEmp(settingsManager.getAccount());
                               shippingFile.setState(FileModelStates.CHECKED_OUT.toString());

                               //now operate on that file
                               settingsManager.getFilesManager().getFilesDBManager().insertFile(shippingFile);
                               markedFiles.add(shippingFile);
                           }
                        }
                        //now remove all shipping files
                        for(RestfulFile markedFile : markedFiles)
                        {
                            settingsManager.getShippingFiles().remove(markedFile);
                        }
                        //settingsManager.setShippingFiles(new ArrayList<RestfulFile>());

                        //Now play the sound
                        SoundUtils.playSound(getActivity());

                        //now update
                        this.refresh();
                    }


                }
            }

        }catch (Exception s)
        {
            s.printStackTrace();
        }

    }
}
