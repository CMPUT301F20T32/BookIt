package com.example.bookit;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText email;
    private EditText username;
    private EditText phoneNumber;
    private EditText fullName;
    private EditText password;
    private String  emailReq;
    private String passReq;
    private FirebaseFirestore db;
    private boolean flag;
    private FirebaseUser user;


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
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
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
            emailReq = email.getText().toString();
            passReq = password.getText().toString();
            if (emailReq!=null && passReq!=null){
            mAuth.createUserWithEmailAndPassword(emailReq, passReq)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                user = mAuth.getCurrentUser();
                            } else {
                                email.setTextColor(Color.RED);
                                Snackbar mySnackbar = Snackbar.make(view, "Sorry, email already taken, try again.", BaseTransientBottomBar.LENGTH_SHORT);
                                mySnackbar.show();
                            }
                        }
                    });}

            //Create firebase object and log user in
            Map<String, Object> completeInformation = new HashMap<>();
            Map<String, Object> user_info = new HashMap<>();
            user_info.put("full_name", fullName.getText().toString());
            user_info.put("email", email.getText().toString());
            user_info.put("phoneNumber", phoneNumber.getText().toString());
            user_info.put("username", username.getText().toString());
            completeInformation.put("user_info",user_info);
            mAuth.signInWithEmailAndPassword(emailReq, passReq)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                            } else {
                            }
                        }
                    });
            db.collection("users2").document(emailReq)
                    .set(completeInformation)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                            Bundle b = new Bundle();
                            b.putString("key", emailReq);
                            intent.putExtras(b);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar mySnackbar = Snackbar.make(view, "Error adding user", BaseTransientBottomBar.LENGTH_SHORT);
                            mySnackbar.show();
                            return;
                        }
                    });

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
    public boolean validatePhoneNumber(@NotNull String phoneNumber){
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
    public void queryFirebase(){

    }
    public boolean validateUsername(String username) {
        final String[] result = {""};
        db = FirebaseFirestore.getInstance();
        db.collection("users2")
                .whereEqualTo("username",username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                flag = true;
                            } else {

                                flag = false;
                            }
                        } else {
                            Log.d("usernameError", "Error getting documents: ", task.getException());
                        }
                    }
                });
        if (flag){
            return true;
        }
        return false;
    }

}
