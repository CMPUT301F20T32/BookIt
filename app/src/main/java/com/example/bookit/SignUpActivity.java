/*
 *  Classname: SignUpActivity
 *  Version: 1.0
 *  Date: 06/11/2020
 *  Copyright notice:
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *         http://www.apache.org/licenses/LICENSE-2.0
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
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
import android.widget.Button;
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

/**
 * SignUpActivity refers to the Login functionality of the application.
 * The flow of the activity is as follows:
 * <ul>
 *     <li> The user attempts to sign up by filling the respective fields.
 *     <li> Upon an illegal entry, the respective text will be colored red.
 *     <li> On correct entries, the text is turned back to/stays black colored.
 *     <li> The user then proceeds to hit the sign up button.
 *     <li> Now calls to the Firebase APIs are made and fields are again validated.
 *     <li> On correct validation, the user proceeds to the {@link MainActivity}.
 * </ul>
 *
 * @author Sutanshu Seth
 * @version 1.0
 * @since 1.0
 */
public class SignUpActivity extends AppCompatActivity {
    /*
     * FirebaseAuth variable to hold the current user token
     */
    private FirebaseAuth mAuth;

    /*
     * EditText object that refers to the EditText field for
     * entering the Email ID of the user
     */
    private EditText email;

    /*
     * EditText object that refers to the EditText field for
     * entering the username of the user
     */
    private EditText username;

    /*
     * EditText object that refers to the EditText field for
     * entering the phone number of the user
     */
    private EditText phoneNumber;

    /*
     * EditText object that refers to the EditText field for
     * entering the full name of the user
     */
    private EditText fullName;

    /*
     * EditText object that refers to the EditText field for
     * entering the password of the user
     */
    private EditText password;
    private Button button;

    /*
     * FirebaseFirestore variable to hold the current
     * current reference to the database
     */
    private FirebaseFirestore db;

    /**
     * This method is used on the creation of this activity.
     * Essentially it contains onFocusChange listeners for
     * each of the EditText fields, and it listens for changes
     * in focus and then checks the content of each of the fields
     * and then highlights the illegal entries.
     * The restrictions for content entries are as follows:
     * <ul>
     *     <li> fullName: Needs a minimum of two characters.
     *     <li> phoneNumber: Needs exactly 10 numbers.
     *     <li> password: Needs a length in the range (6,15).
     *     <li> email: Needs an @ and a . domain.
     *     <li> username: Needs to be unique (queried against db).
     * </ul>
     * Note that fullName can contain a number as to accommodate names
     * such as that of Elon Musk's son: "X AE A-12".
     *
     * @param savedInstanceState refers to the cached state of the UI.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
         * Set the view for the sign up screen
         */
        setContentView(R.layout.activity_sign_up);
        email = findViewById(R.id.editTextTextSignUpEmailAddress);
        fullName = findViewById(R.id.editTextTextPersonName);
        phoneNumber = findViewById(R.id.editTextPhone);
        username = findViewById(R.id.editTextTextUserName);
        password = findViewById(R.id.editTextSignUpPassword);
        button = findViewById(R.id.signUp);
        button.setEnabled(false);
        /*
         * Get the current instances of FirebaseAuth and Firestore
         */
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        /*
         * onFocusChange listener to follow the flow as specified above.
         * Note the colour highlighting to showcase errors.
         */
        fullName.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            /**
             * Method to detect changes on focus, if the focus is changed,
             * and there is content in the field, we can immediately analyse
             * it for correctness.
             *
             * @param v refers to the view that is the EditText in question
             *          in this case
             * @param hasFocus refers to the EditText having focus, is
             *                 <code>true</code> when the user is typing/in
             *                 the field and <code>false</code> otherwise
             */
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if( !hasFocus && fullName.getText() != null){
                    /*
                     * If the EditText contains something and is out of focus.
                     */
                    if (fullName.getText().toString().length()<2){
                        /*
                         * Validate according to the specifications above.
                         */
                        fullName.setTextColor(Color.RED);   //Error highlighting
                        Snackbar snack = Snackbar.make(v, "Full name too short, please have at least 2 letters.", BaseTransientBottomBar.LENGTH_SHORT);
                        snack.show();
                    } else {
                        fullName.setTextColor(Color.BLACK); //Normalizing correct input
                    }
                }
            }
        });

        /*
         * onFocusChange listener to follow the flow as specified above.
         * Note the colour highlighting to showcase errors.
         */
        phoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            /**
             * Method to detect changes on focus, if the focus is changed,
             * and there is content in the field, we can immediately analyse
             * it for correctness.
             *
             * @param v refers to the view that is the EditText in question
             *          in this case
             * @param hasFocus refers to the EditText having focus, is
             *                 <code>true</code> when the user is typing/in
             *                 the field and <code>false</code> otherwise
             */
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus && phoneNumber.getText() != null){
                    /*
                     * If the EditText contains something and is out of focus.
                     */
                    String phone = phoneNumber.getText().toString();
                    String regex = "[a-zA-Z]+";

                    /*
                     * Validate according to the specifications above.
                     */
                    if (phone.matches(regex)){
                        /*
                         * Check to see if the phone number has letters
                         */
                        phoneNumber.setTextColor(Color.RED);
                        Snackbar snack = Snackbar.make(v, "Sorry, phone number must not contain letters.", BaseTransientBottomBar.LENGTH_SHORT);
                        snack.show();
                    }
                    else if (phone.length() != 10) {
                        /*
                         * Check to see if the phone number is of right length
                         */
                        phoneNumber.setTextColor(Color.RED);        //Error highlighting
                        Snackbar snack = Snackbar.make(v, "Sorry, phone number must be exactly 10 digits.", BaseTransientBottomBar.LENGTH_SHORT);
                        snack.show();
                    }
                    else{
                        phoneNumber.setTextColor(Color.BLACK);      //Input normalization
                    }
                }
            }
        });

        /*
         * onFocusChange listener to follow the flow as specified above.
         * Note the colour highlighting to showcase errors.
         */
        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            /**
             * Method to detect changes on focus, if the focus is changed,
             * and there is content in the field, we can immediately analyse
             * it for correctness
             *
             * @param v refers to the view that is the EditText in question
             *          in this case
             * @param hasFocus refers to the EditText having focus, is
             *                 <code>true</code> when the user is typing/in
             *                 the field and <code>false</code> otherwise
             */
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && password.getText() != null){
                    /*
                     * If the EditText contains something and is out of focus.
                     */
                    String pass = password.getText().toString();
                    if (pass.length() < 6 || pass.length() > 15){
                        /*
                         * Check to see if the password's length falls in the (6,15) range.
                         */
                        password.setTextColor(Color.RED);       //Error Highlighting
                        Snackbar snack = Snackbar.make(v, "Sorry, password must be at least 6 characters and no more than 16 characters. ", BaseTransientBottomBar.LENGTH_SHORT);
                        snack.show();
                    }
                    else {
                        password.setTextColor(Color.BLACK);     //Input normalization
                    }
                }
            }
        });

        /*
         * onFocusChange listener to follow the flow as specified above.
         * Note the colour highlighting to showcase errors.
         */
        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            /**
             * Method to detect changes on focus, if the focus is changed,
             * and there is content in the field, we can immediately analyse
             * it for correctness
             *
             * @param v refers to the view that is the EditText in question
             *          in this case.
             * @param hasFocus refers to the EditText having focus, is
             *                 <code>true</code> when the user is typing/in
             *                 the field and <code>false</code> otherwise.
             */
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && email.getText() != null){
                    /*
                     * If the EditText contains something and is out of focus.
                     */
                    String emailID = email.getText().toString();
                    String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
                    boolean valid = emailID.matches(regex);
                    if (!valid) {
                        /*
                         * Email entered is invalid as it violates the regex pattern.
                         */
                        email.setTextColor(Color.RED);      //Error highlighting
                        Snackbar snack = Snackbar.make(v, "Sorry, incorrect email format entered, please try again. ", BaseTransientBottomBar.LENGTH_SHORT);
                        snack.show();
                    }
                    else {
                        email.setTextColor(Color.BLACK);        //Input normalization
                    }
                }
            }
        });

        /*
         * onFocusChange listener to follow the flow as specified above.
         * Note the colour highlighting to showcase errors.
         */
        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            /**
             * Method to detect changes on focus, if the focus is changed,
             * and there is content in the field, we can immediately analyse
             * it for correctness.
             *
             * @param v refers to the view that is the EditText in question
             *          in this case
             * @param hasFocus refers to the EditText having focus, is
             *                 <code>true</code> when the user is typing/in
             *                 the field and <code>false</code> otherwise
             */
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && username.getText() != null){
                    /*
                     * If the EditText contains something and is out of focus.
                     */
                    String user = username.getText().toString();
                    if (user.length() < 2){
                        /*
                         * First check to see if a username is of valid length.
                         */
                        username.setTextColor(Color.RED);       //Error highlighting
                        Snackbar snack = Snackbar.make(v, "Sorry, username must have at least 2 characters. ", BaseTransientBottomBar.LENGTH_SHORT);
                        snack.show();
                    }
                    else {
                        /*
                         * Username is of right length and now check for uniqueness
                         */
                        username.setTextColor(Color.BLACK);         //Input normalizing

                        /*
                         * Query that checks if the username is already present in the database
                         */
                        db.collection("users2")
                                .whereEqualTo("user_info.username",username.getText().toString())
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    /**
                                     * Async handler fires when the call from the Firestore API yields a result
                                     * @param task The object that gets appended to the queue of Tasks to be executed
                                     *             in a different thread
                                     */
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            /*
                                             * If the query *executed* successfully
                                             */
                                            if (!task.getResult().isEmpty()) {
                                                /*
                                                 * If the query has a results: indicating duplicate entry
                                                 */
                                                username.setTextColor(Color.RED);       //Error Highlighting
                                                Snackbar snack = Snackbar.make(v, "Sorry, username already taken, try again.", BaseTransientBottomBar.LENGTH_SHORT);
                                                snack.show();
                                            }
                                            else {
                                                button.setEnabled(true);
                                                username.setTextColor(Color.BLACK);     //Input normalization
                                            }
                                        }
                                        else {
                                            Log.d("Firestore Error","Error fetching results");      //May happen due to internet connection
                                        }
                                    }
                                });
                    }
                }
            }
        });
    }

    /**
     * This method is used to represent the onClick action of the Sign Up
     * button that is consequently used to navigate to the
     * {@link MainActivity} after a successful user creation process.
     * The flow of this method is as follows:
     * <ul>
     *     <li> The method first checks if all fields are non empty.
     *     <li> Then a user is created.
     *     <li> Then that user is signed in.
     *     <li> Then the appropriate data is added to Firestore.
     *     <li> Then the flow goes to the {@link MainActivity}.
     * </ul>
     * @param view view that responds to the Sign Up button being pressed.
     */
    public void HandleSignUp(View view){
        if (!button.isEnabled()){
            Snackbar snack = Snackbar.make(view, "Sorry, please check your input.", BaseTransientBottomBar.LENGTH_SHORT);
            snack.show();
            return;
        }

        if (email.getText()!=null && fullName.getText() != null &&
                phoneNumber.getText() != null && password.getText() != null &&
                username.getText() != null){
            /*
             * Checks to see if the button is being pressed on empty fields
             * If it isn't then the validation would've happened by the listeners
             * above.
             */

            /*
             * Firebase references
             */
            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();

            //Create the user
            mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        /**
                         * Async handler fires when the call from the FirebaseAuth API yields a result
                         * @param task The object that gets appended to the queue of Tasks to be executed
                         *             in a different thread
                         */
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                email.setTextColor(Color.BLACK);        //Input normalization
                                mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                            /**
                                             * Async handler fires when the call from the FirebaseAuth API yields a result
                                             * @param task The object that gets appended to the queue of Tasks to be executed
                                             *             in a different thread
                                             */
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    /*
                                                     * The user is signed in so now it is safe to add relevant data
                                                     * to firestore.
                                                     */
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

                                                    //Setting data in Firestore
                                                    db.collection("users2").document(email.getText().toString().toLowerCase())
                                                            .set(completeInformation)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                /**
                                                                 * Async handler fires when the call from the Firestore API yields a result
                                                                 * @param aVoid onSuccess parameter that is unused as desired by the API
                                                                 */
                                                                public void onSuccess(Void aVoid) {
                                                                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                /**
                                                                 * Async handler fires when the call from the Firestore API does not yield a result
                                                                 * @param e Used for debugging
                                                                 */
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Snackbar snack = Snackbar.make(view, "Error adding user, please check your internet or try again.", BaseTransientBottomBar.LENGTH_SHORT);
                                                                    snack.show();
                                                                    return;
                                                                }
                                                            });
                                                } else {
                                                    /*
                                                     * Email already taken, failed to sign in user
                                                     */
                                                    email.setTextColor(Color.RED);      //Error Highlighting
                                                    Snackbar snack = Snackbar.make(view, "Sorry, email already taken, try again.", BaseTransientBottomBar.LENGTH_SHORT);
                                                    snack.show();
                                                }
                                            }
                                        });
                            }
                            else {
                                /*
                                 *  Email already taken, failed to create user
                                 */
                                email.setTextColor(Color.RED);      //Error Highlighting
                                Snackbar snack = Snackbar.make(view, "Sorry, email already taken, try again.", BaseTransientBottomBar.LENGTH_SHORT);
                                snack.show();
                            }
                        }
                    });
        }
        else {
            /*
             * Empty fields error
             */
            Snackbar snack = Snackbar.make(view, "One or more fields empty, please try again.", BaseTransientBottomBar.LENGTH_SHORT);
            snack.show();
        }
    }
}
