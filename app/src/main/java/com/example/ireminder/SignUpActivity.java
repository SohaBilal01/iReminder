package com.example.ireminder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.drm.DrmStore;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    // Declare variables for views
    EditText email, password;
    Button signup;
    ProgressDialog progressDialog;
    TextView haveAccount;

    // Create a firebase instance
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Create an actionbar and set the title for the activity
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Create Account for the iReminder");
        // Set back button functionality
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // Initialize variables with views
        email = findViewById(R.id.emailEt);
        password = findViewById(R.id.passwordEt);
        signup = findViewById(R.id.signUpBt);

        haveAccount = findViewById(R.id.have_account);

        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing you up ...");

        // Handle sign-up button click
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailString, passwordString;

                emailString = email.getText().toString().trim();
                passwordString = password.getText().toString().trim();

                // Validate the email and password
                if( !(Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) ) {
                    email.setError("Invalid Email. Please enter the email correctly.");
                    email.setFocusable(true);
                }
                else if(passwordString.length()<8) {
                    password.setError("Password length must be at least 8 characters.");
                    password.setFocusable(true);
                }
                else {
                    registerUser(emailString, passwordString); // Sign-up the user if no error
                }

            }
        });

        haveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                finish();
            }
        });


    }

    private void registerUser(String emailString, String passwordString) {
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(emailString, passwordString)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign up success, update UI with the signed-up user's information
                            progressDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(SignUpActivity.this, "Sign in successful\n" + user.getEmail(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpActivity.this, ProfileActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(SignUpActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

    }

    @Override
    public boolean onSupportNavigateUp() {
        // Go to the previous activity (Parent activity)
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
