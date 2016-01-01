package com.example.dicegameclient;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText editText = (EditText)findViewById(R.id.input_ip);
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * onSubmit. Called when the user presses the submit button.
     * Used to register the user in the database
     * @param view the view we're in. Used to extract ip from input-field.
     * @return void
     */
    public void onSubmit(View view) {
        // Get the ip the user has entered.
        String ip = getInputIP();
        // Set the server we are connecting to.
        APIManager.getInstance().setIP(ip);

        // Fetch the users info (android id).
        User user = getUser();
        // cache the user in the sessionManager
        SessionManager.getInstance().user = user;

        // If the user has entered an ip, and the user is not valid (Valid checks if the name is not the default name)
        if(ip != null && !user.isValid()) {
            //final String androidId = user.getId(getApplication());
            String params = user.getId(getApplication());
            // If we have internet
            if(APIManager.getInstance().hasInternetConnection(this)){
                new GetUserTask().execute(params);
            }else{
                //TODO: Inform the user that he/she needs to have an active internet connection.
            }
        }
    }

    private class GetUserTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return APIManager.getInstance().getUser(params[0].toString()).toString();
            } catch (IOException e) {
                return "Unable to retrieve data. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            try {
                setUser(new JSONObject(result));
                User user = SessionManager.getInstance().user;
                //If we were able to fetch user info
                if(user != null && user.isValid()){
                    Log.d("user: ", user.toString());
                    // Go to next screen
                    showToast("Welcome back " + user.getName());
                    startIntentHome();
                }else{
                    startIntentRegister();
                }
            }catch(Exception e){
                Log.d("JSONObject", e.toString());
            }
        }
    }


    private void setUser(JSONObject obj){
        //Double check if the id is the same
        String id = "";
        String name = "";
        try{
            id = obj.getString("androidId");
            name = obj.getString("name");
        }catch(Exception e){
            Log.d("JSONObject", e.toString());
        }
        if(!id.isEmpty() && !name.isEmpty()){
            if(id.equalsIgnoreCase(SessionManager.getInstance().user.getId())){
                // ID checks out, name is not empty, so we set the name of the user
                SessionManager.getInstance().user.setName(name);
            }else{
                System.out.println("id: " + id + ", does not match id: " + SessionManager.getInstance().user.getId());
            }
        }else{
            System.out.println("id or name empty");
        }
    }



    public String getInputIP(){
        // Get text
        EditText text = (EditText) findViewById(R.id.input_ip);

        // Optional checking for special characters and formatting
        String string = text.getText().toString();

        // if it is null or empty
        if(string == null || string.isEmpty()){
            string = "invalid ip";
        }

        // return it
        return string;
    }

    public User getUser(){
        Application application = getApplication();
        User user = new User(application);
        String id = user.getId(application);
        //We don't know the name yet. If the user's id is known in the db, we fetch the name, otherwise make the user register
        //Check if the user's id is known in the db
        Boolean success = userIsRegistered(user);
        if(success) {
            // User does exist in db.

            // We inform the user, and continue
            showToast("Welcome " + user.getName() + "!");
        }
        return user;
    }

    /**
     * userIsRegistered. Returns true if the user is registered in the db.
     * @param user the user we would like to check.
     * @return returns true if the user is known.
     */
    private Boolean userIsRegistered(User user){
        //TODO: Check if we have the user in the database by calling the api and comparing the id.


        //TODO: Set the users name if we succeed. (API Returns the name?)
        user.setName("username");

        // Return true, by calling the user.isValid method.
        // This validates the fields of the user, to make sure they're not empty.
        return user.isValid();
    }

    /**
     * showToast. Generates and displays a toast message to the user.
     * @param text The text to display to the user
     */
    private void showToast(String text){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void startIntentHome(){
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
    }

    private void startIntentRegister(){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }


}
