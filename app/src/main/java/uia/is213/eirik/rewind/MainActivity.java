package uia.is213.eirik.rewind;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;



import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import im.delight.android.ddp.Meteor;
import im.delight.android.ddp.MeteorCallback;
import im.delight.android.ddp.MeteorSingleton;

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
    public static String PREFS_NAME = "rewindAppPrefs";
    //Controlls
    private ListView questionList;
    private TextView status;
    private TextView lectureCode;
    private QuestionAdapter adapter; // using a custom Adapter for lectures
    private String dialogResult = "";
    private static  Context context;
    //Meteor stuff
    private Lecture currentLecture;
    private String defaultLectureCode = "pdc52";

    //User
    private User localUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();

        setContentView(R.layout.activity_main);

        //Get reference to controls
        questionList = (ListView)findViewById(R.id.questionListView);
        status = (TextView)findViewById(R.id.statusText);
        lectureCode = (TextView)findViewById(R.id.lectureCode);

        // Init
        lectures = new ArrayList<Lecture>();
        Questions = new ArrayList<Question>();
        voteMap = new ArrayList<Vote>();
        adapter = new QuestionAdapter(this, Questions);

       //Attach Click Listener to adapter ( Question List )
        questionList.setAdapter(adapter);
        questionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Question q = adapter.getItem(position);
                q.Vote();
                //Toast.makeText(getApplicationContext(), q.text, Toast.LENGTH_SHORT).show();

            }
        });


        //Check if user data exists on device
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        localUser = new User(settings.getString("username", "default"), settings.getString("password", "pass"), settings.getString("email", "default@localhost.com"));

        defaultLectureCode = settings.getString("lectureCode", "odycl");

        //Connect Meteor
        mMeteor = MeteorSingleton.createInstance(this, mUrl);
        mMeteor.setCallback(this);
    }

    public static Context getAppContext(){
        return context;
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


        if (id == R.id.action_settings) {
            return true;

            /*
             *   ASK A QUESTION
             */
        }else if(id == R.id.action_ask){

            //Display Dialog and call postQuestion if user clicks OK.
            inputDialog("Ask a question", new Callable<Boolean>() {
                @Override
                public Boolean call() {
                    return postQuestion();
                }
            });
            return true;
            /*
             *   CHANGE LECTURE ROOM
             */
        }else if(id == R.id.action_change){
            //Change Lecture

            //Display Dialog and Enter new Lecture if user clicks OK.
            inputDialog("Enter Lecture Code", new Callable<Void>(){

                @Override
                public Void call() throws Exception {
                    if(currentLecture != null){
                        currentLecture.Leave();
                    }
                    //Questions.clear();
                    currentLecture = new Lecture(dialogResult);
                    lectureCode.setText(currentLecture.code);

                    return null;
                }
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Postes a question using text from dialogResult to currentLecture
    //Meteor method: questionInsertAddVote ( client/views/lecture/lecture_page_footer.js )
   private boolean postQuestion(){
       Object[] methodArgs = new Object[1];
       Map<String,String> options = new HashMap<>();

       options.put("lectureCode", currentLecture.code);
       options.put("questionText", dialogResult);
       methodArgs[0] = options;

       MeteorSingleton.getInstance().call("questionInsertAddVote", methodArgs);
       return true;
    }

    /* input dialog
     * @parameter title: Title to display
     * @parameter: func - callback on success (OK button)
     * Stores User Input in dialogResult.
     * */
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


    void Log(String msg){
        Log.d("SARA", msg);
    }
    /* Meteor Callbacks */
    @Override
    public void onConnect(boolean b) {
        status.setText("Connected to: " + mUrl);
        Log("Connected");

        //Clear Questions..
        Questions.clear();

        /*
         * Because of checks on this.userId on server, we must authenticate
         * so that this.userId is set on server.
         */
        localUser.Authenticate();

        currentLecture = new Lecture(defaultLectureCode); //debug
        lectureCode.setText(currentLecture.code);
    }

    @Override
    public void onDisconnect(int i, String s) {
        status.setText("Disconnected..");
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
                Questions.add(q);
                break;
            case "votes":
                Vote vote = new Vote(documentId, data.getString("lectureCode"), data.getString("author"), data.getString("questionId"));
                // Add vote to question
                // Find questionId
               // String author = data.getString("author");
               // String id = data.getString("questionId");
                if(vote.id == null){
                    Log("A vote was added without questionId");
                    throw new Exception("A vote was added without questionId");
                }

                //Find which Question this Vote belongs to
                Question question = qById(vote.questionId);
                if(question != null){
                    question.votes++;
                    vote.setQuestion(question);

                    voteMap.add(vote);
                    //voteMap.put(vote, question); // We need to save the Vote ID, to identify Question when removing vote.

                    //Check if this was Our Vote.
                    if(vote.author.equals(localUser.getId())){
                        question.hasVoted = true;
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
                        q.votes--; // Downvote
                        voteMap.remove(v); // Remove vote from voteMap.

                        //Check if this was Our Vote
                       if(v.author.equals(localUser.getId())){
                           q.hasVoted = false;
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

    //Misc helpers
    private Question qById(String id){
        for (Question q : Questions) {
            if(q.id.equals(id))
                return q;
        }
            Log("qById no match: "+id);
            return null;
    }

    private Vote vById(String id){
        for(Vote v : voteMap){
            if(v.id.equals(id))
                return v;
        }
        Log("vById no match: "+id);
        return null;
    }

}
