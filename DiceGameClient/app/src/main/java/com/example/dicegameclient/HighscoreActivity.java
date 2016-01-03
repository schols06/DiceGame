package com.example.dicegameclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

/**
 * HighscoreActivity. The activity of the highscore screen, which links to 2 possible screens:
 * - The Highscore list of the current user.
 * - The highscore list for the current location.
 */
public class HighscoreActivity extends AppCompatActivity {

    // String used to retrieve extra messages when we change screens.
    public final static String EXTRA_MESSAGE = "com.example.dicegameclient.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);
        Button button = (Button) findViewById(R.id.button_location);
        if(SessionManager.getInstance().user != null){
            if(SessionManager.getInstance().user.lastScore != null){
                System.out.println("Hi " + SessionManager.getInstance().user.lastScore.location);
                button.setText("Location: " + SessionManager.getInstance().user.lastScore.location);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_highscore, menu);
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
     * onStartIntentHighscoresPersonal. Called when the user presses the button related to starting this activity.
     * Starts the new activity that shows personal highscores.
     * @param view The current view.
     */
    public void onStartIntentHighscoresPersonal(View view){
        Intent intent = new Intent(this, HighscoreListActivity.class);
        intent.putExtra(EXTRA_MESSAGE, "Highscores: Personal");
        startActivity(intent);
    }

    /**
     * onStartIntentHighscoresLocation. Called when the user presses the button related to starting this activity.
     * Starts the new activity that shows location highscores.
     * @param view
     */
    public void onStartIntentHighscoresLocation(View view){
        Intent intent = new Intent(this, HighscoreListActivity.class);
        String message = "";
        if(SessionManager.getInstance().user != null){
            if(SessionManager.getInstance().user.lastScore != null){
                message = SessionManager.getInstance().user.lastScore.location;
            }
        }
        intent.putExtra(EXTRA_MESSAGE, "Highscores: " + message);
        startActivity(intent);
    }
}
