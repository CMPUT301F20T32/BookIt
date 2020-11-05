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

import java.util.HashMap;
import java.util.Map;

public class MyProfileFragment  extends Fragment {
    /*
         Allow user to view their own profile information
    */

    // declare xml elements
    private TextView fullName, username, email, phone;

    public MyProfileFragment() {
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_profile, container, false);
    }

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
        Toolbar myProfileToolbar = view.findViewById(R.id.toolbar);
        ImageView userIcon = view.findViewById(R.id.profileIcon);
        ImageView emailIcon = view.findViewById(R.id.emailIcon);
        ImageView phoneIcon = view.findViewById(R.id.phoneIcon);

        //get the current user from Firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
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
                        }
                        else{
                            Log.d("READ_DATA", "No such document");
                        }
                    }
                    else {
                        Log.d("READ_DATA", "get failed with ", task.getException());
                    }
                }
            });

        }
        else {
            Log.d("NULL USER","My Profile");
            return;
        }

        //on click listener for edit profile button
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditProfileFragment editProfileFragment = new EditProfileFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, editProfileFragment); // give your fragment container id in first parameter
                transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
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

        //on click listener for back navigation
        myProfileToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

    }
}