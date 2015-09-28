package uia.is213.eirik.rewind;

import android.util.Log;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import im.delight.android.ddp.MeteorSingleton;

/**
 * Created by Eirik on 28.09.2015.
 */
public class Question {
    public String text;
    public String id;
    public String lectureCode;
    public String author;
    public Date date;
    public Integer votes;

    public Question(String id, String text, String lectureCode, String author){
        this.id = id;
        this.text = text;
        this.lectureCode = lectureCode;
        this.author = author;
        this.votes = 0;

        Log.d("SARA", "Question Created: " + text);
    }

    // Hmm.. Call Meteor Vote, server should know if we already voted or not.
    // Need access to Meteor object from Question class. should make static meteor class
    public void Vote(){
        // client/views/question/question.js
        //Object[] vote =  new Object[] { id, lectureCode };
        String json = String.format("questionId: %s, lectureCode: %s", id, lectureCode);

        /*
        Object[] methodArgs = new Object[1];
        Map<String,String> options = new HashMap<>();
        methodArgs[0] = options;
        options.put("questionId", id);
        options.put("lectureCode", lectureCode);
        */
        String[] values = { "questionId: "+ id, "lectureCode: "+lectureCode };
        MeteorSingleton.getInstance().call("voteInsert", values);
        Log.d("SARA", "comon..");

    }

    @Override
    public String toString(){
        return text;
    }
}
