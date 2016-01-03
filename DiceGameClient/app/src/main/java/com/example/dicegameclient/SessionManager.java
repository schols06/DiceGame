package com.example.dicegameclient;

/**
 * SessionManager. Singleton used to hold information about the current session.
 */
public class SessionManager {

    // Singleton
    private SessionManager(){}
    private static SessionManager _instance;
    public static SessionManager getInstance()
    {
        if (_instance == null)
        {
            _instance = new SessionManager();
        }
        return _instance;
    }

    // The last response we got from the server.
    public int lastResponse = -1;

    // The current user.
    public User user = null;
}
