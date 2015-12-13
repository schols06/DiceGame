package com.example.dicegameclient;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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

    public void onRegister(View view){
        String username = getInputString();
        if(username != "Enter a name" && username!= "username"){
            // cache the user in the sessionManager
            SessionManager.getInstance().user.name = username;
            startIntentPlay();
        }else{
            showToast("Invalid username");
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

    private void startIntentPlay(){
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }
}
