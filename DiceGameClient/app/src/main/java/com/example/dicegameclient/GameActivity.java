package com.example.dicegameclient;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements SensorEventListener, LocationListener {

    // The sensorManager to interact with sensors
    private SensorManager mSensorManager;
    // The Accelerometer sensor
    private Sensor mAccelerometer;

    // LocationManager. Used to communicate with the GPS Sensor.
    private LocationManager locationManager;
    // The GPS provider. Used to translate the users location into a readable location such as "Amsterdam".
    private String provider;

    // Variables related to shaking the device
    private static final int FORCE_THRESHOLD = 350;
    private static final int TIME_THRESHOLD = 100;
    private static final int SHAKE_TIMEOUT = 500;
    private static final int SHAKE_DURATION = 1000;
    private static final int SHAKE_COUNT = 3;

    // The max score a user can get. 6 (max score with regular dice, x100 to allow more possible scores)
    private static final int MAX_SCORE = 600;

    // Values used to measure changes in time, accelerometer changes and number of shakes
    private float mLastX=-1.0f, mLastY=-1.0f, mLastZ=-1.0f;
    private long mLastTime;
    private int mShakeCount = 0;
    private long mLastShake;
    private long mLastForce;

    /**
     * onAccuracyChanged. Called when the accuracy of the sensor changes
     * @param sensor The sensor.
     * @param accuracy The accuracy level.
     */
    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    /**
     * onSensorChanged. Called when the sensor registers a change. (Movement).
     * @param event The event fired by the sensor.
     */
    @Override
    public final void onSensorChanged(SensorEvent event) {
        // No point in running if we don't have an accelerometer.
        if(!hasAccelerometer()) return;

        // Get the current time.
        long now = System.currentTimeMillis();

        // Reset the nr of shakes if we go over the timeout value since the time of our last registered force.
        if ((now - mLastForce) > SHAKE_TIMEOUT) {
            mShakeCount = 0;
        }

        // If we pass the time that should pass since our last shake, register that we shake the device.
        if ((now - mLastTime) > TIME_THRESHOLD) {
            // Time difference since the last time we checked.
            long diff = now - mLastTime;
            // The speed the devices was moved with.
            float speed = Math.abs(event.values[SensorManager.DATA_X] + event.values[SensorManager.DATA_Y] + event.values[SensorManager.DATA_Z] - mLastX - mLastY - mLastZ) / diff * 10000;
            // If our force/speed was greater than our threshold, register a shake.
            if (speed > FORCE_THRESHOLD) {
                // If we pass the nr of shakes the user should be doing, and there is enough time between them, call the onShake function.
                if ((++mShakeCount >= SHAKE_COUNT) && (now - mLastShake > SHAKE_DURATION)) {
                    mLastShake = now;
                    mShakeCount = 0;
                    onShake();
                }
                mLastForce = now;
            }
            mLastTime = now;
            mLastX = event.values[SensorManager.DATA_X];
            mLastY = event.values[SensorManager.DATA_Y];
            mLastZ = event.values[SensorManager.DATA_Z];
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
     * onShake. Called when the user succesfully shakes the device the set nr of times.
     */
    public void onShake(){
        // "Generate" a random score, as if the user actually rolled a bunch of dice.
        int score = getRandomNumber(MAX_SCORE);

        // Get the location, which we cached in the user' score variable
        String location = SessionManager.getInstance().user.lastScore.location;

        // Store the score as our last score.
        SessionManager.getInstance().user.lastScore.setScore(score, location);

        // Display the score to the user
        setScoreString("" + score);
        showToast("You rolled " + score + "!");

        // Send the score to the server
        submitScore(SessionManager.getInstance().user.lastScore);
    }

    /**
     * submitScore. Used to submit the score to the server.
     * @param score
     */
    private void submitScore(Score score){
        if(APIManager.getInstance().hasInternetConnection(this)){
            new SetUserScoreTask().execute("");
        }
    }

    /**
     * SetUserScoreTask. Used to communicate with the server.
     * extends AsyncTask, to make sure the communication is done asynchronously from the UI thread.
     */
    private class SetUserScoreTask extends AsyncTask<String, Void, String> {
        /**
         * The task that gets run in the background
         * @param params the parameters we wish to pass in to the function.
         *               Does not actually take any parameters. Here to conform to base class syntax.
         * @return the response as a String.
         */
        @Override
        protected String doInBackground(String... params) {
            try {
                // Tell the api manager to post the last set score to the server.
                APIManager.getInstance().setScore();
                // And return the response (as a String)
                return Integer.toString(SessionManager.getInstance().user.lastScore.lastResponse);
            } catch (Exception e) {
                return "Unable to retrieve data. URL may be invalid.";
            }
        }

        /**
         * onPostExecute. displays the results of the AsyncTask.
         * @param result The result we got from the doInBackground function
         */
        @Override
        protected void onPostExecute(String result) {
            try {
                int response = -1;
                try {
                    // Try retrieving the response
                    response = Integer.parseInt(result);
                } catch(NumberFormatException nfe) {
                    // If there was an exception during pasing the number, print it.
                    System.out.println("Could not parse " + nfe);
                }
                // Inform the user if they broke the highscore.
                if(response == 201){
                    showToast("You broke the highscore!");
                }else{
                    showToast("Try again...");
                }
            }catch(Exception e){
                Log.d("SetUserScoreTask", e.toString());
            }
        }
    }

    /**
     * onResume. When we unpause the application. Register the listeners and request the needed permissions.
     */
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        if ( ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    0);
        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);

    }

    /**
     * onPause. When the application pauses, unregister listeners.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        if ( ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    0);
        }
        locationManager.removeUpdates(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Get the SensorManager and Accelerometer sensor.
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Location
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // check if enabled and if not send user to the GSP settings
        // Better solution would be to display a dialog and suggesting to
        // go to the settings
        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the location provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);

        if ( ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    0);
        }

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location location = locationManager.getLastKnownLocation(provider);

        // Initialize the location fields
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        } else {
            Log.d("location_error", "Location not available");
        }

        System.out.println(SessionManager.getInstance().user.toString());
    }

    /**
     * getRandomNumber. Returns a random number between 0 and max.
     * @param max the maximum number we want to be able to get.
     * @return the random generated number as an integer.
     */
    public int getRandomNumber(int max){
        Random random = new Random();
        return random.nextInt(max);
    }

    /**
     * hasAccelerometer. Check if we have an accelerometer.
     * @return true if the device has an accelerometer. False if otherwise.
     */
    public boolean hasAccelerometer(){
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            return true;
        }
        return false;
    }

    /**
     * setScoreString. Used to set the score as a string to display to the user.
     * @param text
     */
    private void setScoreString(String text){
        TextView textview = (TextView)findViewById(R.id.score_text);
        textview.setText("Score: " + text.toString());
    }

    /**
     * setLocationString. Used to set the location as a string to display to the user.
     * @param text
     */
    private void setLocationString(String text){
        TextView textview = (TextView)findViewById(R.id.location_text);
        textview.setText("Location: \n" + text.toString());
    }

    /**
     * getAllSensors. Get a list of all sensors on the device.
     * @return a List containing objects of type Sensor, that the devices contains.
     */
    public List<Sensor> getAllSensors(){
        List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        return deviceSensors;
    }

    /**
     * printAllSensors. Used to print a list of sensors.
     * @param list The list containing the sensor objects.
     */
    public void printAllSensors(List<Sensor> list){
        for(int i = 0; i < list.size(); i++){
            Log.d("Sensor " + i, "" + list.get(i).toString());
        }
    }

    /**
     * onLocationChanged. Called when the user changes location
     * @param location The new location.
     */
    @Override
    public void onLocationChanged(Location location) {
        // Latitude and longitude.
        float lat = (float) (location.getLatitude());
        float lon = (float) (location.getLongitude());

        // The address.
        String ad = "no location";
        try {
            // Try using Geocoder to get the "address" of the current location
            Geocoder geo = new Geocoder(this.getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(lat, lon, 1);
            if (addresses.isEmpty()) {
                System.out.println("Waiting for Location");
                ad = "Waiting for location";
            }
            else {
                if (addresses.size() > 0) {
                    //System.out.println(addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());
                    //Toast.makeText(getApplicationContext(), "Address:- " + addresses.get(0).getFeatureName() + addresses.get(0).getAdminArea() + addresses.get(0).getSubAdminArea() + addresses.get(0).getLocality() + addresses.get(0).getSubLocality() + addresses.get(0).getSubThoroughfare(), Toast.LENGTH_LONG).show();
                    ad = addresses.get(0).getLocality();
                }else{
                    System.out.println("Address not empty but size is 0");
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace(); // getFromLocation() may sometimes fail
        }

        if(ad != null){
            setLocationString(ad);
            SessionManager.getInstance().user.lastScore.location = ad;
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        //Toast.makeText(this, "Enabled new provider " + provider, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        //Toast.makeText(this, "Disabled provider " + provider, Toast.LENGTH_SHORT).show();
    }
}


