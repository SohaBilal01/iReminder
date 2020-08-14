package com.example.ireminder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.ireminder.Network.NetworkService;
import com.example.ireminder.Network.RetrofitClient;
import com.google.firebase.auth.FirebaseUser;

import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationService extends Service {
    private static final String TAG = "LocationService";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 0;
    private static final float LOCATION_DISTANCE = 10f;

    ProfileActivity profileActivity = new ProfileActivity();
    NotificationCompat.Builder builder;
    NotificationChannel channel;
    ArrayList<ReminderModel> remindersData;
    static FirebaseUser user;
    ArrayList<Location> recentLocations = new ArrayList<>();
    private NetworkService networkService;
    public static Double notifyLatitude, notifyLongitude;

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            recentLocations.add(location);
            checkLocation(mLastLocation);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }


    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
//            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };


//    LocationListener[] mLocationListeners = new LocationListener[]{
//            new LocationListener(LocationManager.PASSIVE_PROVIDER)
//    };

    public void checkLocation(Location location){
        DecimalFormat df2 = new DecimalFormat("#.##");

        final Double currentLatitude = Double.parseDouble(df2.format(location.getLatitude()));
        Double currentLongitude = Double.parseDouble(df2.format(location.getLongitude()));
        Log.e(TAG, "Current " + currentLatitude + " " + currentLongitude);

        fetchReminderData();

        for(ReminderModel m: remindersData){

            Double latitude = Double.parseDouble(df2.format(m.getLatitude()));
            Double longitude = Double.parseDouble(df2.format(m.getLongitude()));;

            Log.e(TAG, "Database " + latitude + " " + longitude);

            if(latitude.equals(currentLatitude) && longitude.equals(currentLongitude)){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationBuilder(m);

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(LocationService.this);
                    notificationManager.notify(Integer.parseInt(channel.getId()), builder.build());

                    notifyLatitude = currentLatitude;
                    notifyLongitude = currentLongitude;

                    deleteReminder(m);
                }
            }
            else{
                if(recentLocations.size() == 5){
                    // TODO Send Request via Retrofit

                    String url = recentLocations.get(0).getLatitude() + "," +
                                 recentLocations.get(0).getLongitude() + "," +
                                 recentLocations.get(1).getLatitude() + "," +
                                 recentLocations.get(1).getLongitude() + "," +
                                 recentLocations.get(2).getLatitude() + "," +
                                 recentLocations.get(2).getLongitude() + "," +
                                 recentLocations.get(3).getLatitude() + "," +
                                 recentLocations.get(3).getLongitude() + "," +
                                 recentLocations.get(4).getLatitude() + "," +
                                 recentLocations.get(4).getLongitude();

                    Log.i(TAG, "URL: "+url);

                    recentLocations.clear();
                    networkService = RetrofitClient.getRetrofitClient().create(NetworkService.class);
                    Call<LocationModel> call = networkService.sendData(url);
                    call.enqueue(new Callback<LocationModel>() {
                        @Override
                        public void onResponse(@NonNull Call<LocationModel> call, @NonNull Response<LocationModel> response) {
                            if(response.body() != null){
                                LocationModel data = response.body();

                                checkNetworkLocation(data.getLat1(), data.getLong1());
                                checkNetworkLocation(data.getLat2(), data.getLong2());
                                checkNetworkLocation(data.getLat3(), data.getLong3());
                                checkNetworkLocation(data.getLat4(), data.getLong4());
                                checkNetworkLocation(data.getLat5(), data.getLong5());
                                }
                        }

                        @Override
                        public void onFailure(@NonNull Call<LocationModel> call, @NonNull Throwable t) {
                            Toast.makeText(getApplicationContext(),"Oops try again!...",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
    }

    public void checkNetworkLocation(float latitude, float longitude) {
        DecimalFormat df2 = new DecimalFormat("#.#");

        final Double currentLatitude = Double.parseDouble(df2.format(latitude));
        Double currentLongitude = Double.parseDouble(df2.format(longitude));

        Log.e(TAG, "Current " + currentLatitude + " " + currentLongitude);

        fetchReminderData();

        for (ReminderModel m : remindersData) {

            Double remLatitude = Double.parseDouble(df2.format(m.getLatitude()));
            Double remLongitude = Double.parseDouble(df2.format(m.getLongitude()));

            Log.e(TAG, "Database " + remLatitude + " " + remLongitude);

            if (remLatitude.equals(currentLatitude) && remLongitude.equals(currentLongitude)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationBuilder(m);

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(LocationService.this);
                    notificationManager.notify(Integer.parseInt(channel.getId()), builder.build());

                    deleteReminder(m);
                }
            }
        }
    }
    private void deleteReminder(ReminderModel reminder){
        try{
            SQLiteDatabase iReminderDB = getApplicationContext().openOrCreateDatabase("iReminder", MODE_PRIVATE, null);
            iReminderDB.execSQL("DELETE FROM Reminders where ID=" + reminder.getID());

            fetchReminderData();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void fetchReminderData(){
        if(remindersData != null)
            remindersData.clear();
        else
            remindersData = new ArrayList<>();

        SQLiteDatabase iReminderDB = getApplicationContext().openOrCreateDatabase("iReminder", MODE_PRIVATE, null);
        Cursor c = iReminderDB.rawQuery("SELECT * from Reminders where UserId = \'" + user.getEmail() + "\'", null);

        int idIndex = c.getColumnIndex("ID");
        int remNameIndex = c.getColumnIndex("RemName");
        int latitudeIndex = c.getColumnIndex("Latitude");
        int longitudeIndex = c.getColumnIndex("Longitude");
        int addressIndex = c.getColumnIndex("Address");
        int detailsIndex = c.getColumnIndex("Details");
        int userIdIndex = c.getColumnIndex("UserId");

        c.moveToFirst();
        do {
            try {
                remindersData.add(new ReminderModel(c.getInt(idIndex),
                        c.getString(remNameIndex),
                        c.getDouble(latitudeIndex),
                        c.getDouble(longitudeIndex),
                        c.getString(addressIndex),
                        c.getString(detailsIndex),
                        c.getString(userIdIndex)));

            } catch(Exception e){
                e.printStackTrace();
            }
        } while(c.moveToNext());
    }

    private void notificationBuilder(ReminderModel reminder){
        // Notification Builder
        DecimalFormat df2 = new DecimalFormat("#.##");

        Intent intent = new Intent(this, NotificationDialog.class);
        // use System.currentTimeMillis() to have a unique ID for the pending intent
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        builder = new NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.drawable.ic_location_on)
                .setContentTitle(reminder.getRemName())
                .setContentIntent(pIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(df2.format(reminder.getLatitude()) + "   " + df2.format(reminder.getLongitude()) + "\n" + reminder.getDetails()))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        createNotificationChannel(reminder);
    }

    private void createNotificationChannel(ReminderModel reminder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = reminder.getRemName();
            String description = reminder.getLatitude() + "   " + reminder.getLongitude() + "\n" + reminder.getDetails();
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {

        Log.e(TAG, "onCreate");

        initializeLocationManager();

        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    LOCATION_INTERVAL,
                    LOCATION_DISTANCE,
                    mLocationListeners[0]
            );
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }

        /*try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    LOCATION_INTERVAL,
                    LOCATION_DISTANCE,
                    mLocationListeners[1]
            );
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }*/
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listener, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager - LOCATION_INTERVAL: "+ LOCATION_INTERVAL + " LOCATION_DISTANCE: " + LOCATION_DISTANCE);
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}