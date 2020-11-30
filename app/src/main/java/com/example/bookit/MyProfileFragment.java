/*
 *  Classname: MyProfileFragment
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
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * MyProfileFragment refers to the view My Profile functionality of the application.
 * The flow of the fragment is as follows:
 * <ul>
 *     <li> The profile fields are displayed</li>
 *     <li> If the user taps the back button, the fragment navigates to the previous Activity</li>
 *     <li> If the user taps the logout button, the </li>
 *     <li> If the user taps the edit button, the fragment navigates to editProfileFragment </li>
 * </ul>
 *
 * @author Alisha Crasta
 * @version 1.0
 * @since 1.0
 */
public class MyProfileFragment  extends Fragment {
    /*
         Allow user to view their own profile information
    */

    // declare xml elements
    private TextView fullName, username, email, phone;
    private String user;

    public MyProfileFragment() {
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
        user = getArguments().getString("user");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_profile, container, false);
    }


    /**
     * This method does the following:
     * <ol>
     *     <li> graphical initializations of the fragment elements </li>
     *     <li> queries FireStore for the users profile info </li>
     *     <li> contains listeners for the back, logout and edit buttons </li>
     * </ol>
     *
     * @param savedInstanceState refers to the cached state of the UI.
     * @param view: The View returned by OnCreateView
     */
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        //get the ids of all xml elements
        fullName = view.findViewById(R.id.profileName);
        username = view.findViewById(R.id.profileUsername);
        email = view.findViewById(R.id.profileEmail);
        phone = view.findViewById(R.id.profilePhone);
        TextView nameHeader = view.findViewById(R.id.nameHeader);
        TextView usernameHeader = view.findViewById(R.id.usernameHeader);
        TextView emailHeader = view.findViewById(R.id.emailHeader);
        TextView phoneHeader = view.findViewById(R.id.phoneHeader);
        FloatingActionButton editProfileButton = (FloatingActionButton) view.findViewById(R.id.editProfileButton);
        FloatingActionButton logoutButton = (FloatingActionButton) view.findViewById(R.id.logoutButton);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ImageView userIcon = view.findViewById(R.id.profileIcon);
        ImageView emailIcon = view.findViewById(R.id.emailIcon);
        ImageView phoneIcon = view.findViewById(R.id.phoneIcon);


        //get the current user from Firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            editProfileButton.setVisibility(view.GONE);
            editProfileButton.setEnabled(false);
            logoutButton.setVisibility(view.GONE);
            logoutButton.setEnabled(false);
            toolbar.setTitle("User Profile");

            CollectionReference colRef = db.collection("users2");
            colRef.whereEqualTo("user_info.username", user).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("User", document.getId() + " => " + document.getData());
                                    HashMap<Object, String> userInfo = (HashMap<Object, String>) document.get("user_info");
                                    for (Map.Entry mapElement : userInfo.entrySet()) {
                                        String key = (String) mapElement.getKey();
                                        String value = (String) mapElement.getValue();

                                        if (key.equals("full_name")) {
                                            fullName.setText(value);
                                        } else if (key.equals("username")) {
                                            username.setText(value);
                                        } else if (key.equals("email")) {
                                            email.setText(value);
                                        } else if (key.equals("phoneNumber")) {
                                            phone.setText(value);
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(getActivity(), "Something went wrong.", LENGTH_SHORT).show();
                                Log.d("READ_DATA", "get failed with ", task.getException());
                            }
                        }
                    });
        } else {
            if (currentUser != null) {
                //get and set users profile fields
                DocumentReference userRef = db.collection("users2").document(currentUser.getEmail());
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
                                        email.setText(value);
                                    } else if (key.equals("full_name")) {
                                        fullName.setText(value);
                                    } else if (key.equals("username")) {
                                        username.setText(value);
                                    } else if (key.equals("phoneNumber")) {
                                        phone.setText(value);
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

            //on click listener for edit profile button
            editProfileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditProfileFragment editProfileFragment = new EditProfileFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, editProfileFragment); // give your fragment container id in first parameter
                    //transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
                    transaction.commit();
                }
            });

            //on click listener for log out  button
            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            });
        }

        //on click listener for back navigation
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

    }
}