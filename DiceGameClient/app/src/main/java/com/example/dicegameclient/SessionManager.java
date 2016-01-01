package com.example.dicegameclient;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Acer on 13/12/2015.
 */
public class SessionManager {

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

    public User user = null;


}
