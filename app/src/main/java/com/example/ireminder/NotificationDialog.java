package com.example.ireminder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class NotificationDialog extends AppCompatActivity {

    private EditText description;
    private int feedback = 1;
    FirebaseAuth firebaseAuth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();

        Log.e("NotificationDialog", "Creating Alert Dialog.");
        AlertDialog.Builder builder = new AlertDialog.Builder(NotificationDialog.this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.feedback_dialog, viewGroup, false);
        description = dialogView.findViewById(R.id.descriptionText);
        builder.setView(dialogView);

        builder.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                feedback = 1;
                String strDescription = description.getText().toString();

                feedbackToDatabase(strDescription);
            }
        });

        builder.setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                feedback = 0;
                String strDescription = description.getText().toString();

                feedbackToDatabase(strDescription);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void feedbackToDatabase(String strDescription){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String userName = user.getEmail();

        SQLiteDatabase iReminderDB = getApplicationContext().openOrCreateDatabase("iReminder", MODE_PRIVATE, null);

        iReminderDB.execSQL("CREATE TABLE IF NOT EXISTS Feedback (ID INTEGER PRIMARY KEY AUTOINCREMENT, Latitude DOUBLE NOT NULL, Longitude DOUBLE NOT NULL, Description VARCHAR, Satisfied INTEGER)");
        iReminderDB.execSQL("INSERT INTO Feedback (Latitude, Longitude, Description, Satisfied) VALUES (" + LocationService.notifyLatitude + ", " + LocationService.notifyLongitude + "," + '\'' + strDescription + '\'' +", " + feedback +")");

        startActivity(new Intent(NotificationDialog.this, ProfileActivity.class));
    }
}
