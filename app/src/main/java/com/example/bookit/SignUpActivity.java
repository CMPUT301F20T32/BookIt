package com.example.bookit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText email;
    private EditText username;
    private EditText phoneNumber;
    private EditText fullName;
    private EditText password;
    private String userEmail;
    private String userPassword;
    private String requiredUsername;
    private String userPhoneNumber;
    private String userFullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        email = findViewById(R.id.editTextTextSignUpEmailAddress);
        username = findViewById(R.id.editTextTextPersonName);
        phoneNumber = findViewById(R.id.editTextPhone);
        fullName = findViewById(R.id.editTextTextUserName);
        password = findViewById(R.id.editTextSignUpPassword);

    }

    //On successful signup: Log the user in, traverse to the LoginActivity, it will then send the user UID bundle to main activity

    public void HandleSignUp(View view){

        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class); //Will change MainActivity to SignUpActivity
        Bundle b = new Bundle();
        b.putInt("key", 1);
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }

    //TODO: Need to complete these validation functions
    public boolean validateFullName(String fullName){
        return true;
    }
    public boolean validateEmail(String email){
        return true;
    }
    public boolean validatePhoneNumber(String phoneNumber){
        return true;
    }
    public boolean validatePassword(String password){
        return true;
    }
    public boolean validateUsername(String username){
        return true;
    }

}
