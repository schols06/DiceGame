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
 * APIManager.
 * Singleton class that takes care of all communication with the API.
 * */
public class APIManager {

    // API URL's
    private static final String DICE_GAME_USER_API =                ":8080/DiceServer/webresources/com.dicedb.users/";
    private static final String DICE_GAME_SET_SCORE_API =           ":8080/DiceServer/webresources/com.dicedb.scores/";
    private static final String DICE_GAME_GET_USER_SCORES_API =     ":8080/DiceServer/webresources/com.dicedb.scores/androidId/";
    private static final String DICE_GAME_GET_LOCATION_SCORES_API = ":8080/DiceServer/webresources/com.dicedb.scores/location/";

    // Singleton part
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

    /**
     * setIP. Used to set the IP of the server we're communicating with.
     * @param serverIP. The server IP as a string. Example: "195.178.0.12"
     */
    public void setIP(String serverIP){
        ip = "http://" + serverIP;
    }

    /**
     * hasInternetConnection. Method that checks if the user has an active internet connection.
     * @param activity. The current activity.
     * @return true if the user has an active internet connection. False otherwise.
     */
    public boolean hasInternetConnection(Activity activity){
        ConnectivityManager connMgr = (ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        System.out.println("No network connection available.");
        return false;
    }

    /**
     * getUser. Tries to get the user from the backend and returns a JSONObject of the user' info if found.
     * @param userId. The user' id we're looking for
     * @return JSONObject. The JSONObject containing the user' info
     * @throws IOException Incase the JSONObject could not be created, exception might be thrown.
     */
    public JSONObject getUser(String userId) throws IOException {
        InputStream is = null;
        try {
            URL url = new URL(ip + DICE_GAME_USER_API + userId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            // Connect
            conn.connect();

            // Get the response code and store it in our sessionManager for later reference.
            int response = conn.getResponseCode();
            Log.d("Debug", "The response is: " + response);
            SessionManager.getInstance().lastResponse = response;

            // Get the inputStream
            is = conn.getInputStream();

            // And read the inputStream
            Reader reader = null;
            reader = new InputStreamReader(is, "UTF-8");
            // Create a char array, that can hold a max of 512 characters
            char[] buffer = new char[512];
            reader.read(buffer);

            // Cast/cache it in a string.
            String temp = new String(buffer);

            // Convert the String into a JSONObject
            JSONObject data = new JSONObject(temp);
            System.out.println("Get User: " + data.toString());

            // And close the connection when we're done.
            conn.disconnect();

            // And return the JSONObject.
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

    /**
     * setUser. Used to create a new user in the database.
     * @param name the name of the user we're registering
     */
    public void setUser(String name){
        URL url;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(ip + DICE_GAME_USER_API);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(0);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");

            JSONObject json = new JSONObject();
            SessionManager.getInstance().user.setName(name);
            json.put("androidId", SessionManager.getInstance().user.getId().toString());
            json.put("name", SessionManager.getInstance().user.getName().toString());

            // Incase there is an outputstream, write it to the input string.
            String input = json.toString();
            OutputStream os = urlConnection.getOutputStream();
            os.write(input.getBytes());
            os.flush();

            // Read the input stream and cache it in our temp String variable.
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String temp = readStream(in);
            // And print the results for debugging purposes
            System.out.println("inputStream: " + temp);
        }
        catch(Exception e){
            Log.d("SetUser", e.toString());
        }
        finally {
            urlConnection.disconnect();
        }
    }

    /**
     * setScore. Used to register a score in the database of the server.
     */
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

    /**
     * getUserScores. Used to retrieve a list of scores of the current user.
     * @param userId. The user' id of the user we're requesting scores for
     * @return JSONObject. The list of scores as a JSONObject.
     * @throws Exception Throws an exception incase the retrieving of scores fails.
     */
    public JSONObject getUserScores(String userId) throws IOException {
        InputStream is = null;
        try {
            URL url = new URL(ip + DICE_GAME_GET_USER_SCORES_API + userId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("Debug3", "The response is: " + response);
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

    /**
     * getLocationScores. Used to retrieve a list of scores of the current location.
     * @param location The location we're requesting scores for, as a String.
     * @return JSONObject. The list of retrieved scores, as a JSONObject
     * @throws IOException
     */
    public JSONObject getLocationScores(String location) throws IOException {
        InputStream is = null;
        try {
            URL url = new URL(ip + DICE_GAME_GET_LOCATION_SCORES_API + location);
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

            System.out.println("Temp3: " + temp);
            // Convert the InputStream into a string
            JSONObject data = new JSONObject();
            try {
                data = new JSONObject(temp);
            }catch(Exception ex){
                System.out.println("Exception: " + ex.toString());
            }

            System.out.println("Get Scores Location: " + data.toString());

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

    /**
     * readStream. Used to read an input stream and return it as a String.
     * @param is. The InputStream.
     * @return String, the result of reading the InputStream as a String.
     * @throws IOException
     */
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