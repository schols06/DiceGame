package com.example.dicegameclient;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Make sure the keyboard disappears when the user clicks someplace else.
        EditText editText = (EditText)findViewById(R.id.input_ip);
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        // Make sure we have a user at this point, and reset the name to prevent caching.
        // This way we're sure that we make another call to the server to check if this user is registered.
        if(SessionManager.getInstance().user != null){
            // Reset the name to prevent caching
            SessionManager.getInstance().user.setName("username");
        }
    }

    /**
     * hideKeyboard. Used to hide the keyboard.
     * @param view The current view.
     */
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
            String userId = user.getId(getApplication());
            // If we have internet
            if(APIManager.getInstance().hasInternetConnection(this)){
                // try and retrieve the user from the server, if any.
                new GetUserTask().execute(userId);
            }else{
                showToast("Active internet connection required...");
            }
        }
    }

    /**
     * GetUserTask. Used to communicate with the server and get a user, if any.
     * extends AsyncTask, to make sure the communication is done asynchronously from the UI thread.
     */
    private class GetUserTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                if(APIManager.getInstance() == null ) {
                    System.out.println("Instance == null");
                }
                if(params == null ) {
                    System.out.println("Params == null");
                }
                if(params[0] == null ) {
                    System.out.println("Params[0] == null");
                }
                return APIManager.getInstance().getUser(params[0]).toString();
            } catch (Exception e) {
                System.out.println("Exception occured: " + e);
                return "Unable to retrieve data. URL may be invalid.";
            }
        }
        @Override
        protected void onPostExecute(String result) {
            try {
                // set the user object and return the statuscode
                int succes = setUser(new JSONObject(result));
                // Then retrieve the user we've just set, and cache it in a local variable.
                User user = SessionManager.getInstance().user;
                //If we were able to fetch user info, and the user is valid (& not null)
                if(succes == 200 && user != null && user.isValid()){
                    // Go to next screen and welcome the user back
                    showToast("Welcome back " + user.getName());
                    startIntentHome();
                }else if(succes == 204){
                    // Else if statuscode 204, make the user register
                    startIntentRegister();
                }else{
                    // Else inform the user
                    showToast("Server could be offline, please try again...");
                }
            }catch(Exception e){
                Log.d("JSONObject_PostExecute", e.toString());
            }
        }
    }

    /**
     * setUser. Used to set the values of the current user, retrieved from a JSONObject.
     * @param obj The JSONObject containing the info.
     * @return an int representing the response code we got from the server.
     */
    private int setUser(JSONObject obj){
        //Double check if the id is the same
        String id = "";
        String name = "";
        int response = SessionManager.getInstance().lastResponse;
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
        return response;
    }

    /**
     * getInputIP. Used to retrieve the IP from the InputField.
     * @return The IP as a String object.
     */
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

    /**
     * getUser. Used to return a new User, and initialize the minimal values, such as the id.
     * Checks if the user is registered, and welcomes the user.
     * @return
     */
    public User getUser(){
        Application application = getApplication();
        User user = new User(application);
        String id = user.getId(application);
        //We don't know the name yet. For now, set it to a default so validation fails
        user.setName("username");
        Boolean success = user.isValid();
        if(success) {
            // User does exist in db.

            // We inform the user, and continue
            showToast("Welcome " + user.getName() + "!");
        }
        return user;
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

    /**
     * startIntentHome. Takes us to the "Home" screen.
     */
    private void startIntentHome(){
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
    }

    /**
     * startIntentRegister. Takes us to the "Registration" screen.
     */
    private void startIntentRegister(){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }


}
