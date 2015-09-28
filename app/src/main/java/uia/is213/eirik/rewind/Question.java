package uia.is213.eirik.rewind;

import android.util.Log;

import java.util.Date;

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


    @Override
    public String toString(){
        return text;
    }
}
