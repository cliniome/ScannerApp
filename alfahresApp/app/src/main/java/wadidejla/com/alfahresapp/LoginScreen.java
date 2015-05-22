package wadidejla.com.alfahresapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by snouto on 22/05/15.
 */
public class LoginScreen extends Activity {


    private EditText userNameText;
    private EditText passwordText;


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


    private void init()
    {
        try
        {
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

            Button loginBtn = (Button)findViewById(R.id.btn_login_login);

            if(loginBtn != null)
            {
                loginBtn.setOnClickListener(new Button.OnClickListener(){

                    @Override
                    public void onClick(View view) {

                        //do the login, for now just call the alfahresMain activity
                        try
                        {
                            Intent startIntent = new Intent(LoginScreen.this,AlfahresMain.class);
                            startActivity(startIntent);


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
