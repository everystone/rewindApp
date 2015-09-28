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
 */
public class QuestionAdapter extends ArrayAdapter<Question> {
    public QuestionAdapter(Context ctx, ArrayList<Question> questions){
        super(ctx, 0, questions);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        // Get the data item for this position
        Question q = getItem(position);
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
        // Return the completed view to render on screen
        return convertView;
    }

}
