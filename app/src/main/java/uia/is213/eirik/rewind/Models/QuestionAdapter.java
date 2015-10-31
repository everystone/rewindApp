package uia.is213.eirik.rewind.Models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import uia.is213.eirik.rewind.MainActivity;
import uia.is213.eirik.rewind.R;

/**
 * Created by Eirik on 28.09.2015.
 * Custom adapter writeup: https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
 */
public class QuestionAdapter extends ArrayAdapter<Question> {
   // private int[] colors = new int[]{ 0xffF8F8F8, 0xffffffff };
    private int[] colors = new int[] { 0xffFFF3E0, 0xffFFE0B2};
    public QuestionAdapter(Context ctx, ArrayList<Question> questions){
        super(ctx, 0, questions);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        // Get the data item for this position
        final Question q = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_question, parent, false);
        }
        // Lookup view for data population
        TextView tvDate = (TextView)convertView.findViewById(R.id.tvDate);
        CheckBox cb = (CheckBox)convertView.findViewById(R.id.star);
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
       // TextView tvUsers = (TextView) convertView.findViewById(R.id.textViewUsers);
        TextView tvVotes = (TextView) convertView.findViewById(R.id.tvVotes);

        //ProgressBar tvVotes = (ProgressBar)convertView.findViewById(R.id.tvVoteProgress);
        // Populate the data into the template view using the data object
        tvDate.setText(q.refreshAge());
        cb.setChecked(q.isHasVoted());
        tvName.setText(q.getText());
        tvVotes.setText(String.format("%d / %d", q.getVotes(), MainActivity.users));
       // tvVotes.setProgress(q.votes);
       // tvVotes.setMax(MainActivity.users);
/*
        if(q.getColor() == null) {
            int colorpos = position % colors.length;
            q.setColor(colors[colorpos]);
        }
        convertView.setBackgroundColor(q.getColor());
        */
        // Return the completed view to render on screen
        return convertView;
    }

}
