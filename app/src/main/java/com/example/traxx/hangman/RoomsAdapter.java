package com.example.traxx.hangman;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Traxx on 04.02.2018.
 */

public class RoomsAdapter extends ArrayAdapter<Room>
{
    HashMap<String, Integer> roomPlayersMap = new HashMap<>();

    public void addRoomPlayersCount(String roomId, int playersCount)
    {
        roomPlayersMap.put(roomId, playersCount);
    }

    public RoomsAdapter(Context context, ArrayList<Room> users)
    {
        super(context, 0, users);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent)
    {
        // Get the data item for this position
        Room room = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_room, parent, false);
        }
        // Lookup view for data population
        TextView textViewRoomName = convertView.findViewById(R.id.textViewRoomName);
        TextView textViewPlayersCount = convertView.findViewById(R.id.textViewPlayersCount);
        Button buttonJoinRoom = convertView.findViewById(R.id.buttonJoinRoom);
        buttonJoinRoom.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ((ListView) parent).performItemClick(view, position, 0); // Let the event be handled in onItemClick()
            }
        });
        // Populate the data into the template view using the data object
        textViewRoomName.setText(room.getName());
        String playersCount;
        if (roomPlayersMap.containsKey(room.getId()))
        {
            playersCount = roomPlayersMap.get(room.getId()) + "/" + room.getMaxNumberOfPlayers();
        } else
        {
            playersCount = "?/" + room.getMaxNumberOfPlayers();
        }
        textViewPlayersCount.setText(playersCount);
        // Return the completed view to render on screen
        return convertView;
    }
}
