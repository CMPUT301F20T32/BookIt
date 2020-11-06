package com.example.bookit;

import android.app.Fragment;
import android.app.FragmentTransaction;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;


public class EditProfileFragment  extends Fragment {
    /*
         Allow user to edit their own profile information
    */

    private TextView editName, editUsername, editEmail, editPhone;
    private boolean usernameFlag = true;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    boolean validEmail = true;
    String oldEmail;

    public void setUsernameFlag(boolean flag){
        this.usernameFlag = flag;
    }

    public boolean getUsernameFlag(){
        return this.usernameFlag;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

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
                                    oldEmail = value;
                                } else if (key.equals("full_name")) {
                                    editName.setText(value);
                                } else if (key.equals("username")) {
                                    editUsername.setText(value);
                                } else if (key.equals("phoneNumber")) {
                                    editPhone.setText(value);
                                }
                            }
                        } else {
                            Log.d("READ_DATA", "1. No such document");
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

                currentUser.updateEmail(editEmail.getText().toString())
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>(){
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    Log.d("emailError", "Invalid email: "+ task.getException());
                                    Toast.makeText(getActivity(), task.getException().getMessage(), LENGTH_SHORT).show();
                                    validEmail = false;
                                }
                                else{
                                    validEmail=true;
                                }
                            }
                        });

                if(!validEmail){
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
                                        if (email.equals(oldEmail)) {
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
                                    boolean emailChanged = false;
                                    if (!editEmail.getText().toString().equals(oldEmail)){
                                        emailChanged = true;
                                    }
                                    //push edits to my profile
                                    HashMap<Object, String> editedInfo = new HashMap<>();
                                    editedInfo.put("email", editEmail.getText().toString());
                                    editedInfo.put("full_name", editName.getText().toString());
                                    editedInfo.put("phoneNumber", editPhone.getText().toString());
                                    editedInfo.put("username", editUsername.getText().toString());
                                    userRef.update("user_info", editedInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (!task.isSuccessful()) {
                                                Log.d("UPDATE_DATA", "update failed with ", task.getException());
                                            }
                                        }});

                                    if (emailChanged) {
                                        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        Map<String, Object> data = new HashMap<>();
                                                        data = document.getData();

                                                        // saves the data to 'new document'
                                                        db.collection("users2").document(editEmail.getText().toString()).set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    // deletes the old document
                                                                    userRef.delete();

                                                                    MyProfileFragment myProfileFragment = new MyProfileFragment();
                                                                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                                                    transaction.replace(R.id.fragment_container, myProfileFragment);
                                                                    transaction.commit();

                                                                } else {
                                                                    Log.d("SET_DATA", "set failed with ", task.getException());
                                                                }
                                                            }
                                                        });
                                                    } else {
                                                        Log.d("READ_DATA", "userref get No such document");
                                                    }
                                                } else {
                                                    Log.d("READ_DATA", "get old user doc failed with ", task.getException());
                                                }
                                            }
                                        });
                                    }
                                    else {
                                        MyProfileFragment myProfileFragment = new MyProfileFragment();
                                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                        transaction.replace(R.id.fragment_container, myProfileFragment);
                                        transaction.commit();
                                    }
                                }
                            }
                        });
                }});

            }
        }

