package com.example.dicegameclient;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
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

    private static final String DICE_GAME_GET_USER_API = ":8080/DiceServer/webresources/com.dicedb.users/";
    private static final String DICE_GAME_SET_SCORE_API = ":8080/DiceServer/webresources/com.dicedb.scores/";

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
            if(ip == null){ System.out.println("IP == NULL"); }else{System.out.println("IP: " + ip);}
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
            SessionManager.getInstance().lastResponse = response;
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

            conn.disconnect();

            return data;
        }catch(Exception e){
            return new JSONObject();
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
            url = new URL(ip + DICE_GAME_GET_USER_API);
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

    public void setScore(){
        URL url;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(ip + DICE_GAME_SET_SCORE_API);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(0);

            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");

            User tempUser = SessionManager.getInstance().user;



            JSONObject json = new JSONObject();


            int score = tempUser.lastScore.score;
            System.out.println("Score is (before post): " + score);
            json.put("timestamp", tempUser.lastScore.timestamp);
            json.put("value", score);
            json.put("location", tempUser.lastScore.location.toString());


            JSONObject jsonUser = new JSONObject();
            jsonUser.put("androidId", tempUser.getId().toString());
            jsonUser.put("name", tempUser.getName().toString());

            json.accumulate("androidId", jsonUser);




            System.out.println("JSON Score: \n" + json.toString());

            String input = json.toString();
            OutputStream os = urlConnection.getOutputStream();
            os.write(input.getBytes());
            os.flush();


            System.out.println("Response was: " + urlConnection.getResponseCode() + "\nMessage: " + urlConnection.getResponseMessage());
            SessionManager.getInstance().user.lastScore.lastResponse = urlConnection.getResponseCode();
            urlConnection.disconnect();

        }
        catch(Exception e){
            Log.d("SetUserScore", e.toString());
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