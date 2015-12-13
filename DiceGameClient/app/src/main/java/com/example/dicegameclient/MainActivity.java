package com.example.dicegameclient;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
     * onSubmit. Called when the user presses the submit button.
     * Used to register the user in the database
     * @param view the view we're in. Used to extract ip from input-field.
     * @return void
     */
    public void onSubmit(View view) {
        // TODO: Check the ip that was entered and try to make a call
        String ip = getInputIP();

        //Fetch the info (android id)
        User user = getUser();
        user.serverIP = ip;
        // cache the user in the sessionManager
        SessionManager.getInstance().user = user;
        //If we were able to fetch user info
        if(user != null && user.isValid()){
            Log.d("user: ", user.toString());


            // Go to next screen
            startIntentHome();
        }else{
            Log.d("User", "User registration required");
            startIntentRegister();
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
        User user = new User();
        user.id = getAndroidID();
        //We don't know the name yet. If the user's id is known in the db, we fetch the name, otherwise make the user register
        user.name = "";

        //Check if the user's id is known in the db
        Boolean success = userIsRegistered(user);
        if(success) {
            // We inform the user, and continue
            showToast("Welcome " + user.name + "!");
            return user;
        }
        return null;
    }

    /**
     * userIsRegistered. Returns true if the user is registered in the db.
     * @param user the user we would like to check.
     * @return returns true if the user is known.
     */
    private Boolean userIsRegistered(User user){
        //TODO: Check if we have the user in the database by calling the api and comparing the id.


        //TODO: Set the users name if we succeed.
        user.name = "username";

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

    private String getAndroidID() {
        String id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Log.d("android_id", id);
        return id;
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
