package uia.is213.eirik.rewind;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Eirik on 28.09.2015.
 * Custom adapter writeup: https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
 */
public class QuestionAdapter extends ArrayAdapter<Question> {
    private int[] colors = new int[]{ 0xffF8F8F8, 0xffffffff };
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
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
       // TextView tvUsers = (TextView) convertView.findViewById(R.id.textViewUsers);
        TextView tvVotes = (TextView) convertView.findViewById(R.id.tvVotes);

        //ProgressBar tvVotes = (ProgressBar)convertView.findViewById(R.id.tvVoteProgress);
        // Populate the data into the template view using the data object
        tvDate.setText(q.age);
        tvName.setText(q.text);
        tvVotes.setText(String.format("%d / %d", q.votes, MainActivity.users));
       // tvVotes.setProgress(q.votes);
       // tvVotes.setMax(MainActivity.users);

        int colorpos = position % colors.length;
        //super.getView(position, convertView, parent).setBackgroundColor(colors[colorpos]);
        //tvVotes.setText(q.votes.toString());
        convertView.setBackgroundColor(colors[colorpos]);
        // Return the completed view to render on screen
        return convertView;
    }

}
