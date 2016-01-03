package com.example.dicegameclient;

import java.util.Calendar;

/**
 * Score. Object that represents a score.
 * Has a location, score value, timestamp of when the score was generated
 * and the last response received from the server, based on this score.
 */
public class Score {
    public String location = "";
    public int score = -1;
    public long timestamp = Calendar.getInstance().getTime().getTime();
    public int lastResponse = -1;

    // Empty constructor
    public Score(){

    }

    /**
     * setScore. used to set a score and update its timestamp.
     * @param value The score value
     * @param location The location the score was generated at
     */
    public void setScore(int value, String location){
        this.score = value;
        this.location = location;
        updateTimestamp();
    }

    /**
     * updateTimestamp. Updates the timestamp to the current time. (UNIX Timestamp).
     */
    public void updateTimestamp(){
        timestamp = Calendar.getInstance().getTime().getTime();
    }
}
