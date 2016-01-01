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

    private class SetUserTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            // params comes from the execute() call: params[0] is the url.
            try {
                String userName = params[0].toString();
                APIManager.getInstance().setUser(userName);
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
