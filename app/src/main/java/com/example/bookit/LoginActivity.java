package com.example.bookit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText email;
    private EditText password;
    private String userEmail;
    private String userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Initializing login activity
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance(); //getting firebase instance
        mAuth.signOut();
        FirebaseUser currentUser = mAuth.getCurrentUser(); //assign current user state to currentUser
        if (currentUser!=null){ //Check to see if the user is logged in or not
            //Navigate to MainActivity if logged in with user's UID bundled
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            Bundle b = new Bundle();
            b.putString("key", currentUser.getUid());
            intent.putExtras(b);
            startActivity(intent);
            finish();
        }
        else{
            //If not logged in, show log in scree
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);
        }
    }
    public void NavigateToMain(final View view){
        //Navigating to main
        userEmail = email.getText().toString();
        userPassword = password.getText().toString();

        if(userEmail.length()<=1 || userPassword.length()<=1){
            //Show invalid entries for email/password
            Snackbar nullError = Snackbar.make(view, "Email/Password empty", BaseTransientBottomBar.LENGTH_SHORT);
            nullError.show();
            return;
        }
        if (userEmail!="" && userPassword!="") {
            //Sign in the user
            mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) { //Successful
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                Bundle b = new Bundle();
                                b.putString("key", user.getUid());
                                intent.putExtras(b);
                                startActivity(intent);
                                finish();
                            } else { //Failed login can only happen if there's an incorrect entry
                                Snackbar mySnackbar = Snackbar.make(view, "Email/Password incorrect.", BaseTransientBottomBar.LENGTH_SHORT);
                                mySnackbar.show();
                            }
                        }
                    });
        }
    }
    public void NavigateToSignUp(View view){
        //TODO: Need to complete SignUpActivity
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class); //Will change MainActivity to SignUpActivity
        Bundle b = new Bundle();
        b.putInt("key", 1);
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }
}