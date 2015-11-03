package uia.is213.eirik.rewind;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import im.delight.android.ddp.Meteor;
import im.delight.android.ddp.MeteorCallback;
import im.delight.android.ddp.MeteorSingleton;
import im.delight.android.ddp.ResultListener;
import uia.is213.eirik.rewind.Comparators.QuestionVotesComparator;
import uia.is213.eirik.rewind.Models.Lecture;
import uia.is213.eirik.rewind.Models.Question;
import uia.is213.eirik.rewind.Models.QuestionAdapter;
import uia.is213.eirik.rewind.Models.User;
import uia.is213.eirik.rewind.Models.Vote;
import uia.is213.eirik.rewind.Comparators.QuestionAgeComparator;

/**
 *
 * Android app for Meteor project Rewind
 * uses the Meteor DDP Protocol, https://github.com/delight-im/Android-DDP
 * Eirik Kvarstein 28.09.2015
 *
 */

public class MainActivity extends AppCompatActivity implements MeteorCallback{

    //Vars
    private Meteor mMeteor;
    private static String mUrl = "ws://eirik.pw:3000/websocket"; //192.168.11.87
  //private static String mUrl = "192.168.11.87:3000/websocket";
    private ArrayList<Lecture> lectures;
    private ArrayList<Question> Questions;
    //private HashMap<Vote, Question> voteMap;
    private ArrayList<Vote> voteMap;
    private static String PREFS_NAME = "rewindAppPrefs";
    //Controlls
    private ListView questionList;
    private TextView status;
    private QuestionAdapter adapter; // using a custom Adapter for lectures
    private String dialogResult = "";
    private static  Context context;
    //Meteor stuff
    private Lecture currentLecture;
    private String defaultLectureCode = "pdc52";

    //Notifications
    NotificationManager notificationManager;

    //User
    private User localUser;

    public static Context getAppContext(){
        return context;
    }
    public static Integer users;

    /**
     * Callback for Activities started with StartActivityForResult
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK)
            return;

        switch(requestCode){
            case Constants.SETTINGS_RESULT:
                String ip = data.getExtras().getString("meteor_url");
                if(!ip.equals(mUrl)){
                    mUrl = ip;
                    //User changed IP, attempt to connect to new ip.
                    MeteorSingleton.getInstance().disconnect();
                    mMeteor = MeteorSingleton.createInstance(this, mUrl);
                    mMeteor.setCallback(this);
                }

                if(data.getExtras().getString("username") != null){
                    localUser.username = data.getExtras().getString("username");
                    localUser.email = data.getExtras().getString("email");
                    localUser.password = data.getExtras().getString("password");
                    Log("Reconnecting with new user details");
                    // Attempt to Auth with new data
                    mMeteor.logout();
                    currentLecture.Leave();
                    localUser.Authenticate();
                }
                Log("Server Ip: "+ip);
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Called when Activity is created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        users=5;
        context = getApplicationContext();
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        setContentView(R.layout.activity_main);

        //Get reference to controls
        questionList = (ListView)findViewById(R.id.questionListView);
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputDialog("Ask a question", new Callable<Boolean>() {
                    @Override
                    public Boolean call() {
                        return postQuestion();
                    }});
            }});
        //status = (TextView)findViewById(R.id.statusText);

        // Init
        lectures = new ArrayList<>();
        Questions = new ArrayList<>();
        voteMap = new ArrayList<>();
        adapter = new QuestionAdapter(this, Questions);

        questionList.setAdapter(adapter);
        questionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Question q = adapter.getItem(position);
                q.Vote();
                Log("Clicked item..");

            }
        });


        //Check if user data exists on device
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        //localUser = new User(settings.getString("username", "default"), settings.getString("password", "pass"), settings.getString("email", "default@localhost.com"));
        // Use Device ID as username and pass
        localUser = new User();

        defaultLectureCode = settings.getString("lectureCode", "odycl");
        mUrl = settings.getString("meteor_url", mUrl);
        //Connect Meteor
        mMeteor = MeteorSingleton.createInstance(this, mUrl);
        mMeteor.setCallback(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Called when An item in the ActionBar menu is clicked.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id){
            case R.id.action_settings:
                //Start SettingsActivity
                Intent intent = new Intent(this, SettingsActivity.class);
                intent.putExtra("meteor_url", mUrl);
                startActivityForResult(intent, Constants.SETTINGS_RESULT);
                return true;

            case R.id.action_ask:
                //Display Dialog and call postQuestion if user clicks OK.
                inputDialog("Ask a question", new Callable<Boolean>() {
                    @Override
                    public Boolean call() {
                        return postQuestion();
                    }
                });
                return true;

            case R.id.action_change:
                //Display Dialog and Enter new Lecture if user clicks OK.
                inputDialog("Enter Lecture Code", new Callable<Void>(){

                    @Override
                    public Void call() throws Exception {
                        changeLecture(dialogResult);
                        return null;
                    }
                });
                return true;

            case R.id.action_sort_age:
                Collections.sort(Questions,new QuestionAgeComparator());
                adapter.notifyDataSetChanged();
                break;

            case R.id.action_sort_votes:
                Collections.sort(Questions, new QuestionVotesComparator());
                adapter.notifyDataSetChanged();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Creates a Android Notification
     * @param title
     * @param text
     */
    private void notify(String title, String text){
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification n  = new Notification.Builder(this)
                .setContentTitle("Rewind - "+title)
                .setContentText(text)
                .setSmallIcon(R.drawable.questionmark)
              //  .setSmallIcon(R.drawable.icon)
                .setAutoCancel(true) //clears Notification when clicked on
                .setContentIntent(pIntent).build();

        notificationManager.notify(0, n);
    }

    /**
     * Posts a question using text from dialogResult to currentLecture
     * Meteor method: questionInsertAddVote ( client/views/lecture/lecture_page_footer.js )
     */
   private boolean postQuestion(){
       Object[] methodArgs = new Object[1];
       Map<String, String> options = new HashMap<>();

       options.put("lectureCode", currentLecture.code);
       options.put("questionText", dialogResult);
       methodArgs[0] = options;

       MeteorSingleton.getInstance().call("questionInsertAddVote", methodArgs);
       return true;
   }

    /**
     * Creates a new Lecture and subscribes to it.
     */
    private void createLecture() {
        MeteorSingleton.getInstance().call("lectureInsert", new ResultListener() {
            @Override
            public void onSuccess(String s) {
                try {
                    JSONObject res = new JSONObject(s);
                    String generatedLectureCode = res.getString("lectureCode");
                    changeLecture(generatedLectureCode);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String s, String s1, String s2) {

            }
        });
    }

    void Log(String msg){
        Log.d("SARA", msg);
    }


    /***************  Meteor Callbacks ************************
     * ********************************************************
     */

    @Override
    public void onConnect(boolean b) {
        //status.setText("Connected to: " + mUrl);
        Log("Connected");
        //Save meteor url
        KeyValueDB.setKeyValue("meteor_url", mUrl);

        //Clear Questions..
        Questions.clear();
        /*
         * Because of checks on this.userId on server, we must authenticate
         * so that userId is set on server.
         */
        localUser.Authenticate();

        currentLecture = new Lecture(defaultLectureCode); //debug
        this.setTitle("Rewind: " + currentLecture.code);
    }

    @Override
    public void onDisconnect(int i, String s) {
        //status.setText("Disconnected..");
        Questions.clear();
        //Log("Disconnected: " + s);
    }

    @Override
    public void onDataAdded(String Collection, String documentId, String newJsonVals) {
        // votes, 8n3hviBnRAaLK88bv, {"lectureCode":"pdc52","questionId":"9MdB2JyKpJuKzPNMX","author":"BZaHJSRMupGJxAJ47"}
        // questions, 2BpDfDmv3sNWgMxXQ, {"lectureCode":"pdc52","questionText":"Question #9","author":"j2npZDqK5aNgg7QGy","submitted":{"$date":1444738651297}}
        try {
            JSONObject data = new JSONObject(newJsonVals);

        switch(Collection){
            case "questions":
                Question q = new Question(documentId, data.getString("questionText"), data.getString("lectureCode"), data.getString("author"));
                String unix_time_in_ms = data.getJSONObject("submitted").getString("$date");
                long unix_time_ms = (long)Double.parseDouble(unix_time_in_ms);
                DateTime date = new DateTime(unix_time_ms);
                q.setDate(date);
                Questions.add(q);

                //Sort Questions
                Collections.sort(Questions,new QuestionAgeComparator());

                //If this is not our question, notify us
                if(!q.getAuthor().equals(localUser.getId()))
                    notify("New question..", q.getText());
                break;
            case "votes":
                Vote vote = new Vote(documentId, data.getString("lectureCode"), data.getString("author"), data.getString("questionId"));
                // Add vote to question
                // Find questionId
               // String author = data.getString("author");
               // String id = data.getString("questionId");
                if(vote.getId() == null){
                    Log("A vote was added without questionId");
                    throw new Exception("A vote was added without questionId");
                }

                //Find which Question this Vote belongs to
                Question question = qById(vote.getQuestionId());
                if(question != null){
                    question.upvote();
                    vote.setQuestion(question);

                    voteMap.add(vote);
                    //voteMap.put(vote, question); // We need to save the Vote ID, to identify Question when removing vote.

                    //Check if this was Our Vote.
                    if(vote.getAuthor().equals(localUser.getId())){
                        question.setHasVoted(true);
                        Log("*** Our Vote ***");
                    }
                }
                break;
        }

         adapter.notifyDataSetChanged(); // tell listview to redraw itself
        //Log.d("SARA", "Questions: "+Questions.size());
        Log("Data Added: "+Collection+", "+documentId+", "+newJsonVals);
        }catch(JSONException ex){
            Log("Json Ex: "+ex.getMessage());
        }catch(Exception ex){
            Log("Data Exception: "+ex.getMessage());
        }
    }

    @Override
    public void onDataChanged(String Collection, String documentId, String updatedVals, String removedVals) {
        Log("Data changed: " + Collection + ", " + documentId + ", " + updatedVals + ", " + removedVals);
    }

    @Override
    public void onDataRemoved(String Collection, String id) {
        // votes, 2sNMN6oxnsj5eAEaC
        switch(Collection){

            case "votes":
                //Lookup Question from voteMap
                Vote v = vById(id);
                if(v != null){
                    Question q = v.getQuestion();
                    if(q != null){
                        q.downvote(); // Downvote
                        voteMap.remove(v); // Remove vote from voteMap.

                        //Check if this was Our Vote
                       if(v.getAuthor().equals(localUser.getId())){
                           q.setHasVoted(false);
                       }
                    }
                }

                break;

            //When we unsubscribe from questions the server will tell us the id of questions we no longer have access to
            case "questions":
                if(qById(id) != null){
                    Questions.remove(qById(id));
                }
                break;
        }
        adapter.notifyDataSetChanged();
        Log("Data Removed: " + Collection + ", " + id);
    }

    @Override
    public void onException(Exception e) {
        Log("Exception: "+e.getMessage());
    }


    /********************** HELPERS *****************
     * **********************************************
     */


    /***
     * Unsubscribes from currentLecture and enters new
     * @param code - lectureCode
     */
    private void changeLecture(String code){
        if(currentLecture != null){
            currentLecture.Leave();
        }
        //Questions.clear();
        currentLecture = new Lecture(code);
        this.setTitle("Rewind: "+currentLecture.code);
    }

    /**
     * Displays inputDialog and calls Callable func on successs ( user clicks OK )
     * @param title
     * @param func
     */
    private void inputDialog(String title, final Callable func) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        alert.setTitle(title);
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialogResult = input.getText().toString();
                try {
                    func.call();
                } catch (Exception ex) {
                    //Callback raised exception

                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        alert.show();
    }

    private Question qById(String id){
        for (Question q : Questions) {
            if (q.getId().equals(id))
                return q;
        }
            Log("qById no match: "+id);
            return null;
    }

    private Vote vById(String id){
        for(Vote v : voteMap){
            if(v.getId().equals(id))
                return v;
        }
        Log("vById no match: "+id);
        return null;
    }

}
