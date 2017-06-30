package com.realizer.salladodriver;

import android.*;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.realizer.salladodriver.databasemodel.Driver;
import com.realizer.salladodriver.utils.Constants;
import com.realizer.salladodriver.utils.Singleton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Win on 12/9/2015.
 */
public class ServiceLocationChange extends IntentService implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private static final int TWO_MINUTES = 30000;
    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    String username;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    Location BetterLocation;
    SimpleDateFormat df;
    public static final String TAG = LoginActivity.class.getSimpleName();
    FirebaseDatabase database;
    DatabaseReference driverRef;
    Driver driver;
    SharedPreferences pref;

    public ServiceLocationChange() {
        super("ServiceLocationChange");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Singleton.getDatabase() == null)
            Singleton.setDatabase(FirebaseDatabase.getInstance());


        Toast.makeText(ServiceLocationChange.this, "Service Created", Toast.LENGTH_LONG).show();
    }

    private class BackgroundThread extends Thread {
        @Override
        public void run() {
            super.run();

            mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .addConnectionCallbacks(ServiceLocationChange.this)
                    .addOnConnectionFailedListener(ServiceLocationChange.this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
/*
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new AutoSyncServerDataTrack(), 1000 * 10, 1000 * 30);*/
        }
    }

    class AutoSyncServerDataTrack extends TimerTask {
        @Override
        public void run() {

            Log.d("Async", "ok");

            username = pref.getString("UserID", "");

        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Toast.makeText(ServiceLocationChange.this, "service start", Toast.LENGTH_SHORT).show();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(ServiceLocationChange.this, "Task performed in service", Toast.LENGTH_SHORT).show();
        df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        showNotification();

        database = Singleton.getDatabase();
        driverRef = database.getReference("Driver");
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        username = pref.getString("UserID", "");

        Query query = driverRef.child(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    driver = dataSnapshot.getValue(Driver.class);

                    BackgroundThread background = new BackgroundThread();
                    background.start();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return START_STICKY;
    }


    private void showNotification() {
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification;
        Notification.Builder builder = new Notification.Builder(ServiceLocationChange.this);

        Intent notificationIntent = new Intent(ServiceLocationChange.this, ServiceLocationChange.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent intent =
                PendingIntent.getActivity(ServiceLocationChange.this, 0, notificationIntent, 0);

        builder.setAutoCancel(true);
        builder.setContentTitle("Track");
        builder.setContentText("Tracking is on");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentIntent(intent);
        builder.setNumber(100);
        builder.setOngoing(false);  //API level 16
        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setDefaults(Notification.DEFAULT_VIBRATE);
        builder.build();

        notification = builder.getNotification();
        notificationManager.notify(0, notification);

        startForeground(101, notification);

    }


    @Override
    public void onDestroy() {
        Log.d("Test Service", "Stop");
        if(mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                mGoogleApiClient.disconnect();
            }
        }
        super.onDestroy();
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

       /* AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, intent.getIntExtra("Hour",0));
        calendar.set(Calendar.MINUTE, intent.getIntExtra("Minute", 0));
        Intent myIntent = new Intent(ServiceLocationChange.this, AlarmReceiver.class);
        myIntent.putExtra("Type","Stop");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ServiceLocationChange.this, 0, myIntent, 0);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);*/
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Toast.makeText(ServiceLocationChange.this, "On Task Removed", Toast.LENGTH_SHORT).show();


        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 100,
                restartServicePendingIntent);

        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_IN_MILLISECONDS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS)
                .setSmallestDisplacement(10);

        /*if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }*/
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        Toast.makeText(ServiceLocationChange.this, "On Connected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        //Toast.makeText(TestServicecLocation.this, "On Location Changed", Toast.LENGTH_SHORT).show();
        float accuracy = location.getAccuracy();
        driver.setLatitude(location.getLatitude());
        driver.setLongitude(location.getLongitude());

        driverRef.child(username).setValue(driver);

        Toast.makeText(ServiceLocationChange.this, "Accuracy =>" + String.valueOf(accuracy), Toast.LENGTH_SHORT).show();
        /*if(BetterLocation == null)
            BetterLocation = location;
        Toast.makeText(ServiceLocationChange.this, "On Location Changed", Toast.LENGTH_SHORT).show();

        Location tempBetterLocation = getBetterLocation(BetterLocation, location);
        if(tempBetterLocation == location) {
            if(BetterLocation != null && BetterLocation != tempBetterLocation) {
                BetterLocation = tempBetterLocation;
                float accuracy = location.getAccuracy();
                driver.setLatitude(BetterLocation.getLatitude());
                driver.setLongitude(BetterLocation.getLongitude());

                driverRef.child(username).setValue(driver);

                Toast.makeText(ServiceLocationChange.this, "Accuracy =>" + String.valueOf(accuracy), Toast.LENGTH_SHORT).show();
            }

            }*/
    }

    @Override
    public void onClick(View v) {

    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult((android.app.Activity) getApplicationContext(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }




    protected Location getBetterLocation(Location newLocation, Location currentBestLocation) {
       /* if (currentBestLocation == null) {
            // A new location is always better than no location
            return newLocation;
        }

        double distance = currentBestLocation.distanceTo(newLocation);

        if(distance>=16)
        {
            return  newLocation;
        }
        else
        {
            return currentBestLocation;
        }*/

        if (currentBestLocation == null) {
            // A new location is always better than no location
            return newLocation;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = newLocation.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved.
        if (isSignificantlyNewer) {
            return newLocation;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return currentBestLocation;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (newLocation.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;



        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return newLocation;
        } else if (isNewer && !isLessAccurate) {
            return newLocation;
        } else if (isNewer && !isSignificantlyLessAccurate ) {
            return newLocation;
        }
        return currentBestLocation;
    }

}
