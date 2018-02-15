package com.example.traxx.hangman;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Traxx on 05.02.2018.
 */

public class PlayerScore
{
    private String name;
    private String id;
    private int attempts = 0;
    private List<String> guessedLetters;
    private boolean isMakingMove = false;
    private boolean isHost;
    private boolean isLoser = false;

    public PlayerScore()
    {
    }

    public PlayerScore(String name, String id, boolean isMakingMove, boolean isHost)
    {
        this.name = name;
        this.id = id;
        this.isMakingMove = isMakingMove;
        this.isHost = isHost;
    }

    public boolean isLoser()
    {
        return isLoser;
    }

    public void setLoser(boolean loser)
    {
        isLoser = loser;
    }

    public boolean isHost()
    {
        return isHost;
    }

    public void setHost(boolean host)
    {
        isHost = host;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public int getAttempts()
    {
        return attempts;
    }

    public void setAttempts(int attempts)
    {
        this.attempts = attempts;
    }

    public List<String> getGuessedLetters()
    {
        if(guessedLetters == null)
        {
            guessedLetters = new ArrayList<>();
        }
        return guessedLetters;
    }

    public void setGuessedLetters(List<String> guessedLetters)
    {
        this.guessedLetters = guessedLetters;
    }

    public boolean isMakingMove()
    {
        return isMakingMove;
    }

    public void setMakingMove(boolean makingMove)
    {
        isMakingMove = makingMove;
    }
}