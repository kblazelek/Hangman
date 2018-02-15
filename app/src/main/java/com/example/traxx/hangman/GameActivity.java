package com.example.traxx.hangman;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GameActivity extends AppCompatActivity implements View.OnClickListener
{
    TextView textViewGameName;
    TextView textViewHint;
    TextView textViewWordToGuess;
    TextView textViewDiscoveredLetters;
    Button buttonDiscoverLetter;
    ListView listViewPlayerScores;
    String gameId;
    Player currentPlayer;
    String wordToGuess;
    DatabaseReference database;
    ArrayList<PlayerScore> playerScores;
    PlayerScoresAdapter adapter;
    List<String> alphabet;
    List<String> guessedLetters;
    PlayerScore currentPlayerScore;
    Game currentGame;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        database = FirebaseDatabase.getInstance().getReference();
        playerScores = new ArrayList<>();
        guessedLetters = new ArrayList<>();
        initializeAlphabetList();
        getParametersFromIntent();
        registerViews();
        registerPlayerScoresAdapter();
        registerGameListener();
        registerPlayerScoresListener();
        createPlayerScore();
    }

    private void createPlayerScore()
    {
        PlayerScore playerScore = new PlayerScore(currentPlayer.getName(), currentPlayer.getId(), currentPlayer.isHost(), currentPlayer.isHost());
        database.child("playerScores").child(gameId).child(currentPlayer.getId()).setValue(playerScore);
    }

    private void initializeAlphabetList()
    {
        char[] tempAlphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        alphabet = new ArrayList<>();
        for (char letter : tempAlphabet)
        {
            alphabet.add(Character.toString(letter));
        }
    }

    private void registerPlayerScoresAdapter()
    {
        adapter = new PlayerScoresAdapter(this, playerScores);
        ListView listView = findViewById(R.id.listViewPlayerScores);
        listView.setAdapter(adapter);
    }

    private void getParametersFromIntent()
    {
        Bundle b = getIntent().getExtras();
        if (b != null)
        {
            gameId = b.getString("gameId");
            currentPlayer = b.getParcelable("player");
        }
    }

    private void registerGameListener()
    {
        Query gameQuery = database.child("games").child(gameId);
        ValueEventListener startGameListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Game game = dataSnapshot.getValue(Game.class);
                if (game != null)
                {
                    currentGame = game;
                    if (!game.getWinnerId().equals("")) // Game has winner => game is over, go back to room
                    {
                        ShowEndGameDialog();
                        return;
                    }
                    textViewGameName.setText(game.getName());
                    textViewHint.setText(game.getHint());
                    wordToGuess = game.getWordToGuess();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Toast.makeText(getApplicationContext(), databaseError.toException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        gameQuery.addValueEventListener(startGameListener);
    }

    private void registerPlayerScoresListener()
    {
        final Query playerScoresQuery = database.child("playerScores").child(gameId);
        ValueEventListener playerScoresListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                playerScores.clear();
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren())
                {
                    PlayerScore playerScore = singleSnapshot.getValue(PlayerScore.class);
                    if (playerScore.getId().equals(currentPlayer.getId()))
                    {
                        StringBuilder builder = new StringBuilder();
                        guessedLetters.clear();
                        guessedLetters.addAll(playerScore.getGuessedLetters());
                        for (String character : guessedLetters)
                        {
                            builder.append(character).append(',');
                        }
                        if (builder.length() > 0)
                        {
                            builder.deleteCharAt(builder.length() - 1);
                        }
                        textViewDiscoveredLetters.setText(builder.toString());
                        textViewWordToGuess.setText(GetMaskedWordToGuess(wordToGuess, guessedLetters));
                    }
                    playerScores.add(playerScore);
                }
                for (PlayerScore playerScore : playerScores)
                {
                    if (playerScore.getId().equals(currentPlayer.getId()))
                    {
                        currentPlayerScore = playerScore;
                        break;
                    }
                }
                if (CountActivePlayers(playerScores) == 1)
                {
                    if (!currentPlayerScore.isLoser() && playerScores.size() != 1) // find better way to handle single player being a winner
                    {
                        // Current player has won
                        currentGame.setWinnerId(currentPlayerScore.getId());
                        database.child("rooms").child(gameId).child("gameStarted").setValue(false);
                        database.child("games").child(gameId).setValue(currentGame);
                    }
                }
                buttonDiscoverLetter.setEnabled(currentPlayerScore.isMakingMove());
                buttonDiscoverLetter.setClickable(currentPlayerScore.isMakingMove());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Toast.makeText(getApplicationContext(), databaseError.toException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        playerScoresQuery.addValueEventListener(playerScoresListener);
    }

    private void registerViews()
    {
        textViewGameName = findViewById(R.id.textViewGameName);
        textViewHint = findViewById(R.id.textViewHint);
        textViewWordToGuess = findViewById(R.id.textViewWordToGuess);
        textViewDiscoveredLetters = findViewById(R.id.textViewDiscoveredLetters);
        buttonDiscoverLetter = findViewById(R.id.buttonDiscoverLetter);
        listViewPlayerScores = findViewById(R.id.listViewPlayerScores);
        buttonDiscoverLetter.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.buttonDiscoverLetter:
                buttonDiscoverLetter.setClickable(false);
                buttonDiscoverLetter.setEnabled(false);
                ShowDiscoverLetterDialog();
                break;
        }
    }

    private void ShowDiscoverLetterDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick a letter to discover");

        List<String> lettersToDiscover = new ArrayList<>();
        lettersToDiscover.addAll(alphabet);
        lettersToDiscover.removeAll(guessedLetters);
        final CharSequence[] charSequence = new CharSequence[lettersToDiscover.size()];
        for (int i = 0; i < lettersToDiscover.size(); i++)
        {
            charSequence[i] = lettersToDiscover.get(i);
        }
        builder.setItems(charSequence, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

                PlayerScore nextPlayerScore = GetNextPlayerScore(currentPlayerScore, playerScores);
                String characterToDiscover = Character.toString(charSequence[which].charAt(0));
                currentPlayerScore.getGuessedLetters().add(characterToDiscover);
                if (wordToGuess.contains(characterToDiscover))
                {
                    String maskedWordToGuess = GetMaskedWordToGuess(wordToGuess, currentPlayerScore.getGuessedLetters());
                    textViewWordToGuess.setText(maskedWordToGuess);
                    if (!maskedWordToGuess.contains("_")) // All letters have been discovered => player has won
                    {
                        currentGame.setWinnerId(currentPlayerScore.getId());
                        database.child("rooms").child(gameId).child("gameStarted").setValue(false);
                        database.child("games").child(gameId).setValue(currentGame);
                        return;
                    }
                } else
                {
                    currentPlayerScore.setAttempts(currentPlayerScore.getAttempts() + 1);
                    if (currentPlayerScore.getAttempts() >= 10)
                    {
                        currentPlayerScore.setLoser(true);
                    }
                }
                nextPlayerScore.setMakingMove(true);
                currentPlayerScore.setMakingMove(false);
                database.child("playerScores").child(gameId).child(currentPlayerScore.getId()).setValue(currentPlayerScore);
                database.child("playerScores").child(gameId).child(nextPlayerScore.getId()).setValue(nextPlayerScore);
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            public void onCancel(DialogInterface dialog)
            {
                buttonDiscoverLetter.setEnabled(true);
                buttonDiscoverLetter.setClickable(true);
            }
        });
        builder.show();
    }

    private String GetMaskedWordToGuess(String wordToGuess, List<String> lettersToDiscover)
    {
        StringBuilder tempWordToGuess = new StringBuilder();
        for (char c : wordToGuess.toCharArray())
        {
            if (lettersToDiscover.contains(Character.toString(c)))
            {
                tempWordToGuess.append(c);
            } else
            {
                tempWordToGuess.append('_');
            }
        }
        return tempWordToGuess.toString();
    }

    @Override
    public void onBackPressed()
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                switch (which)
                {
                    case DialogInterface.BUTTON_POSITIVE:
                        if (currentPlayer.isHost())
                        {
                            currentPlayerScore.setLoser(true);
                            currentPlayerScore.setHost(false);
                            PlayerScore nextHost = GetNextPlayerScore(currentPlayerScore, playerScores);
                            nextHost.setHost(true);
                            database.child("playerScores").child(gameId).child(currentPlayerScore.getId()).setValue(currentPlayerScore);
                            database.child("playerScores").child(gameId).child(nextHost.getId()).setValue(nextHost);
                        } else
                        {
                            currentPlayerScore.setLoser(true);
                            database.child("playerScores").child(gameId).child(currentPlayerScore.getId()).setValue(currentPlayerScore);
                        }
                        finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
        builder.setMessage("Do you want to leave current game?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private int CountActivePlayers(ArrayList<PlayerScore> playerScores)
    {
        int activePlayers = 0;
        for (PlayerScore playerScore : playerScores)
        {
            if (!playerScore.isLoser())
            {
                activePlayers += 1;
            }
        }
        return activePlayers;
    }

    private PlayerScore GetNextPlayerScore(PlayerScore currentPlayerScore, ArrayList<PlayerScore> playerScores)
    {
        PlayerScore nextPlayerScore = null;
        PlayerScore tempPlayerScore;
        int currentPlayerScoreIndex = playerScores.indexOf(currentPlayerScore);
        int playerScoresSize = playerScores.size();
        for (int i = (currentPlayerScoreIndex + 1) % playerScoresSize; i != currentPlayerScoreIndex; i++)
        {
            tempPlayerScore = playerScores.get(i);
            if (!tempPlayerScore.isLoser())
            {
                nextPlayerScore = tempPlayerScore;
                break;
            }
        }
        return nextPlayerScore;
    }

    private void ShowEndGameDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String winnerId = currentGame.getWinnerId();
        PlayerScore winner = null;
        for (PlayerScore playerScore : playerScores)
        {
            if (playerScore.getId().equals(winnerId))
            {
                winner = playerScore;
            }
        }
        String message;
        if (winner != null)
        {
            if (currentPlayerScore.getId().equals(winnerId))
            {
                message = "You have won. The word to guess was " + wordToGuess + ".";
            } else
            {
                message = winner.getName() + " has won. The word to guess was " + wordToGuess + ".";
            }
        } else
        {
            message = "Game has ended.";
        }
        message += " Press ok to go back to room";
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Intent intent = new Intent(GameActivity.this, RoomActivity.class);
                        Bundle b = new Bundle();
                        b.putString("id", gameId);
                        b.putBoolean("isHost", currentPlayer.isHost());
                        intent.putExtras(b);
                        startActivity(intent);
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        if (!isFinishing())
        {
            alert.show();
        }
    }
}
