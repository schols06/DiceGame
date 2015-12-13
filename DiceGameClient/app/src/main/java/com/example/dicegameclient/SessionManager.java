package com.example.dicegameclient;

/**
 * Created by Acer on 13/12/2015.
 */
public class SessionManager {

    private static SessionManager _instance;

    public User user;

    private SessionManager()
    {

    }

    public static SessionManager getInstance()
    {
        if (_instance == null)
        {
            _instance = new SessionManager();
        }
        return _instance;
    }
}
