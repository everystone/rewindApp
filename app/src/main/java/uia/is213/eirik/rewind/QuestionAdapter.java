package uia.is213.eirik.rewind;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Eirik on 28.09.2015.
 * Custom adapter writeup: https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
 */
public class QuestionAdapter extends ArrayAdapter<Question> {
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
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvVotes = (TextView) convertView.findViewById(R.id.tvVotes);
        // Populate the data into the template view using the data object
        tvName.setText(q.text);
        tvVotes.setText(q.votes.toString());

        //Create animation
/*
        final AlphaAnimation anim2 = new AlphaAnimation(0.7f, 0.0f);
        //final TranslateAnimation anim2 = new TranslateAnimation(0, 200, 0, 0);
        anim2.setFillAfter(false);
        anim2.setFillBefore(true);
        anim2.setDuration(200);



        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                q.Vote();
              //  v.startAnimation(anim2);

                //v.animate
                // v.setBackgroundColor(120);
                // Toast.makeText(getContext(), q.text, Toast.LENGTH_SHORT).show();
            }
        });
*/
        // Return the completed view to render on screen
        return convertView;
    }

}
