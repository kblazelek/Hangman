package com.example.traxx.hangman;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

public class RoomActivity extends AppCompatActivity implements View.OnClickListener
{
    String roomId;
    ArrayList<Player> arrayOfPlayers = new ArrayList<Player>();
    DatabaseReference database;
    PlayersInRoomAdapter adapter;
    Button buttonStartGame;
    TextView textViewRoomName;
    TextView textViewHostName;
    TextView textViewPlayers;
    Player host;
    Player currentPlayer;
    FirebaseUser currentUser;
    int maxNumberOfPlayers = 0;
    boolean wasPlayerAdded = false;
    boolean isHost = false;
    ArrayList<Question> questions;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        Bundle b = getIntent().getExtras();
        if (b != null)
        {
            roomId = b.getString("id");
            isHost = b.getBoolean("isHost");
        }
        buttonStartGame = findViewById(R.id.buttonStartGame);
        buttonStartGame.setOnClickListener(this);
        textViewRoomName = findViewById(R.id.textViewRoomName);
        textViewHostName = findViewById(R.id.textViewHostName);
        textViewPlayers = findViewById(R.id.textViewPlayers);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        questions = new ArrayList<>();
        database = FirebaseDatabase.getInstance().getReference();
        if(isHost)
        {
            registerQuestionsListener();
        }
        adapter = new PlayersInRoomAdapter(this, arrayOfPlayers);
        ListView listView = findViewById(R.id.listViewPlayers);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                long viewId = view.getId();
                if (viewId == R.id.imageButtonKickUser)
                {
                    database.child("players").child(roomId).child(arrayOfPlayers.get(i).getId()).removeValue();
                }
            }
        });
        registerRoomListener();
        registerPlayersListener();
    }

    private void registerQuestionsListener()
    {
        final Query questionsQuery = database.child("questions");
        ValueEventListener questionsListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                questions.clear();
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren())
                {
                    Question question = singleSnapshot.getValue(Question.class);
                    questions.add(question);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Toast.makeText(getApplicationContext(), databaseError.toException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        questionsQuery.addListenerForSingleValueEvent(questionsListener);
    }

    private void registerPlayersListener()
    {
        Query playersQuery = database.child("players").child(roomId);
        ValueEventListener playersListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                arrayOfPlayers.clear();
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren())
                {
                    Player player = singleSnapshot.getValue(Player.class);
                    arrayOfPlayers.add(player);
                }
                String playersCount;
                currentPlayer = null;
                for (Player player : arrayOfPlayers)
                {
                    if(player.isHost())
                    {
                        textViewHostName.setText(player.getName());
                    }
                    if (player.getId().equals(currentUser.getUid()))
                    {
                        currentPlayer = player;
                    }
                }
                if (currentPlayer == null) // user has been removed
                {
                    if(wasPlayerAdded)
                    {
                        finish();
                    }
                    else {
                        wasPlayerAdded = true;
                        database.child("players").child(roomId).child(currentUser.getUid()).setValue(new Player(currentUser.getUid(), currentUser.getDisplayName(), isHost));
                    }
                } else
                {
                    adapter.setCurrentPlayer(currentPlayer);
                    if (currentPlayer.isHost())
                    {
                        buttonStartGame.setVisibility(View.VISIBLE);
                    } else
                    {
                        buttonStartGame.setVisibility(View.INVISIBLE);
                    }
                }
                if (arrayOfPlayers == null)
                {
                    playersCount = "0/" + maxNumberOfPlayers;
                } else
                {
                    playersCount = arrayOfPlayers.size() + "/" + maxNumberOfPlayers;
                }
                textViewPlayers.setText(playersCount);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Toast.makeText(getApplicationContext(), databaseError.toException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        playersQuery.addValueEventListener(playersListener);
    }

    private void registerRoomListener()
    {
        Query roomQuery = database.child("rooms").child(roomId);
        ValueEventListener roomListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Room room = dataSnapshot.getValue(Room.class);
                if(room == null)
                {
                    if(!isHost)
                    {
                        Toast.makeText(getApplicationContext(), "Host has closed the room", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                    return;
                }
                textViewRoomName.setText(room.getName());
                maxNumberOfPlayers = room.getMaxNumberOfPlayers();
                if(room.isGameStarted())
                {
                    Intent intent = new Intent(RoomActivity.this, GameActivity.class);
                    Bundle b = new Bundle();
                    b.putParcelable("player", currentPlayer);
                    b.putString("gameId", roomId);
                    intent.putExtras(b);
                    database.child("players").child(roomId).child(currentUser.getUid()).removeValue();
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Toast.makeText(getApplicationContext(), databaseError.toException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        roomQuery.addValueEventListener(roomListener);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.buttonStartGame:
                int playersToFill = maxNumberOfPlayers - arrayOfPlayers.size();
                if(playersToFill <= 0)
                {
                    Question question;
                    if(questions.size() == 0)
                    {
                        Toast.makeText(getApplicationContext(), "Did not receive questions from server. Try again in a while", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else
                    {
                        int count = questions.size();
                        Random generator = new Random();
                        int i = generator.nextInt(count);
                        question = questions.get(i);
                    }
                    Game game = new Game(textViewRoomName.getText().toString(), question.getWordToGuess(), question.getHint());
                    database.child("games").child(roomId).setValue(game);
                    database.child("rooms").child(roomId).child("gameStarted").setValue(true);
                }
                else
                {
                    String message = "Cannot start the game, because there is still " + playersToFill + " player/s missing";
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onBackPressed()
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        if(isHost)
                        {
                            database.child("rooms").child(roomId).removeValue();
                            database.child("players").child(roomId).removeValue();
                        }
                        else
                        {
                            database.child("players").child(roomId).child(currentUser.getUid()).removeValue();
                        }
                        finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(RoomActivity.this);
        builder.setMessage("Do you want to leave current room?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
}
