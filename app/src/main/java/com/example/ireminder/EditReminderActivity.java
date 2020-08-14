package com.example.ireminder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.smartlib.addresspicker.AddressPickerActivity;
import com.smartlib.addresspicker.MyLatLng;
import com.smartlib.addresspicker.Pin;

import java.util.ArrayList;

public class EditReminderActivity extends AppCompatActivity {

    EditText taskNameEt, latitudeEt, longitudeEt, detailsEt;
    Button placePickerBtn, updateReminderBtn;
    private final int PLACE_PICKER_REQUEST = 1;
    private ReminderModel reminder;
    float ZOOM_LEVEL = 15.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_reminder);

        initializeComponents();
        fillData();
        onListener();
    }

    private void initializeComponents(){
        taskNameEt = findViewById(R.id.editTaskNameEt);
        latitudeEt = findViewById(R.id.editLatitudeEt);
        longitudeEt = findViewById(R.id.editLongitudeEt);
        detailsEt = findViewById(R.id.editDetailsEt);
        placePickerBtn = findViewById(R.id.editPlacePickerBtn);
        updateReminderBtn = findViewById(R.id.editReminderBtn);
    }

    private void fillData(){
        reminder = ProfileActivity.updateReminder;

        taskNameEt.setText(reminder.getRemName());
        latitudeEt.setText(""+reminder.getLatitude());
        longitudeEt.setText(""+reminder.getLongitude());
        detailsEt.setText(reminder.getDetails());
    }

    private void onListener(){
        placePickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddressPickerActivity tf = new AddressPickerActivity();;
                Intent intent = new Intent(EditReminderActivity.this, AddressPickerActivity.class);
                intent.putExtra("arg_lat_lng", new MyLatLng(31.400631, 74.210853));
                ArrayList pinList= new ArrayList<Pin>();
                pinList.add(new Pin(new MyLatLng(31.417006, 73.086522),"Work"));
                pinList.add(new Pin(new MyLatLng(31.41829163025886, 73.06521473889961),"Home"));
                intent.putExtra("list_pins",  pinList);
                intent.putExtra("level_zoom",  ZOOM_LEVEL);
                startActivityForResult(intent,PLACE_PICKER_REQUEST);
            }
        });

        updateReminderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SQLiteDatabase iReminderDB = getApplicationContext().openOrCreateDatabase("iReminder", MODE_PRIVATE, null);
                    iReminderDB.execSQL("Update Reminders" +
                            " SET RemName = \'" + taskNameEt.getText().toString() + "\'" +
                            ",Latitude =  " + Double.parseDouble(latitudeEt.getText().toString()) +
                            ",Longitude =  " + Double.parseDouble(longitudeEt.getText().toString()) +
                            ",Details = \'" + detailsEt.getText().toString() + "\'" +
                            " where ID = " + reminder.getID());

                    Toast.makeText(EditReminderActivity.this, "Reminder Updated", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EditReminderActivity.this, ProfileActivity.class));
                }
                catch(Exception e){
                    e.printStackTrace();
                    Toast.makeText(EditReminderActivity.this, "Error Updating the reminder.\nPlease try again.", Toast.LENGTH_SHORT).show();
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

                latitudeEt.setText(latitude);
                longitudeEt.setText(longitude);

            }
        }
    }
}
