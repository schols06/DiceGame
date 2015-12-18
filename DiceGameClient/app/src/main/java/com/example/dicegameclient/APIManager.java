package com.example.dicegameclient;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Luc on 18-12-2015.
 */
public class APIManager extends AsyncTask<String, String, String> {

    @Override
    protected String doInBackground(String... params) {

        String urlString=params[0]; // URL to call

        String resultToDisplay = "";

        InputStream input = null;

        // HTTP Get
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            input = new BufferedInputStream(urlConnection.getInputStream());

        } catch (Exception e ) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }

        return resultToDisplay;

    }

    protected void onPostExecute(String result) {

    }

} // end CallAPI