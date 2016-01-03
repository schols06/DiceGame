package com.example.dicegameclient;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;

/**
 * RegisterActivity. The activity used to register the user.
 */
public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * onRegister. Called when the user hits the register button.
     * @param view The current view.
     */
    public void onRegister(View view){
        // Get the username the user entered form the input field.
        String username = getInputString();
        // Let the user class check if this is a valid username
        if(SessionManager.getInstance().user.isValid(username)){
            if(APIManager.getInstance().hasInternetConnection(this)){
                new SetUserTask().execute(username);
            }else{
                //TODO: Inform the user that registering somehow failed?.
            }
        }else{
            // Inform the user and try again.
            showToast("Invalid username");
        }
    }

    /**
     * SetUserTask. Used to communicate with the server and register a user.
     * extends AsyncTask, to make sure the communication is done asynchronously from the UI thread.
     */
    private class SetUserTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                // Get the username from the parameters.
                String userName = params[0].toString();
                // Call the APIManager to register this user.
                APIManager.getInstance().setUser(userName);
                // Pass along the username parameters.
                return userName;
            } catch (Exception e) {
                return "Unable to retrieve data. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            try {
                System.out.println("Result:" + result);
                // Get the name, should be stored by the apimanager.
                String tempName = SessionManager.getInstance().user.getName();
                // Double check the name.
                if(result.equalsIgnoreCase(tempName)){
                    // Start the game, and welcome the user.
                    startIntentPlay();
                    showToast("Welcome " + tempName);
                }
            }catch(Exception e){
                Log.d("JSONObject", e.toString());
            }
        }
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
     * getInputString. Returns the contents of the InputField as a string.
     * @return The contents of the inputfield as a String.
     */
    private String getInputString(){
        // Get text
        EditText text = (EditText) findViewById(R.id.input_name);
        // Optional checking for special characters and formatting
        String string = text.getText().toString();
        // if it is null or empty
        if(string == null || string.isEmpty()){
            string = "Enter a name";
        }
        // return it
        return string;
    }

    /**
     * startIntentPlay. Takes us to the game screen.
     */
    private void startIntentPlay(){
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }
}
