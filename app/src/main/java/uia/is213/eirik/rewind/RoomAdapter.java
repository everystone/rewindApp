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
public class RoomAdapter extends ArrayAdapter<Room> {
    public RoomAdapter(Context ctx, ArrayList<Room> rooms){
        super(ctx, 0, rooms);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        // Get the data item for this position
        Room room = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_room, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvOwner = (TextView) convertView.findViewById(R.id.tvOwner);
        // Populate the data into the template view using the data object
        tvName.setText(room.name);
        tvOwner.setText(room.owner);
        // Return the completed view to render on screen
        return convertView;
    }

}
