/*
 *  Classname: LogInActivity
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
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * LoginActivity refers to the Login functionality of the application.
 * The flow of the activity is as follows:
 * <ul>
 *     <li> The user attempts to a log in with an already existing pair of email ID and password
 *     <li> A FirebaseAuth instance is then used to validate and sign in the said user
 *     <li> On successful entry, the user is navigated to the {@link MainActivity} (Home Screen)
 *     <li> On an unsuccessful entry, a Snackbar is displayed that appropriately highlights the error
 *     <li> If the user does not have an existing account, they can sign up
 *     <li> To sign up, after the button is pressed, the user is directed to {@link SignUpActivity}
 * </ul>
 *
 * @author Sutanshu Seth
 * @version 1.0
 * @since 1.0
 */
public class LoginActivity extends AppCompatActivity {

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
     * entering the Password of the user
     */
    private EditText password;

    /*
     * String refers to the textual content of the EditText
     * email object
     */
    private String userEmail;

    /*
     * String refers to the textual content of the EditText
     * password object
     */
    private String userPassword;

    /**
     * This method is used on the creation of this activity.
     * Essentially the flow of the app should navigate to the {@link MainActivity}
     * if the user is already signed in, and so as such, on creation of this activity
     * if there is a login token that is not null from firebaseAuth, the user
     * is automatically signed in.
     *
     * @param savedInstanceState refers to the cached state of the UI.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
         * Gets the current instance of firebase local to the phone
         * and assign current user state to currentUser
         */
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        /*
         *Check to see if the user is logged in or not
         *Navigate to MainActivity if logged in with user's UID bundled
         */
        if (currentUser != null) {
            /*
             * Create an intent that is used to navigate to the MainActivity
             * Follows the flow mentioned above: If the user is already logged
             * in, the sensible thing to do is to display the home screen.
             */
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            /*
             * Set the view of the log in screen, show the required
             * text fields to be edited.
             */
            setContentView(R.layout.activity_login);
            email = findViewById(R.id.editTextTextEmailAddress);
            password = findViewById(R.id.editTextTextPassword);
            }
    }

    /**
     * This method is used to represent the onClick action of the login
     * button that is consequently used to navigate to the
     * {@link MainActivity} only if the login fields are correct and
     * follows the specifications as indicated above.
     * @param view view that responds to the Login button being pressed.
     */
    public void NavigateToMain(final View view){
        /*
         * Gets the respective contents of the email and password
         * EditText fields respectively.
         */
        userEmail = email.getText().toString();
        userPassword = password.getText().toString();

        if (userEmail.length() <= 1 || userPassword.length() <= 1) {
            Snackbar nullError = Snackbar.make(view, "Email/Password empty", BaseTransientBottomBar.LENGTH_SHORT); //Invalid entries for email/password
            nullError.show();
            return;
        }
        if (!userEmail.equals("") && !userPassword.equals("")) {
            /*
             * At this point the fields contain valid entries in terms of length
             * and can now be checked with FirebaseAuth.
             */
            mAuth.signInWithEmailAndPassword(userEmail, userPassword)  //Sign in the user
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        /**
                         * Async handler fires when the call from the FirebaseAuth API yields a result.
                         * @param task The object that gets appended to the queue of Tasks to be executed
                         *             in a different thread.
                         */
                        @Override
                        public void onComplete (@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                /*
                                 *Sign in success, update UI with the signed-in user's information
                                 */
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                /*
                                 *Sign in failure, update UI with the appropriate SnackBar
                                 */
                                Snackbar snack = Snackbar.make(view, "Email/Password incorrect.", BaseTransientBottomBar.LENGTH_SHORT);
                                snack.show();
                            }
                        }
                    });
        }
    }

    /**
     * This method is used to represent the onClick action of the Sign Up
     * button that is consequently used to navigate to the
     * {@link SignUpActivity}.
     * @param view view that responds to the Sign Up button being pressed
     */
    public void NavigateToSignUp(View view){
        /*
         * Create an intent that is used to navigate to the SignUpActivity.
         */
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class); //Will change MainActivity to SignUpActivity
        startActivity(intent);
        finish();
    }
}