package com.example.traxx.hangman;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Traxx on 04.02.2018.
 */

public class PlayersInRoomAdapter extends ArrayAdapter<Player>
{
    Player currentPlayer;
    public PlayersInRoomAdapter(Context context, ArrayList<Player> players)
    {
        super(context, 0, players);
    }

    public void setCurrentPlayer(Player currentPlayer)
    {
        this.currentPlayer = currentPlayer;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent)
    {
        // Get the data item for this position
        Player player = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_player_in_room, parent, false);
        }
        // Lookup view for data population
        TextView textViewPlayerName = convertView.findViewById(R.id.textViewPlayerName);
        ImageButton imageButtonKickPlayer = convertView.findViewById(R.id.imageButtonKickUser);
        if(currentPlayer != null && currentPlayer.isHost())
        {
            if(player.getId() != currentPlayer.getId())
            {
                imageButtonKickPlayer.setVisibility(View.VISIBLE);
            }
            else
            {
                imageButtonKickPlayer.setVisibility(View.INVISIBLE);
            }
        }
        else
        {
            imageButtonKickPlayer.setVisibility(View.INVISIBLE);
        }
        imageButtonKickPlayer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ((ListView) parent).performItemClick(view, position, 0); // Let the event be handled in onItemClick()
            }
        });
        // Populate the data into the template view using the data object
        textViewPlayerName.setText(player.getName());
        notifyDataSetChanged();
        // Return the completed view to render on screen
        return convertView;
    }
}

