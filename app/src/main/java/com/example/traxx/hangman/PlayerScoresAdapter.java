package com.example.traxx.hangman;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Traxx on 06.02.2018.
 */

public class PlayerScoresAdapter extends ArrayAdapter<PlayerScore>
{
    public PlayerScoresAdapter(Context context, ArrayList<PlayerScore> playerScores)
    {
        super(context, 0, playerScores);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent)
    {
        PlayerScore playerScore = getItem(position);
        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_player_score, parent, false);
        }
        TextView textViewPlayerName = convertView.findViewById(R.id.textViewPlayerName);
        ImageView imageViewAttempts = convertView.findViewById(R.id.imageViewAttempts);
        textViewPlayerName.setText(playerScore.getName());
        if (playerScore.isMakingMove())
        {
            textViewPlayerName.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
        }
        else
        {
            textViewPlayerName.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
        }
        switch (playerScore.getAttempts())
        {
            case 0:
                imageViewAttempts.setImageResource(R.drawable.attempt_0);
                break;
            case 1:
                imageViewAttempts.setImageResource(R.drawable.attempt_1);
                break;
            case 2:
                imageViewAttempts.setImageResource(R.drawable.attempt_2);
                break;
            case 3:
                imageViewAttempts.setImageResource(R.drawable.attempt_3);
                break;
            case 4:
                imageViewAttempts.setImageResource(R.drawable.attempt_4);
                break;
            case 5:
                imageViewAttempts.setImageResource(R.drawable.attempt_5);
                break;
            case 6:
                imageViewAttempts.setImageResource(R.drawable.attempt_6);
                break;
            case 7:
                imageViewAttempts.setImageResource(R.drawable.attempt_7);
                break;
            case 8:
                imageViewAttempts.setImageResource(R.drawable.attempt_8);
                break;
            case 9:
                imageViewAttempts.setImageResource(R.drawable.attempt_9);
                break;
            case 10:
                imageViewAttempts.setImageResource(R.drawable.attempt_10);
                break;
        }
        return convertView;
    }
}
