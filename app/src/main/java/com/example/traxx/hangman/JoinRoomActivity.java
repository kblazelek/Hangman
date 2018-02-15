package com.example.traxx.hangman;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class JoinRoomActivity extends AppCompatActivity
{
    ArrayList<Room> arrayOfRooms = new ArrayList<Room>();
    DatabaseReference database;
    RoomsAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_room);
        adapter = new RoomsAdapter(this, arrayOfRooms);
        ListView listView = findViewById(R.id.listViewRooms);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                long viewId = view.getId();
                if (viewId == R.id.buttonJoinRoom) {
                    String id = arrayOfRooms.get(i).getId();
                    Intent intent = new Intent(JoinRoomActivity.this, RoomActivity.class);
                    Bundle b = new Bundle();
                    b.putString("id", id);
                    b.putBoolean("isHost", false);
                    intent.putExtras(b);
                    startActivity(intent);
                }

            }
        });
        database = FirebaseDatabase.getInstance().getReference();
        registerRoomsListener();

    }

    private void registerRoomsListener()
    {
        Query roomsQuery = database.child("rooms");
        ValueEventListener roomsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arrayOfRooms.clear();
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Room room = singleSnapshot.getValue(Room.class);
                    registerRoomPlayersListener(room.getId());
                    arrayOfRooms.add(room);
                }
                if(adapter != null)
                {
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.toException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        roomsQuery.addValueEventListener(roomsListener);
    }

    private void registerRoomPlayersListener(final String roomId)
    {
        Query roomPlayersQuery = database.child("players").child(roomId);
        ValueEventListener roomsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = (int)dataSnapshot.getChildrenCount();
                if(adapter != null)
                {
                    adapter.addRoomPlayersCount(roomId, count);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.toException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        roomPlayersQuery.addValueEventListener(roomsListener);
    }


}
