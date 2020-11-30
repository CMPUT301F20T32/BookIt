package com.example.bookit;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;


import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NotificationsFragment extends Fragment {


    public NotificationsFragment() {        // constructor
    }

    private SearchView searchView;

    private RecyclerView notificationsRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<Notification> myDataset = new ArrayList<Notification>();

    private String currentUsername;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // TODO: Rename and change types and number of parameters
    public static NotificationsFragment newInstance(String param1, String param2) {
        NotificationsFragment fragment = new NotificationsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
            }
        };
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_notifications, container, false);
        return v;
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {


        // Link the Views to the Layout
        notificationsRecyclerView = view.findViewById(R.id.notification_list);
        Toolbar myNotificationToolbar = view.findViewById(R.id.toolbar_noti);

        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        notificationsRecyclerView.setHasFixedSize(true);

        // Use a linear layout manager
        layoutManager = new LinearLayoutManager(view.getContext());
        notificationsRecyclerView.setLayoutManager(layoutManager);

        // Retrieve the database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference notificationReference = db.collection("notification");

        // Get the current User
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Get Current Username
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
                            currentUsername = document.getString("user_info.username");

                            if (currentUsername == null){
                                currentUsername = "empty string";
                            }

                            notificationReference
                                    .whereEqualTo("username", currentUsername)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                            // Clear the dataSet
                                            myDataset.clear();
                                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                                Log.d("READ_DATA", "DocumentSnapshot data: " + doc.getData());
                                                String status = (String) doc.getData().get("status");
                                                String text = (String) doc.getData().get("text");
                                                String username = (String) doc.getData().get("username");
                                                Date datetime = doc.getTimestamp("time").toDate();

                                                // Change the date format to yyyy-mm-dd hh:mm:ss
                                                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                String strDate = dateFormat.format(datetime);

                                                myDataset.add(new Notification(text, strDate));
                                            }
                                            Collections.sort(myDataset, Collections.reverseOrder());
                                            mAdapter.notifyDataSetChanged();
                                        }
                                    });
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


        // Set up the adapter
        mAdapter = new NotificationAdapter(myDataset, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
            }

            @Override
            public boolean onLongClick(View view, int position) {
                return false;
            }

        });

        notificationsRecyclerView.setAdapter(mAdapter);

        // Set Up BackButton
        myNotificationToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

}