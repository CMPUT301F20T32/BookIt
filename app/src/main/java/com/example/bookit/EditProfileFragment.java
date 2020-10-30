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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;


public class EditProfileFragment  extends Fragment {

    // declare xml elements
    private TextView editName, editUsername, editEmail, editPhone;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
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
        ImageView phoneIcon = view.findViewById(R.id.imageView);
        FloatingActionButton saveChangesButton = (FloatingActionButton) view.findViewById(R.id.saveProfileChanges);

        //get the current user from Firebase TODO
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users2").document("eJl7kfYl5eRlNIs44Aqt");

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
                            } else if (key.equals("fullname")) {
                                editName.setText(value);
                            } else if (key.equals("username")) {
                                editUsername.setText(value);
                            } else if (key.equals("phone")) {
                                editPhone.setText(value);
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

        //on click listener for save profile changes button
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editEmail.getText().toString().isEmpty() || editName.getText().toString().isEmpty() ||
                        editPhone.getText().toString().isEmpty() || editUsername.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), "One or more fields are empty.", LENGTH_SHORT).show();
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(editEmail.getText().toString()).matches()){
                    Toast.makeText(getActivity(), "Invalid email address", LENGTH_SHORT).show();
                    return;
                }

                //TODO check if username is taken

                HashMap<String, Object> editedInfo =  new HashMap<>();
                editedInfo.put("email", editEmail.getText().toString());
                editedInfo.put("fullname", editName.getText().toString());
                editedInfo.put("phone", editPhone.getText().toString());
                editedInfo.put("username", editUsername.getText().toString());
                userRef.update("user_info", editedInfo);

                getFragmentManager().popBackStack();

            }

        });
    }
}
