package com.example.traxx.hangman;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Traxx on 03.02.2018.
 */

@IgnoreExtraProperties
public class Room {
    private String id;
    private String name;
    private int maxNumberOfPlayers;
    private boolean isGameStarted = false;

    public boolean isGameStarted()
    {
        return isGameStarted;
    }

    public void setGameStarted(boolean gameStarted)
    {
        isGameStarted = gameStarted;
    }

    public Room() {
        // Default constructor required for calls to DataSnapshot.getValue(Room.class)
    }

    public Room(String id, String name, int maxNumberOfPlayers) {
        this.id = id;
        this.name = name;
        this.maxNumberOfPlayers = maxNumberOfPlayers;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getMaxNumberOfPlayers() {
        return maxNumberOfPlayers;
    }
}
