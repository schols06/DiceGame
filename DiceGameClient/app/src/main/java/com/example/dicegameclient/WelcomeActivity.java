package com.example.dicegameclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * WelcomeActivity. The homepage for the user.
 */
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

    /**
     * setUsernameText. Used to set the user' name to the TextView on the UI.
     */
    private void setUsernameText(){
        // Get text
        TextView text = (TextView) findViewById(R.id.text_username);
        text.setText(SessionManager.getInstance().user.getName());
    }

    /**
     * startIntentPlay. Start the intent that takes us to the game.
     */
    private void startIntentPlay(){
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }
    /**
     * startIntentHighscores. Start the intent that takes us to the highscores page.
     */
    private void startIntentHighscores(){
        Intent intent = new Intent(this, HighscoreActivity.class);
        startActivity(intent);
    }

    /**
     * onPlay. Called when the user presses the play button.
     * @param view The current view.
     */
    public void onPlay(View view){
        startIntentPlay();
    }

    /**
     * onHighscore. Called when the user presses the highscore button.
     * @param view
     */
    public void onHighscore(View view){
        startIntentHighscores();
    }
}
