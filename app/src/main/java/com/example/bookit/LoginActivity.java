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
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!=null){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            Bundle b = new Bundle();
            b.putString("key", currentUser.getUid());
            intent.putExtras(b);
            startActivity(intent);
            finish();
        }
        else{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);
        }
    }
    public void NavigateToMain(final View view){
        userEmail = email.getText().toString();
        userPassword = password.getText().toString();
        if(userEmail.length()<=1 || userPassword.length()<=1){
            Snackbar nullError = Snackbar.make(view, "Email/Password empty", BaseTransientBottomBar.LENGTH_SHORT);
            nullError.show();
            return;
        }
        if (userEmail!="" && userPassword!="") {
            mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                Bundle b = new Bundle();
                                b.putString("key", user.getUid());
                                intent.putExtras(b);
                                startActivity(intent);
                                finish();
                            } else {
                                Snackbar mySnackbar = Snackbar.make(view, "Email/Password incorrect.", BaseTransientBottomBar.LENGTH_SHORT);
                                mySnackbar.show();
                            }
                        }
                    });
        }
    }
    public void NavigateToSignUp(View view){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        Bundle b = new Bundle();
        b.putInt("key", 1);
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }
}