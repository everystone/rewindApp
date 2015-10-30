package uia.is213.eirik.rewind;

import android.graphics.Color;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;
import org.joda.time.format.PeriodFormatter;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import im.delight.android.ddp.MeteorSingleton;

/**
 * Created by Eirik on 28.09.2015.
 */
public class Question {
    private String text;
    private String id;
    private String lectureCode;
    private String author;
    private String age;
    private DateTime date;
    private Integer votes;

    public String getText() {return text;}
    public Integer getVotes() {return votes;}
    public String getId() {return id;}
    public String getAge() {return age;}
    public void setAge(String age) {this.age = age;}
    public void setVotes(Integer votes) {this.votes = votes;}
    public void setText(String text) {this.text = text;}
    public Integer getColor() {return color;}
    public void setColor(Integer color) {this.color = color;}
    private Integer color;
    private boolean hasVoted = false; // If App user has voted on this question
    public void setHasVoted(boolean b){
        this.hasVoted = b;
        if(b){
            color = Color.argb(100, 0, 200, 0);
        }else {
            color =null;
        }
    }
    public void upvote(){ votes++; }
    public void downvote(){ votes--; }

    public Question(String id, String text, String lectureCode, String author){
        this.id = id;
        this.text = text;
        this.lectureCode = lectureCode;
        this.author = author;
        this.votes = 0;

        Log.d("SARA", "Question Created: " + text);
    }
    public void setDate(DateTime date) {
        this.date = date;
        DateTime now = new DateTime();
        /*
        Period period = new Period( date, now );
        PeriodFormatter periodFormatter = PeriodFormat.wordBased();
        String output = periodFormatter.print( period );
        this.age = output;
        */
       age = Minutes.minutesBetween(date, now).getMinutes()+"m";
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

        // If App user has not voted on this question
        if(!hasVoted) {
            Object[] methodArgs = new Object[1];
            Map<String, String> options = new HashMap<>();

            options.put("questionId", id);
            options.put("lectureCode", lectureCode);
            methodArgs[0] = options;

            MeteorSingleton.getInstance().call("voteInsert", methodArgs);
           // this.color = R.color.accent;
            // No need to set hasVoted to true here, when we receive the Vote back from the server, it will be sat to true.
        }else {
            // Remove our Vote
            this.color = null;
            Log.d("SARA", "Removing our vote: "+id);
            MeteorSingleton.getInstance().call("voteDelete", new Object[]{ id});


        }

    }



    @Override
    public String toString(){
        return text;
    }

    /**
     *     Postes a question using text from dialogResult to currentLecture
        Meteor method: questionInsertAddVote ( client/views/lecture/lecture_page_footer.js )
     */

    private static boolean postQuestion(String lectureCode, String text){
        Object[] methodArgs = new Object[1];
        Map<String, String> options = new HashMap<>();

        options.put("lectureCode", lectureCode);
        options.put("questionText", text);
        methodArgs[0] = options;

        MeteorSingleton.getInstance().call("questionInsertAddVote", methodArgs);
        return true;
    }

    public String getAuthor() {
        return author;
    }
}
