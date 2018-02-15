package com.example.traxx.hangman;

/**
 * Created by Traxx on 15.02.2018.
 */

public class Question
{
    private String hint;
    private String wordToGuess;

    public Question()
    {
    }

    public Question(String hint, String wordToGuess)
    {
        this.hint = hint;
        this.wordToGuess = wordToGuess;
    }

    public String getHint()
    {
        return hint;
    }

    public void setHint(String hint)
    {
        this.hint = hint;
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
