package com.example.traxx.hangman;

/**
 * Created by Traxx on 05.02.2018.
 */

public class Game
{
    private String name;
    private String wordToGuess;
    private String hint;
    private String winnerId = "";

    public String getWinnerId()
    {
        return winnerId;
    }

    public void setWinnerId(String winnerId)
    {
        this.winnerId = winnerId;
    }

    public String getHint()
    {
        return hint;
    }

    public void setHint(String hint)
    {
        this.hint = hint;
    }

    public Game()
    {
    }

    public Game(String name, String wordToGuess, String hint)
    {
        this.name = name;
        this.wordToGuess = wordToGuess;
        this.hint = hint;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getWordToGuess()
    {
        return wordToGuess;
    }

    public void setWordToGuess(String wordToGuess)
    {
        this.wordToGuess = wordToGuess;
    }
}
