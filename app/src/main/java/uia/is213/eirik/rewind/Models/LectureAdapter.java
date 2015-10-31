package uia.is213.eirik.rewind.Models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import uia.is213.eirik.rewind.R;

/**
 * Created by Eirik on 28.09.2015.
 */
public class LectureAdapter extends ArrayAdapter<Lecture> {
    public LectureAdapter(Context ctx, ArrayList<Lecture> lectures){
        super(ctx, 0, lectures);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        // Get the data item for this position
        Lecture lecture = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_room, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvOwner = (TextView) convertView.findViewById(R.id.tvOwner);
        // Populate the data into the template view using the data object
        tvName.setText(lecture.name);
        tvOwner.setText(lecture.owner);
        // Return the completed view to render on screen
        return convertView;
    }

}
