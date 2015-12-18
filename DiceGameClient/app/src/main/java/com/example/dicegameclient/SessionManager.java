package com.example.dicegameclient;

/**
 * Created by Acer on 13/12/2015.
 */
public class SessionManager {

    private static SessionManager _instance;

    public User user;

    public final static String strikeIronUserName = "stikeironusername@yourdomain.com";
    public final static String strikeIronPassword = "strikeironpassword";
    public final static String apiURL = "http://ws.strikeiron.com/StrikeIron/EMV6Hygiene/VerifyEmail?";

    private SessionManager(){}

    public static SessionManager getInstance()
    {
        if (_instance == null)
        {
            _instance = new SessionManager();
        }
        return _instance;
    }
}
