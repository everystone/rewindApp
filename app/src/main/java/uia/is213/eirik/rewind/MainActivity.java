package uia.is213.eirik.rewind;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import im.delight.android.ddp.Meteor;
import im.delight.android.ddp.MeteorCallback;

public class MainActivity extends AppCompatActivity implements MeteorCallback{

    //Vars
    private Meteor mMeteor;
    private static String mUrl = "ws://192.168.11.87:3000/websocket";
    private ArrayList<Room> Rooms;
    private ArrayList<Question> Questions;

    //Controlls
    private ListView roomList;
    private TextView status;
    private QuestionAdapter adapter; // using a custom Adapter for Rooms

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get reference to controls
        roomList = (ListView)findViewById(R.id.roomListView);
        status = (TextView)findViewById(R.id.statusText);

        // Init
        Rooms = new ArrayList<Room>();
        Questions = new ArrayList<Question>();
        adapter = new QuestionAdapter(this, Questions);

        roomList.setAdapter(adapter);

        //Connect Meteor
        mMeteor = new Meteor(this, mUrl);
        mMeteor.setCallback(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* Meteor Callbacks */
    @Override
    public void onConnect(boolean b) {
        status.setText("Connected to: "+mUrl);
        Log.d("SARA", "Connected");

        //Subscribing to collections
         //String id = mMeteor.subscribe("lectures", "rt2vi");
        String lectureId = "rt2vi";

        String id = mMeteor.subscribe("lectures", new Object[] { lectureId });
        mMeteor.subscribe("questions", new Object[] { lectureId });
        mMeteor.subscribe("votes", new Object[] { lectureId });
        Log.d("SARA", "Lecture Subscription id: "+id);
    }

    @Override
    public void onDisconnect(int i, String s) {
        status.setText("Disconnected..");
        Log.d("SARA", "Disconnected: "+s);
    }

    @Override
    public void onDataAdded(String s, String s1, String s2) {
        // s = Collection
        // s1 = id?
        // s2 = json

        // Parse json
        try {
            //JSONArray arr = new JSONArray(s2);
            JSONObject data = new JSONObject(s2);

        switch(s){
            case "questions":
                Question q = new Question(data.getString("questionText"), data.getString("lectureCode"), data.getString("author"));
                Questions.add(q);
                adapter.notifyDataSetChanged(); // tell listview to redraw itself
                break;
            case "votes":
                break;
        }
        //Log.d("SARA", "Questions: "+Questions.size());
       // Log.d("SARA", "Data Added: "+s+", "+s1+", "+s2);
        }catch(JSONException ex){
            Log.d("SARA", "Json Ex: "+ex.getMessage());
        }catch(Exception ex){
            Log.d("SARA", "Data Exception: "+ex.getMessage());
        }
    }

    @Override
    public void onDataChanged(String s, String s1, String s2, String s3) {
        Log.d("SARA", "Data changed: "+s+", "+s1+", "+s2+", "+s3);
    }

    @Override
    public void onDataRemoved(String s, String s1) {
        Log.d("SARA", "Data Removed: "+s+", "+s1);
    }

    @Override
    public void onException(Exception e) {
        Log.d("SARA", "Exception: "+e.getMessage());
    }
}
