package com.example.ireminder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    // Firebase authentication
    private FirebaseAuth firebaseAuth;

    private TextView profile;
    private ListView remindersList;
    private ArrayList<ReminderModel> remindersData;
    private SQLiteDatabase iReminderDB;
    public static ReminderModel updateReminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Create an actionbar and set the title for the activity
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Welcome to the iReminder");

        initializeComponents();
        onListeners();
        startLocationService();
    }

    private void startLocationService(){
        LocationService.user = firebaseAuth.getCurrentUser();
        startService(new Intent(ProfileActivity.this, LocationService.class));
    }

    private void initializeComponents(){
        iReminderDB = getApplicationContext().openOrCreateDatabase("iReminder", MODE_PRIVATE, null);
        firebaseAuth = FirebaseAuth.getInstance();
        profile = findViewById(R.id.profile);
        remindersList = findViewById(R.id.reminders_list);
        remindersData = new ArrayList<>();

        fetchReminderData();
    }

    private void onListeners(){

        remindersList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final ReminderModel reminder = remindersData.get(position);

                try {
                    // Build an AlertDialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);

                    // Set a title for alert dialog
                    builder.setTitle("Delete Reminder?");

                    // Ask the final question
                    builder.setMessage("Are you sure you want to delete the reminder?");

                    // Set the alert dialog yes button click listener
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do something when user clicked the Yes button
                            iReminderDB.execSQL("DELETE FROM Reminders where ID = " + reminder.getID());
                        }
                    });

                    // Set the alert dialog no button click listener
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do something when No button clicked
                        }
                    });

                    AlertDialog dialog = builder.create();
                    // Display the alert dialog on interface
                    dialog.show();


                    fetchReminderData();
                }
                catch(Exception e){
                    e.printStackTrace();
                    Toast.makeText(ProfileActivity.this, "Error deleting the reminder.", Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });

        remindersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateReminder = remindersData.get(position);
                startActivity(new Intent(ProfileActivity.this, EditReminderActivity.class));
            }
        });
    }

    public void fetchReminderData(){
        remindersData.clear();

        checkUserStatus();
        try{
        Cursor c = iReminderDB.rawQuery("SELECT * from Reminders where UserId = \'" + firebaseAuth.getCurrentUser().getEmail() + "\'", null);

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

//                getAddress(c.getDouble(latitudeIndex), c.getDouble(longitudeIndex));

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

        RemindersViewAdapter arrayAdapter = new RemindersViewAdapter(this, remindersData);
        remindersList.setAdapter(null);
        remindersList.setAdapter(arrayAdapter);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void checkUserStatus() {
        if(firebaseAuth == null)
            firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(user != null) {
            // user is signed in, stay on this activity
            profile.setText("Welcome " + user.getEmail());
        }
        else {
            // user isn't signed in, go to the main activity
            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
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
    protected void onResume(){
        fetchReminderData();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.action_logout) {
            firebaseAuth.signOut();
            checkUserStatus();
        }
        else if(id == R.id.action_add) {
            startActivity(new Intent(ProfileActivity.this, CreateReminder.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Go to the previous activity (Parent activity)
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
