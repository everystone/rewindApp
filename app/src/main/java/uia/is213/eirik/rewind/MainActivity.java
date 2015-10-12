package uia.is213.eirik.rewind;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import im.delight.android.ddp.ResultListener;

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
    private static String mUrl = "ws://192.168.11.87:3000/websocket";
    private ArrayList<Lecture> lectures;
    private ArrayList<Question> Questions;
    private HashMap<String, Question> voteMap;
    //Controlls
    private ListView questionList;
    private TextView status;
    private QuestionAdapter adapter; // using a custom Adapter for lectures
    private String dialogResult = "EMPTY";

    //Meteor stuff

    private Lecture currentLecture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get reference to controls
        questionList = (ListView)findViewById(R.id.questionListView);
        status = (TextView)findViewById(R.id.statusText);

        // Init
        lectures = new ArrayList<Lecture>();
        Questions = new ArrayList<Question>();
        voteMap = new HashMap<>();
        adapter = new QuestionAdapter(this, Questions);
        //Attach Click Listener to adapter ( Question List )


        questionList.setAdapter(adapter);

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

        //Authenticate?
        mMeteor.loginWithUsername("john", "password", new ResultListener() {
            @Override
            public void onSuccess(String s) {

            }

            @Override
            public void onError(String s, String s1, String s2) {

            }
        });

        currentLecture = new Lecture("qjf9m");
    }

    @Override
    public void onDisconnect(int i, String s) {
        status.setText("Disconnected..");
        Questions.clear();
        Log("Disconnected: " + s);
    }

    @Override
    public void onDataAdded(String Collection, String documentId, String newJsonVals) {
        // Collection = Collection
        // documentId = id?
        // newJsonVals = json
        try {
            JSONObject data = new JSONObject(newJsonVals);

        switch(Collection){
            case "questions":
                Question q = new Question(documentId, data.getString("questionText"), data.getString("lectureCode"), data.getString("author"));
                Questions.add(q);
                break;
            case "votes":
                // Add vote to question
                // Find questionId
                String id = data.getString("questionId");
                if(id == null){
                    Log("A vote was added without questionId");
                    throw new Exception("A vote was added without questionId");
                }

                Question question = qById(id);
                if(question != null){
                    question.votes++;
                    voteMap.put(documentId, question); // We need to save the Vote ID, to identify Question when removing vote.
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
        switch(Collection){

            case "votes":
                //Lookup Question from voteMap
                if(voteMap.containsKey(id)){
                    Question q = voteMap.get(id);
                    if(q != null){
                        q.votes--; // Downvote
                        voteMap.remove(id); // Remove vote from voteMap.
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
}
