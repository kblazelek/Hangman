package com.example.traxx.hangman;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.UUID;

public class CreateRoomActivity extends AppCompatActivity implements View.OnClickListener
{
    EditText editTextRoomName;
    EditText editTextMaxNumberOfPlayers;
    Button buttonCreateRoom;
    FirebaseAuth firebaseAuth;
    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);
        editTextRoomName = findViewById(R.id.editTextRoomName);
        editTextMaxNumberOfPlayers = findViewById(R.id.editTextMaxNumberOfPlayers);
        buttonCreateRoom = findViewById(R.id.buttonCreateRoom);
        buttonCreateRoom.setOnClickListener(this);
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onClick(View view)
    {
        try
        {
            switch (view.getId())
            {
                case R.id.buttonCreateRoom:
                    Room room = CreateRoom();
                    Intent intent = new Intent(this, RoomActivity.class);
                    Bundle b = new Bundle();
                    b.putString("id", room.getId());
                    b.putBoolean("isHost", true);
                    intent.putExtras(b);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
        catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public Room CreateRoom() throws Exception
    {
        String roomName = editTextRoomName.getText().toString();
        if (roomName.length() < 4)
        {
            throw new Exception("Room name must me longer than 4 characters");
        }
        int maxNumberOfPlayers = Integer.parseInt(editTextMaxNumberOfPlayers.getText().toString());
        if (maxNumberOfPlayers < 2 || maxNumberOfPlayers > 4)
        {
            throw new Exception("Max number of players must be between 2 and 4");
        }
        DatabaseReference ref = database.child("rooms").push();
        Room room = new Room(ref.getKey(), roomName, maxNumberOfPlayers);
        database.child("rooms").child(room.getId()).setValue(room);
        return room;
    }
}
