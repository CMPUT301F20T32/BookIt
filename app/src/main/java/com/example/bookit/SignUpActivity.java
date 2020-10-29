package com.example.bookit;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
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
        fullName = findViewById(R.id.editTextTextPersonName);
        phoneNumber = findViewById(R.id.editTextPhone);
        username = findViewById(R.id.editTextTextUserName);
        password = findViewById(R.id.editTextSignUpPassword);


    }
    //On successful signup: Log the user in, traverse to the LoginActivity, it will then send the user UID bundle to main activity
    public void HandleSignUp(View view){

        boolean fullNameRight = validateFullName(username.getText().toString());
        if (fullNameRight){
            fullName.setTextColor(Color.BLACK);
        }
        if (!fullNameRight){
            fullName.setTextColor(Color.RED);
            Snackbar mySnackbar = Snackbar.make(view, "Full name too short, please have at least 2 letters.", BaseTransientBottomBar.LENGTH_SHORT);
            mySnackbar.show();
        }


        boolean emailRight = validateEmail(email.getText().toString());
        if (emailRight){
            email.setTextColor(Color.BLACK);
        }
        if (!emailRight){
            email.setTextColor(Color.RED);
            Snackbar mySnackbar = Snackbar.make(view, "Email invalid, please make sure you have an @ and a dot domain.", BaseTransientBottomBar.LENGTH_SHORT);
            mySnackbar.show();
        }

        boolean phoneNumberRight = validatePhoneNumber(phoneNumber.getText().toString());
        if (phoneNumberRight){
            phoneNumber.setTextColor(Color.BLACK);
        }
        if (!phoneNumberRight){
            phoneNumber.setTextColor(Color.RED);
            Snackbar mySnackbar = Snackbar.make(view, "Phone number incorrect, please ensure that you enter only 10 digits, and nothing else", BaseTransientBottomBar.LENGTH_SHORT);
            mySnackbar.show();
        }

        boolean usernameRight = validateUsername(username.getText().toString());
        if (usernameRight){
            username.setTextColor(Color.BLACK);
        }
        if (!usernameRight){
            username.setTextColor(Color.RED);
            Snackbar mySnackbar = Snackbar.make(view, "Username already taken, sorry, try again.", BaseTransientBottomBar.LENGTH_SHORT);
            mySnackbar.show();
        }

        boolean passwordRight = validatePassword(password.getText().toString());
        if (passwordRight){
            password.setTextColor(Color.BLACK);
        }
        if (!passwordRight){
            password.setTextColor(Color.RED);
            Snackbar mySnackbar = Snackbar.make(view, "Password length incorrect, make sure you have a number of characters between 6 and 15 both included.", BaseTransientBottomBar.LENGTH_SHORT);
            mySnackbar.show();
        }
        if (passwordRight && usernameRight && phoneNumberRight && emailRight && fullNameRight){
            //Create firebase object and log user in
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class); //Will change MainActivity to SignUpActivity
        startActivity(intent);
        finish();
        }
    }

    public boolean validateFullName(String fullName){
        if(fullName.length()>=2) {
            return true;
        }
        return false;
    }
    public boolean validateEmail(String email){
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        boolean valid = email.matches(regex);
        return valid;
    }
    public boolean validatePhoneNumber(String phoneNumber){
        if(phoneNumber.length()!=10) {
            return false;
        }
        return true;
    }
    public boolean validatePassword(String password){

        if(password.length()>=6 && password.length()<=15) {
            return true;
        }
        return false;
    }
    public boolean validateUsername(String username){
        if(username.length()>=2) {
            return true;
        }
        return false;
    }

}
