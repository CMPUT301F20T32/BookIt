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

import java.util.ArrayList;
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
    private FirebaseFirestore db;
    private boolean userNameFlag;
    private boolean emailFlag;
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

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();

        fullName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus && fullName.getText()!=null){
                    if (fullName.getText().toString().length()<2){
                        fullName.setTextColor(Color.RED);
                        Snackbar snack = Snackbar.make(v, "Full name too short, please have at least 2 letters.", BaseTransientBottomBar.LENGTH_SHORT);
                        snack.show();
                    }
                    else{
                        fullName.setTextColor(Color.BLACK);
                    }
                }
            }
        });

        phoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus && phoneNumber.getText()!=null){
                    String phone = phoneNumber.getText().toString();
                    String regex = "[a-zA-Z]+";
                    if (phone.matches(regex)){
                        phoneNumber.setTextColor(Color.RED);
                        Snackbar snack = Snackbar.make(v, "Sorry, phone number must not contain letters.", BaseTransientBottomBar.LENGTH_SHORT);
                        snack.show();
                    }
                    else if (phone.length() != 10){
                        phoneNumber.setTextColor(Color.RED);
                        Snackbar snack = Snackbar.make(v, "Sorry, phone number must be exactly 10 digits.", BaseTransientBottomBar.LENGTH_SHORT);
                        snack.show();
                    }
                    else{
                        phoneNumber.setTextColor(Color.BLACK);
                    }
                }
            }
        });

        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && password.getText()!=null){
                    String pass = password.getText().toString();
                    if (pass.length()<6 || pass.length()>15){
                        password.setTextColor(Color.RED);
                        Snackbar snack = Snackbar.make(v, "Sorry, password must be at least 6 characters and no more than 16 characters. ", BaseTransientBottomBar.LENGTH_SHORT);
                        snack.show();
                    }
                    else{
                        password.setTextColor(Color.BLACK);
                    }
                }
            }
        });

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && email.getText()!=null){
                    String emailID = email.getText().toString();
                    String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
                    boolean valid = emailID.matches(regex);
                    if (!valid){
                        email.setTextColor(Color.RED);
                        Snackbar snack = Snackbar.make(v, "Sorry, incorrect email format entered, please try again. ", BaseTransientBottomBar.LENGTH_SHORT);
                        snack.show();
                    }
                    else{
                        email.setTextColor(Color.BLACK);
                    }
                }
            }
        });


        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && username.getText()!=null){
                    String user = username.getText().toString();
                    if (user.length()<2){
                        username.setTextColor(Color.RED);
                        Snackbar snack = Snackbar.make(v, "Sorry, username must have at least 2 characters. ", BaseTransientBottomBar.LENGTH_SHORT);
                        snack.show();
                    }
                    else{
                        username.setTextColor(Color.BLACK);
                        db.collection("users2")
                                .whereEqualTo("user_info.username",username.getText().toString())
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            if (!task.getResult().isEmpty()) {
                                                username.setTextColor(Color.RED);
                                                Snackbar snack = Snackbar.make(v, "Sorry, username already taken, try again.", BaseTransientBottomBar.LENGTH_SHORT);
                                                snack.show();
                                            }
                                            else{
                                                username.setTextColor(Color.BLACK);
                                            }
                                        }
                                        else{
                                            Log.d("Firestore Error","Error fetching results");
                                        }
                                    }
                                });
                    }
                }
            }
        });

    }
    public void HandleSignUp(View view){
        if (email.getText()!=null && fullName.getText()!=null
                && phoneNumber.getText()!=null&& password.getText()!=null && username.getText()!=null){
            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();

            mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                email.setTextColor(Color.BLACK);
                                Log.d("Error!?",mAuth.getCurrentUser().getEmail());
                                mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) { //Successful
                                                    Log.d("Error??",mAuth.getCurrentUser().getEmail());
                                                    Map<String, Object> completeInformation = new HashMap<>();
                                                    Map<String, Object> user_info = new HashMap<>();
                                                    Map<String, Object> my_books=new HashMap<>();
                                                    ArrayList<String> requested_books = new ArrayList<>();
                                                    ArrayList<String> accepted_books = new ArrayList<>();
                                                    ArrayList<String> borrowed_books = new ArrayList<>();
                                                    user_info.put("full_name", fullName.getText().toString());
                                                    user_info.put("email", email.getText().toString().toLowerCase());
                                                    user_info.put("phoneNumber", phoneNumber.getText().toString());
                                                    user_info.put("username", username.getText().toString());
                                                    completeInformation.put("user_info", user_info);
                                                    completeInformation.put("my_books", my_books);
                                                    completeInformation.put("requested_books", requested_books);
                                                    completeInformation.put("borrowed_books", borrowed_books);
                                                    completeInformation.put("accepted_books", accepted_books);
                                                    db.collection("users2").document(email.getText().toString().toLowerCase())
                                                            .set(completeInformation)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                public void onSuccess(Void aVoid) {
                                                                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                                                    Bundle b = new Bundle();
                                                                    b.putString("key", email.getText().toString().toLowerCase());
                                                                    intent.putExtras(b);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Snackbar snack = Snackbar.make(view, "Error adding user, please check your internet or try again.", BaseTransientBottomBar.LENGTH_SHORT);
                                                                    snack.show();
                                                                    return;
                                                                }
                                                            });
                                                } else {
                                                    email.setTextColor(Color.RED);
                                                    Snackbar snack = Snackbar.make(view, "Sorry, email already taken, try again.", BaseTransientBottomBar.LENGTH_SHORT);
                                                    snack.show();
                                                }
                                            }
                                        });
                            }
                            else {
                                email.setTextColor(Color.RED);
                                Snackbar snack = Snackbar.make(view, "Sorry, email already taken, try again.", BaseTransientBottomBar.LENGTH_SHORT);
                                snack.show();
                            }
                        }
                    });
        }
        else{
            Snackbar snack = Snackbar.make(view, "One or more fields empty, please try again.", BaseTransientBottomBar.LENGTH_SHORT);
            snack.show();
        }
    }
}
