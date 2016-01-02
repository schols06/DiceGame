package com.example.dicegameclient;

import java.util.Calendar;

/**
 * Created by Acer on 02/01/2016.
 */
public class Score {
    public String location = "";
    public int score = -1;
    public long timestamp = Calendar.getInstance().getTime().getTime();
    public int lastResponse = -1;

    public Score(){

    }

    public void setScore(int value, String location){
        this.score = value;
        this.location = location;
        updateTimestamp();
    }

    public void updateTimestamp(){
        timestamp = Calendar.getInstance().getTime().getTime();
    }
}
