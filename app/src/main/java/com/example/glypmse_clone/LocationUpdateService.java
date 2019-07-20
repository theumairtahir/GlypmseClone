package com.example.glypmse_clone;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;

public class LocationUpdateService extends Service {

    int counter = 0;

    public LocationUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        startTimer();
        return START_STICKY;
    }

    private Timer timer;
    private TimerTask timerTask;

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 10 second
        timer.schedule(timerTask, 1000, 10000); //
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.i("in timer", "in timer ++++  " + (counter++));
                getDeviceLatestLocation();
            }
        };
    }

    private void updateLocation(LatLng location) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference users = db.getReference("users");
        SharedPreferences preferences = getSharedPreferences(getResources().getString(R.string.glympse_shared_preferences), MODE_PRIVATE);
        users.child(preferences.getString("phoneNumber", "")).child("lastPosition").setValue(new com.example.glypmse_clone.Models.LatLng(location.latitude, location.longitude));
    }

    private LatLng getDeviceLatestLocation() {
        final LatLng[] latestLocation = {null};
        FusedLocationProviderClient fusedLocationProviderClient = new FusedLocationProviderClient(LocationUpdateService.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.i("permission_check","Permission is not authenticated");
            }
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    latestLocation[0] = new LatLng(location.getLatitude(), location.getLongitude());
                    updateLocation(latestLocation[0]);
                }
            }
        });
        return latestLocation[0];
    }

    /**
     * not needed
     */
    public void stopTimerTask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onDestroy() {
        stopTimerTask();
        super.onDestroy();
    }
}
