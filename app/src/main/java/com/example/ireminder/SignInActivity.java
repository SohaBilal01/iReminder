package com.example.ireminder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.SigningInfo;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.regex.Pattern;

public class SignInActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;
    EditText email, password;
    Button signin;
    TextView notHaveAccount, forgotPassword;
    SignInButton googleLogin;

    GoogleSignInClient googleSignInClient;

    // Declare an instance of FirebaseAuth
    private FirebaseAuth mAuth;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Create an actionbar and set the title for the activity
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Sign In to the iReminder");
        // Set back button functionality
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.emailEt);
        password = findViewById(R.id.passwordEt);
        signin = findViewById(R.id.signInBt);
        notHaveAccount = findViewById(R.id.not_have_account);
        forgotPassword = findViewById(R.id.forgot_password);
        googleLogin = findViewById(R.id.google_login);


        // Handle sign-in button click
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Take the input data from the user and store it
                String emailString, passwordString;
                emailString = email.getText().toString();
                passwordString = password.getText().toString().trim();

                if( !(Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) ) {
                    email.setError("Invalid Email entered. Please enter the email correctly.");
                    email.setFocusable(true);
                }
                else {
                    signinUser(emailString, passwordString);
                }

            }
        });

        // Handle text view click listener
        notHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
                finish();
            }
        });

        // Handle the recover password view click listenser
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecoverPasswordDialog();
            }
        });

        // Register the Google login button
        googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        progressDialog = new ProgressDialog(this);

    }

    private void showRecoverPasswordDialog() {
        // Alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");

        // set layout of dialog to linear layout
        LinearLayout linearLayout = new LinearLayout(this);

        // Set views in the dialog
        final EditText email = new EditText(this);
        email.setHint("Email");
        email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        email.setMinEms(15);

        linearLayout.addView(email);
        linearLayout.setPadding(15, 15, 15, 15);

        builder.setView(linearLayout);

        // Buttons on the dialog
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String emailString = email.getText().toString().trim();
                beginRecovery(emailString);

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Show the dialog
        builder.create().show();
    }

    private void beginRecovery(String emailString) {

        progressDialog.setMessage("Sending email ...");
        progressDialog.show();

        mAuth.sendPasswordResetEmail(emailString)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()) {
                            Toast.makeText(SignInActivity.this, "Email sent.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(SignInActivity.this, "Email could not be sent due to some error.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(SignInActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signinUser(String emailString, String passwordString) {

        progressDialog.setMessage("Signing In ...");

        progressDialog.show();
        mAuth.signInWithEmailAndPassword(emailString, passwordString)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();

                            // On successfully logging in
                            startActivity(new Intent(SignInActivity.this, ProfileActivity.class));
                            finish();
                        } else {
                            progressDialog.dismiss();
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(SignInActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        // Go to the previous activity (Parent activity)
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            // Show the user email in a toast
                            Toast.makeText(SignInActivity.this, "" + user.getEmail(), Toast.LENGTH_SHORT).show();
                            // Go to the profile of the user after successfully logging in
                            startActivity(new Intent(SignInActivity.this, ProfileActivity.class));
                            finish();
                            // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignInActivity.this, "Login failed ...", Toast.LENGTH_SHORT).show();
                            // updateUI(null);
                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignInActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
