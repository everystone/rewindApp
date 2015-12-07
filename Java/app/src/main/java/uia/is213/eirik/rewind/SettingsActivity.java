package uia.is213.eirik.rewind;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import uia.is213.eirik.rewind.Models.User;

public class SettingsActivity extends Activity {
    private EditText serverIp;
    private EditText username;
    private EditText password;
    private EditText email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        serverIp = (EditText)findViewById(R.id.textViewServerIp);
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        email = (EditText)findViewById(R.id.email);

        Button ok = (Button)findViewById(R.id.SettingsSaveBtn);

        //REceive current MeteorUrl from MainActivity
        String url = getIntent().getExtras().getString("meteor_url");
        serverIp.setText(url);

        //Load user data
        final User u = KeyValueDB.getUserData();
        username.setText(u.username);
        email.setText(u.email);
        password.setText(u.password);

        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent();
                //@todo: check if value differs, if so, send new value back.
                intent.putExtra("meteor_url", serverIp.getText().toString()); //send new value back

                //Check if any user information was changed
                if( (!username.getText().toString().equals(u.username)) || (!email.getText().toString().equals(u.email)) || (!password.getText().toString().equals(u.password))){
                    intent.putExtra("username", username.getText().toString());
                    intent.putExtra("email", email.getText().toString());
                    intent.putExtra("password", password.getText().toString());

                }

                setResult(RESULT_OK, intent);
                finish();
            }

        });
    }



}
