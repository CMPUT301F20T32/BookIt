package com.example.bookit;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;

public class RetrieveInfoFragment extends Fragment {
    private TextView fullName, userName, email, phoneNumber;
    private String user;
    //private String call;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        user = getArguments().getString("user");
        //call = getArguments().getString("CallFrom");

        if (user != null) {
            Toast.makeText(getContext(), "Data received!", LENGTH_SHORT).show();
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_retrieve_info, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar retrieveInfoToolbar = view.findViewById(R.id.toolbar);
        fullName = view.findViewById(R.id.nameTextView);
        userName = view.findViewById(R.id.userNameTextView);
        email = view.findViewById(R.id.emailTextView);
        phoneNumber = view.findViewById(R.id.phoneTextView);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference colRef = db.collection("users2");

        if (user != null) {
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
                                            userName.setText(value);
                                        } else if (key.equals("email")) {
                                            email.setText(value);
                                        } else if (key.equals("phoneNumber")) {
                                            phoneNumber.setText(value);
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(getActivity(), "Something went wrong.", LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(getActivity(), "Couldn't receive user information.", LENGTH_SHORT).show();
        }

        retrieveInfoToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }
}
