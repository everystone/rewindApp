package uia.is213.eirik.rewind;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SettingsActivity extends AppCompatActivity {
    private EditText serverIp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        serverIp = (EditText)findViewById(R.id.textViewServerIp);
        Button ok = (Button)findViewById(R.id.SettingsSaveBtn);

        //REceive current MeteorUrl from MainActivity
        String url = getIntent().getExtras().getString("meteor_url");
        serverIp.setText(url);

        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent();
                //@todo: check if value differs, if so, send new value back.
                intent.putExtra("meteor_url", serverIp.getText().toString()); //send new value back
                setResult(RESULT_OK, intent);
                finish();
            }

        });
    }



}
