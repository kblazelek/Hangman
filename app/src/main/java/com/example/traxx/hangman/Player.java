package com.example.traxx.hangman;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Traxx on 04.02.2018.
 */

public class Player implements Parcelable
{
    private String id;
    private String name;
    private boolean isHost = false;

    public boolean isHost()
    {
        return isHost;
    }

    public void setHost(boolean host)
    {
        isHost = host;
    }

    public Player()
    {
    }

    public Player(String id, String name, Boolean isHost)
    {
        this.id = id;
        this.name = name;
        this.isHost = isHost;
    }

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    protected Player(Parcel in) {
        id = in.readString();
        name = in.readString();
        isHost = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeByte((byte) (isHost ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Player> CREATOR = new Parcelable.Creator<Player>() {
        @Override
        public Player createFromParcel(Parcel in) {
            return new Player(in);
        }

        @Override
        public Player[] newArray(int size) {
            return new Player[size];
        }
    };
}