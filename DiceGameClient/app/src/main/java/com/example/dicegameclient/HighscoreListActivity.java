package com.example.dicegameclient;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONObject;

/**
 * HighscoreListActivity. The activity that shows a list of highscores.
 */
public class HighscoreListActivity extends AppCompatActivity {

    // Boolean that determines if we show the list of personal highscores. false if we display location based highscores.
    private boolean showPersonal = false;
    // Values to display in the list.
    public String[] listValues = new String[9];
    // The listView.
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore_list);
        // Get the intent and the extra message it contains.
        Intent intent = getIntent();
        String message = intent.getStringExtra(HighscoreActivity.EXTRA_MESSAGE);
        // Set the title of the current screen to show the message.
        setTitle(message);

        // And store that we're either showing personal or not.
        showPersonal = false;
        if(message.equalsIgnoreCase("Highscores: Personal")){
            showPersonal = true;
        }

        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.list);

        // Get the values we need to display by calling the server.
        // If we have internet
        if(APIManager.getInstance().hasInternetConnection(this)){
            if(showPersonal){
                new GetUserScoresTask().execute(SessionManager.getInstance().user.getId());
            }else{
                new GetLocationScoresTask().execute(SessionManager.getInstance().user.lastScore.location);
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.custom_list_item, R.id.text_name, listValues);
        // Assign adapter to ListView
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_highscore_list, menu);
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
     * GetUserScoresTask. Used to communicate with the server and get a list of the users scores.
     * extends AsyncTask, to make sure the communication is done asynchronously from the UI thread.
     */
    private class GetUserScoresTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                return APIManager.getInstance().getUserScores(params[0]).toString();
            } catch (Exception e) {
                System.out.println("Exception occured: " + e);
                return "Unable to retrieve data. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            try {
                System.out.println("Result is: " + result);
                JSONObject obj = new JSONObject(result);
                System.out.println("JSON: " + obj.toString());
            }catch(Exception e){
                Log.d("JSONObject_PostExecute", e.toString());
            }
        }
    }

    /**
     * GetLocationScoresTask. Used to communicate with the server and get a list of the current location' scores.
     * extends AsyncTask, to make sure the communication is done asynchronously from the UI thread.
     */
    private class GetLocationScoresTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            // params comes from the execute() call: params[0] is the url.
            try {
                return APIManager.getInstance().getLocationScores(params[0]).toString();
            } catch (Exception e) {
                System.out.println("Exception occured: " + e);
                return "Unable to retrieve data. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            try {
                System.out.println("Result is: " + result);
            }catch(Exception e){
                Log.d("JSONObject_PostExecute", e.toString());
            }
        }
    }
}
