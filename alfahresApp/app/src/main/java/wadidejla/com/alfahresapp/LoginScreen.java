package wadidejla.com.alfahresapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.degla.restful.models.BooleanResult;
import com.degla.restful.models.http.HttpResponse;
import com.wadidejla.network.AlfahresConnection;
import com.wadidejla.settings.SystemSettingsManager;

/**
 * Created by snouto on 22/05/15.
 */
public class LoginScreen extends ActionBarActivity {


    private EditText userNameText;
    private EditText passwordText;
    private ProgressBar loadingBar;
    private SystemSettingsManager settingsManager;



    public LoginScreen(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen_main);
        setTitle(R.string.TITLE_LOGIN);
        this.init();
    }



    private void clear()
    {
        if(userNameText != null) userNameText.setText("");
        if(passwordText != null) passwordText.setText("");

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this,SettingsActivity.class);
            startActivity(settingsIntent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void init()
    {
        try
        {
            //initialize the settings manager
            settingsManager = SystemSettingsManager.createInstance(this);
            loadingBar = (ProgressBar)findViewById(R.id.loginProgressBar);
            loadingBar.setVisibility(View.GONE);
            userNameText = (EditText)findViewById(R.id.userNameTxt);
            passwordText = (EditText)findViewById(R.id.passwordTxt);



            //get the clear button and assign the Clear action Listener to it
            Button clearBtn = (Button)findViewById(R.id.btn_login_clear);

            if(clearBtn != null)
            {
                clearBtn.setOnClickListener(new Button.OnClickListener(){

                    @Override
                    public void onClick(View view) {

                        LoginScreen.this.clear();


                    }
                });
            }
            //get the login button and assign the login action

            final Button loginBtn = (Button)findViewById(R.id.btn_login_login);

            if(loginBtn != null)
            {
                loginBtn.setOnClickListener(new Button.OnClickListener(){

                    @Override
                    public void onClick(View view) {

                        //do the login, for now just call the alfahresMain activity
                        try
                        {

                            //get the username and password in here
                            final String username = userNameText.getText().toString();
                            final String password = passwordText.getText().toString();

                            if(username != null && password != null && username.length() > 0 &&
                                    password.length() > 0)
                            {

                                LoginScreen.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        LoginScreen.this.loadingBar.setVisibility(View.GONE);
                                        LoginScreen.this.loadingBar.setVisibility(View.VISIBLE);
                                    }
                                });

                                    Thread newThread = new Thread(new Runnable() {
                                        @Override
                                        public void run() {

                                            AlfahresConnection connection = new AlfahresConnection(
                                                    LoginScreen.this.settingsManager.getServerAddress()
                                                    ,"8080","alfahres","rest");

                                            HttpResponse response = connection.path("employee/login").setMethodType(AlfahresConnection.GET_HTTP_METHOD)
                                                    .setAuthorization(username,password).call(BooleanResult.class);

                                            if(response != null)
                                            {
                                                if(Integer.parseInt(response.getResponseCode()) == 200)
                                                {
                                                    LoginScreen.this.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {

                                                            //hide the progress bar
                                                            LoginScreen.this.loadingBar.setVisibility(View.VISIBLE);
                                                            LoginScreen.this.loadingBar.setVisibility(View.GONE);
                                                            LoginScreen.this.finish();
                                                            //Start the main activity
                                                            Intent mainIntent = new Intent(LoginScreen.this,AlfahresMain.class);
                                                            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            startActivity(mainIntent);


                                                        }
                                                    });
                                                }else
                                                {
                                                    LoginScreen.this.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {

                                                            //stop the progress bar
                                                            LoginScreen.this.loadingBar.setVisibility(View.GONE);

                                                            //show a dialog
                                                            final AlertDialog dlg = new AlertDialog.Builder(LoginScreen.this)
                                                                    .setTitle(R.string.LOGIN_UNATHORIZED)
                                                                    .setMessage(R.string.LOGIN_UNAUTH_MESSAGE)
                                                                    .setCancelable(false)
                                                                    .setIcon(R.drawable.denied)
                                                                    .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialogInterface, int i) {

                                                                            dialogInterface.cancel();
                                                                            //clear the username and password fields
                                                                            LoginScreen.this.clear();

                                                                        }
                                                                    }).create();

                                                            dlg.show();

                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    });
                                    newThread.start();

                            }else
                            {

                                loadingBar.setVisibility(View.GONE);

                                //show an alert dialog
                                final AlertDialog dlg = new AlertDialog
                                        .Builder(LoginScreen.this)
                                        .setCancelable(false)
                                        .setIcon(R.drawable.error)
                                        .setMessage("Username/Password are invalid or empty")
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                dialogInterface.cancel();

                                            }
                                        }).create();

                                dlg.show();
                            }





                        }catch (Exception s)
                        {
                            Log.e("LoginScreen",s.getMessage());
                        }

                    }
                });
            }

        }catch (Exception s)
        {
            Log.d("LoginScreen",s.getMessage());
        }
    }




}
