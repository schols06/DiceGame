package com.example.dicegameclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        setUsernameText();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
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

    private void setUsernameText(){
        // Get text
        TextView text = (TextView) findViewById(R.id.text_username);
        text.setText(SessionManager.getInstance().user.getName());
    }

    private void startIntentPlay(){
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    private void startIntentHighscores(){
        Intent intent = new Intent(this, HighscoreActivity.class);
        startActivity(intent);
    }

    public void onPlay(View view){
        startIntentPlay();
    }
    public void onHighscore(View view){
        startIntentHighscores();
    }
}
