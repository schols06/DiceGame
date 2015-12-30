package com.example.dicegameclient;

/**
 * Created by Acer on 13/12/2015.
 */
public class SessionManager {

    private static SessionManager _instance;

    public User user = null;

    public final static String apiURL = "/DiceServer/webresources/";
    public final static String urlUsers = "com.dicedb.users";
    public final static String urlScores = "com.dicedb.scores";
    private String ip = "http://";

    private SessionManager(){}

    public String result = "";

    public static SessionManager getInstance()
    {
        if (_instance == null)
        {
            _instance = new SessionManager();
        }
        return _instance;
    }

    public void setIP(String serverIP){
        String port = ":8080";
        ip = "http://" + serverIP;
    }

    public String getUrlUsers(){
        String string = ip + apiURL + urlUsers;
        System.out.println("Users: " + string);
        return string;
    }
    public String getUrlScores(){
        String string = ip + apiURL + urlScores;
        System.out.println("Scores: " + string);
        return string;
    }
}
