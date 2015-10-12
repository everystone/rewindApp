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
    //
    public void Vote(){
        /* client/views/question/question.js
         * from votes.js:
         * 	check(this.userId, String); // this is null
            check(voteAttributes, {
            questionId: String,
            lectureCode: String
            });
         */
        //Object[] vote =  new Object[] { id, lectureCode };



        Object[] methodArgs = new Object[1];
        Map<String,String> options = new HashMap<>();

        options.put("questionId", id);
        options.put("lectureCode", lectureCode);
        methodArgs[0] = options;

        MeteorSingleton.getInstance().call("voteInsert", methodArgs);
       // Log.d("SARA", "comon..");

    }

    @Override
    public String toString(){
        return text;
    }
}
