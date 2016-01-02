package com.example.dicegameclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class HighscoreActivity extends AppCompatActivity {

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

    public void onStartIntentHighscoresPersonal(View view){
        Intent intent = new Intent(this, HighscoreListActivity.class);
        intent.putExtra(EXTRA_MESSAGE, "Highscores: Personal");
        startActivity(intent);
    }

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
