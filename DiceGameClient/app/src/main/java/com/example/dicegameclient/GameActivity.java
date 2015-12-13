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
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements SensorEventListener, LocationListener {
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    Vector3[] vectors;

    private LocationManager locationManager;
    private String provider;


    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        // In this example, alpha is calculated as t / (t + dT),
        // where t is the low-pass filter's time-constant and
        // dT is the event delivery rate.

        final float alpha = 0.8f;
        float[] gravity = new float[3];
        float[] linear_acceleration = new float[3];

        // Isolate the force of gravity with the low-pass filter.
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        // Remove the gravity contribution with the high-pass filter.
        linear_acceleration[0] = event.values[0] - gravity[0];
        linear_acceleration[1] = event.values[1] - gravity[1];
        linear_acceleration[2] = event.values[2] - gravity[2];

        if (vectors[0] == null) {
            //not set anything yet
            vectors[0] = new Vector3(linear_acceleration[0], linear_acceleration[1], linear_acceleration[2]);
        } else if (vectors[0] != null && vectors[1] == null) {
            //have not set 2nd vector
            vectors[1] = new Vector3(linear_acceleration[0], linear_acceleration[1], linear_acceleration[2]);
        } else {
            //both set, swap and replace
            vectors[0] = vectors[1];
            vectors[1] = new Vector3(linear_acceleration[0], linear_acceleration[1], linear_acceleration[2]);
        }

        TextView textview = (TextView) findViewById(R.id.accelerometer_text);
        if (vectors[0] != null && vectors[1] != null) {
            textview.setText("Accelerometer: \n" +
                            "x: " + vectors[1].x + "\n" +
                            "y: " + vectors[1].y + "\n" +
                            "z: " + vectors[1].z + "\n" +
                            "Distance: " + vectors[0].Distance(vectors[1])
            );
        }
    }

    private boolean userDidShakeDevice(float threshold) {
        if (vectors[0] != null && vectors[1] != null) {
            if (vectors[0].Distance(vectors[1]) >= threshold) {
                return true;
            }
        }
        return false;
    }

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

        vectors = new Vector3[2];

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //Location
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
        // Define the criteria how to select the locatioin provider -> use
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
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

    public int getRandomNumber(long seed, int max){
        Random random = new Random(seed);
        return random.nextInt(max);
    }

    // REGION LOCATION & SENSOR REGION
    // Checking for sensor availability.
    // Check if we have an accelerometer.
    public boolean hasAccelerometer(){
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            // Success! There's a magnetometer.
            return true;
        }
        return false;
    }

    private void setScoreString(String text){
        TextView textview = (TextView)findViewById(R.id.score_text);
        textview.setText("Score: " + text.toString());
    }

    private void setLocationString(String text){
        TextView textview = (TextView)findViewById(R.id.location_text);
        textview.setText("Location: " + text.toString());
    }

    private void setLatitudeString(float lat){
        TextView textview = (TextView)findViewById(R.id.latitude_text);
        textview.setText("Latitude: " + lat);
    }

    private void setLongitudeString(float lon){
        TextView textview = (TextView)findViewById(R.id.longitude_text);
        textview.setText("Longitude: " + lon);
    }

    private void setLocationLatLonString(String location, float lat, float lon){
        setLocationString(location);
        setLatitudeString(lat);
        setLongitudeString(lon);
    }

    public List<Sensor> getAllSensors(){
        List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        return deviceSensors;
    }

    public void printAllSensors(List<Sensor> list){
        for(int i = 0; i < list.size(); i++){
            Log.d("Sensor " + i, "" + list.get(i).toString());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        float lat = (float) (location.getLatitude());
        float lon = (float) (location.getLongitude());

        setLatitudeString(lat);
        setLongitudeString(lon);
        String ad = "";
        try {

            Geocoder geo = new Geocoder(this.getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(lat, lon, 1);
            if (addresses.isEmpty()) {
                System.out.println("Waiting for Location");
                ad = "Waiting for location";
            }
            else {
                if (addresses.size() > 0) {
                    System.out.println(addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() +", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());
                    Toast.makeText(getApplicationContext(), "Address:- " + addresses.get(0).getFeatureName()  + addresses.get(0).getAdminArea() + addresses.get(0).getSubAdminArea() + addresses.get(0).getLocality() + addresses.get(0).getSubLocality() + addresses.get(0).getSubThoroughfare(), Toast.LENGTH_LONG).show();
                    ad = addresses.get(0).getLocality();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace(); // getFromLocation() may sometimes fail
        }
        if(ad != null){
            setLocationLatLonString(ad, lat, lon);
        }else{
            System.out.println("AD == NULL!!!");
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    // END REGION LOCATION & SENSOR


}


