package com.example.traxx.hangman;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

public class MainMenuActivity extends AppCompatActivity implements View.OnClickListener {
    Button buttonSignOut;
    Button buttonCreateRoom;
    Button buttonJoinRoom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        buttonSignOut = findViewById(R.id.buttonSignOut);
        buttonSignOut.setOnClickListener(this);
        buttonCreateRoom = findViewById(R.id.buttonCreateRoom);
        buttonCreateRoom.setOnClickListener(this);
        buttonJoinRoom = findViewById(R.id.buttonJoinRoom);
        buttonJoinRoom.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.buttonSignOut:
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                startActivity(new Intent(this, SignInActivity.class));
                break;
            case R.id.buttonCreateRoom:
                startActivity(new Intent(this, CreateRoomActivity.class));
                break;
            case R.id.buttonJoinRoom:
                startActivity(new Intent(this, JoinRoomActivity.class));
                break;
        }
    }
}
