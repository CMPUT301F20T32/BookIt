/*
 *  Classname: EditProfileFragment
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

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * EditProfileFragment refers to the edit My Profile functionality of the application.
 * The flow of the fragment is as follows:
 * <ul>
 *     <li> The profile fields are displayed</li>
 *     <li> If the user taps the edit button, the profile fields are validated</li>
 *     <li> Upon validation, the fields are updated in Firestore </li>
 *     <li> The fragment navigates to myProfileFragment</li>
 * </ul>
 *
 * @author Alisha Crasta
 * @version 1.0
 * @since 1.0
 */

public class EditProfileFragment  extends Fragment {

    private TextView editName, editUsername, editEmail, editPhone;
    private boolean usernameFlag;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    /**
     * This method sets this.usernameFlag to the flag
     * @param flag boolean
     */
    private void setUsernameFlag(boolean flag){
        this.usernameFlag = flag;
    }

    /**
     * This method returns this.usernameFlag
     */
    private boolean getUsernameFlag(){
        return this.usernameFlag;
    }

    /**
     * This method is called to do initial creation of a fragment
     * It inflates the layout of the fragment
     *
     * @param savedInstanceState refers to the cached state of the UI.
     * @param inflater: The LayoutInflater object that can be used to inflate any views in the fragment
     * @param container: If non-null, this is the parent view that the fragment's UI should be attached to.
     */
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    /**
     * This method does the following:
     * <ol>
     *     <li> graphical initializations of the fragment elements </li>
     *     <li> queries FireStore for the users profile info </li>
     *     <li> contains listeners for the save button </li>
     *     <li> validates the edited user profile fields </li>
     *     <li> once validated, updates Firestore with edited profile info </li>
     *     <li> navigates back to myProfileFragment </li>
     * </ol>
     *
     * @param savedInstanceState refers to the cached state of the UI.
     * @param view: The View returned by OnCreateView
     */
    public synchronized void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        //get the ids of all xml elements
        editName = view.findViewById(R.id.edit_name);
        editUsername = view.findViewById(R.id.edit_username);
        editEmail = view.findViewById(R.id.edit_email);
        editPhone = view.findViewById(R.id.edit_phone);
        TextView nameHeader = view.findViewById(R.id.nameHeader2);
        TextView usernameHeader = view.findViewById(R.id.usernameHeader2);
        TextView emailHeader = view.findViewById(R.id.emailHeader2);
        TextView phoneHeader = view.findViewById(R.id.phoneHeader2);
        Toolbar editProfileToolbar = view.findViewById(R.id.toolbar2);
        ImageView userIcon = view.findViewById(R.id.profile_icon2);
        ImageView emailIcon = view.findViewById(R.id.email_icon2);
        ImageView phoneIcon = view.findViewById(R.id.phoneIcon);
        FloatingActionButton saveChangesButton = (FloatingActionButton) view.findViewById(R.id.saveProfileChanges);
        DocumentReference userRef;

        //get the current user from Firebase
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            userRef = db.collection("users2").document(currentUser.getEmail());
            //get and set user profile fields
            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("READ_DATA", "DocumentSnapshot Data: " + document.getData());
                            HashMap<Object, String> userInfo = (HashMap<Object, String>) document.get("user_info");
                            for (Map.Entry mapElement : userInfo.entrySet()) {
                                String key = (String) mapElement.getKey();
                                String value = (String) mapElement.getValue();

                                if (key.equals("email")) {
                                    editEmail.setText(value);
                                } else if (key.equals("full_name")) {
                                    editName.setText(value);
                                } else if (key.equals("username")) {
                                    editUsername.setText(value);
                                } else if (key.equals("phoneNumber")) {
                                    editPhone.setText(value);
                                }
                            }
                        } else {
                            Log.d("READ_DATA", "No such document");
                        }
                    } else {
                        Log.d("READ_DATA", "get failed with ", task.getException());
                    }
                }
            });
        } else {
            Log.d("NULL USER", "My Profile");
            return;
        }

        //on click listener for save profile changes button
        saveChangesButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public synchronized void onClick(View v) {

                //check if any of the fields are empty
                if (editEmail.getText().toString().isEmpty() || editName.getText().toString().isEmpty() ||
                        editPhone.getText().toString().isEmpty() || editUsername.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "One or more fields are empty.", LENGTH_SHORT).show();
                    return;
                }

                //validate email
                if (!Patterns.EMAIL_ADDRESS.matcher(editEmail.getText().toString()).matches()) {
                    Toast.makeText(getActivity(), "Invalid email address", LENGTH_SHORT).show();
                    return;
                }

                //validate phone number
                if(editPhone.getText().toString().length()!=10){
                    Toast.makeText(getActivity(), "Invalid phone number, please ensure that you enter 10 digits", LENGTH_SHORT).show();
                    return;
                }

                //validate username
                db.collection("users2")
                        .whereEqualTo("user_info.username", editUsername.getText().toString())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public  void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    //no other users have the same username
                                    if (task.getResult().size() == 0) {
                                        setUsernameFlag(true);
                                    }
                                    //multiple users have the same username (shouldn't happen but check anyway)
                                    else if (task.getResult().size() > 1) {
                                        setUsernameFlag(false);
                                    }
                                    // one user has the same username
                                    else {
                                        //check if it is the current user
                                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                        String email = document.get("user_info.email").toString();
                                        if (email.equals(currentUser.getEmail())) {
                                            setUsernameFlag(true);
                                        }
                                        else{
                                            setUsernameFlag(false);
                                        }
                                    }
                                } else {
                                    Log.d("usernameError", "Error getting documents: ", task.getException());
                                }

                                if (!getUsernameFlag()) {
                                    Toast.makeText(getActivity(), "Username is already taken", LENGTH_SHORT).show();
                                }

                                else{
                                    //push edits to my profile
                                    HashMap<Object, String> editedInfo = new HashMap<>();
                                    editedInfo.put("email", editEmail.getText().toString());
                                    editedInfo.put("full_name", editName.getText().toString());
                                    editedInfo.put("phoneNumber", editPhone.getText().toString());
                                    editedInfo.put("username", editUsername.getText().toString());
                                    userRef.update("user_info", editedInfo);

                                    getFragmentManager().popBackStack();
                                }
                            }
                        });
            }
        });
    }
}