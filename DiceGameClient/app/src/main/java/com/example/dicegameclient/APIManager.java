package com.example.dicegameclient;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInstaller;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
/**
 * Created by Luc on 18-12-2015.
 */
public class APIManager {

    private static final String DICE_GAME_GET_USER_API = ":8080/DiceTestServer/webresources/entities.users/";
    private static final String DICE_GAME_SET_USER_API = ":8080/DiceTestServer/webresources/entities.users";

    private APIManager(){}
    private static APIManager _instance;
    public static APIManager getInstance()
    {
        if (_instance == null)
        {
            _instance = new APIManager();
        }
        return _instance;
    }

    private String ip = "http://";

    public void setIP(String serverIP){
        ip = "http://" + serverIP;
    }

    public boolean hasInternetConnection(Activity activity){
        ConnectivityManager connMgr = (ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        System.out.println("No network connection available.");
        return false;
    }



    // Given a URL, establishes an HttpUrlConnection and retrieves
    // the JSON content as a InputStream, which it returns as
    // a JSONObject.
    public JSONObject getUser(String userId) throws IOException {
        InputStream is = null;
        try {
            URL url = new URL(ip + DICE_GAME_GET_USER_API + userId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("Debug", "The response is: " + response);
            is = conn.getInputStream();

            Reader reader = null;
            reader = new InputStreamReader(is, "UTF-8");
            // Create a char array, that can hold a max of 512 characters
            char[] buffer = new char[512];
            reader.read(buffer);
            String temp = new String(buffer);
            // Convert the InputStream into a string
            JSONObject data = new JSONObject(temp);
            System.out.println("Get User: " + data.toString());
            return data;
        }catch(Exception e){
            return null;
        } finally {
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
            if (is != null) {
                is.close();
            }
        }
    }

    public void setUser(String name){
        URL url;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(ip + DICE_GAME_SET_USER_API.toString() + "/");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(0);

            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");

            JSONObject json = new JSONObject();
            SessionManager.getInstance().user.setName(name);
            json.put("androidId", SessionManager.getInstance().user.getId().toString());
            json.put("name", SessionManager.getInstance().user.getName().toString());

            String input = json.toString();

            OutputStream os = urlConnection.getOutputStream();
            os.write(input.getBytes());
            os.flush();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String temp = readStream(in);
            System.out.println("Temp: " + temp);

        }
        catch(Exception e){
            Log.d("SetUser", e.toString());
        }
        finally {
            urlConnection.disconnect();
        }
    }


    private String readStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(is),1000);
        for (String line = r.readLine(); line != null; line =r.readLine()){
            sb.append(line);
        }
        is.close();
        return sb.toString();
    }

}