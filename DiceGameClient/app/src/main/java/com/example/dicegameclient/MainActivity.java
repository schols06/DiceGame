package com.example.dicegameclient;

import android.content.Context;
import android.content.Intent;
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
     * @param view
     * @return void
     */
    public void onSubmit(View view) {
        //Fetch the info entered in input field
        User user = getUserInfo();
        //If we were able to fetch user info
        if(user != null && user.isValid()){
            Log.d("user: ", user.toString());

            // Go to next screen
            startIntentHome();
        }
        //otherwise return
        Log.d("invalid user", "User registration failed");
        return;
    }

    public User getUserInfo(){
        User user = new User();
        user.id = getAndroidID();
        user.name = getInputText();

        //push all to server, on succes callback,continue to next screen
        Boolean succes = isUserValid(user);
        if(succes) {
            // We inform the user, and continue
            showToast("Registered!");
            return user;
        }
        // If we failed to register, inform the user and take appropriate action.
        showToast("Failed...");

        //If we did not return at this point, return a new user object and inform the user
        return new User();
    }

    private Boolean isUserValid(User user){
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

    /**
     * getInputText. returns the text the user entered in the inputfield.
     * @return String input text. The name the user entered in the inputfield.
     */
    public String getInputText(){
        // Get text
        EditText text = (EditText) findViewById(R.id.input_name);

        // Optional checking for special characters and formatting
        String string = text.getText().toString();

        // return it
        return string;
    }

    private String getAndroidID(){
        String id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Log.d("android_id", id);
        return id;
    }

    private void startIntentHome(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}
