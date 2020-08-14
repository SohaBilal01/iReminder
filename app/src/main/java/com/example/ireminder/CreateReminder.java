package com.example.ireminder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.smartlib.addresspicker.AddressPickerActivity;
import com.smartlib.addresspicker.MyLatLng;
import com.smartlib.addresspicker.Pin;

import java.util.ArrayList;

public class CreateReminder extends AppCompatActivity {

    // Firebase authentication
    FirebaseAuth firebaseAuth;

    EditText taskNameEt, latitudeEt, longitudeEt, detailsEt;
    Button placePickerBtn, createReminderBtn;
    int PLACE_PICKER_REQUEST = 1;
    float ZOOM_LEVEL = 15.0f;
    private String add;

    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                placePickerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AddressPickerActivity tf = new AddressPickerActivity();
                        Intent intent = new Intent(CreateReminder.this, AddressPickerActivity.class);
                        intent.putExtra("arg_lat_lng", new MyLatLng(31.400631, 74.210853));
                        ArrayList pinList= new ArrayList<Pin>();
                        pinList.add(new Pin(new MyLatLng(31.417006, 73.086522),"Work"));
                        pinList.add(new Pin(new MyLatLng(31.41829163025886, 73.06521473889961),"Home"));
                        intent.putExtra("list_pins",  pinList);
                        intent.putExtra("level_zoom",  ZOOM_LEVEL);
                        startActivityForResult(intent, PLACE_PICKER_REQUEST );
                    }
                });
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_reminder);

        // Create an actionbar and set the title for the activity
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Create Reminder");

        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize views
        taskNameEt = findViewById(R.id.taskNameEt);
        latitudeEt = findViewById(R.id.latitudeEt);
        longitudeEt = findViewById(R.id.longitudeEt);
        detailsEt = findViewById(R.id.detailsEt);
        placePickerBtn = findViewById(R.id.placePickerBtn);
        createReminderBtn = findViewById(R.id.createReminderBtn);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if(Build.VERSION.SDK_INT >= 23) {

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PLACE_PICKER_REQUEST);
            }
            else {
                placePickerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AddressPickerActivity tf = new AddressPickerActivity();;
                        Intent intent = new Intent(CreateReminder.this, AddressPickerActivity.class);
                        intent.putExtra("arg_lat_lng", new MyLatLng(31.400631, 74.210853));
                        ArrayList pinList= new ArrayList<Pin>();
                        pinList.add(new Pin(new MyLatLng(31.417006, 73.086522),"Work"));
                        pinList.add(new Pin(new MyLatLng(31.41829163025886, 73.06521473889961),"Home"));
                        intent.putExtra("list_pins",  pinList);
                        intent.putExtra("level_zoom",  ZOOM_LEVEL);
                        startActivityForResult(intent,PLACE_PICKER_REQUEST );
                    }
                });
            }



        } else {

            placePickerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AddressPickerActivity tf = new AddressPickerActivity();;
                    Intent intent = new Intent(CreateReminder.this, AddressPickerActivity.class);
                    intent.putExtra("arg_lat_lng", new MyLatLng(31.400631, 74.210853));
                    ArrayList pinList= new ArrayList<Pin>();
                    pinList.add(new Pin(new MyLatLng(31.417006, 73.086522),"Work"));
                    pinList.add(new Pin(new MyLatLng(31.41829163025886, 73.06521473889961),"Home"));
                    intent.putExtra("list_pins",  pinList);
                    intent.putExtra("level_zoom",  ZOOM_LEVEL);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                }
            });

        }

        createReminderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    String userName = user.getEmail();

                    String taskName = taskNameEt.getText().toString();
                    double latitude = Double.parseDouble(latitudeEt.getText().toString());
                    double longitude = Double.parseDouble(longitudeEt.getText().toString());
                    String details = detailsEt.getText().toString();

                    if(taskName == null || latitude == 0 || longitude == 0) {

                        Toast.makeText(CreateReminder.this, "Please fill in the required missing fields.", Toast.LENGTH_SHORT).show();
                    }
                    else {

                        SQLiteDatabase iReminderDB = getApplicationContext().openOrCreateDatabase("iReminder", MODE_PRIVATE, null);

                        iReminderDB.execSQL("CREATE TABLE IF NOT EXISTS Reminders (ID INTEGER PRIMARY KEY AUTOINCREMENT, RemName VARCHAR NOT NULL, Latitude DOUBLE NOT NULL, Longitude DOUBLE NOT NULL, Address VARCHAR, Details TEXT, UserId TEXT)");

                        iReminderDB.execSQL("INSERT INTO Reminders (RemName, Latitude, Longitude, Address, Details, UserId) VALUES ("+ '\'' + taskName + '\'' + ", " + latitude + ", " + longitude + "," +'\'' + add +'\''+ "," + '\'' + details + '\'' +", " + '\'' +userName + '\'' +")");

                        Toast.makeText(CreateReminder.this, "Reminder created successfully.", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(CreateReminder.this, ProfileActivity.class));

                    }


                }
                catch (Exception e) {

                    e.printStackTrace();
                    Toast.makeText(CreateReminder.this, "Error creating the reminder.\nPlease try again.", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                
                Address address = data.getParcelableExtra("address");

                String latitude = String.valueOf(address.getLatitude());
                String longitude = String.valueOf(address.getLongitude());
                add = address.getAddressLine(0);

                latitudeEt.setText(latitude);
                longitudeEt.setText(longitude);

            }
        }
    }

    private void stopService(){
        stopService(new Intent(CreateReminder.this, LocationService.class));
        startService();
    }

    private void startService(){
        LocationService.user = firebaseAuth.getCurrentUser();
        startService(new Intent(CreateReminder.this, LocationService.class));
    }

    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(user != null) {

            String username = user.getEmail();
        }
        else {
            // user isn't signed in, go to the main activity
            startActivity(new Intent(CreateReminder.this, MainActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStart() {

        checkUserStatus();

        super.onStart();
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Go to the previous activity (Parent activity)
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
