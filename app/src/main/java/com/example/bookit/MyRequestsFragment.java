package com.example.bookit;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MyRequestsFragment extends Fragment {
    // Re-sort the list for each time you click on the Button.
    private Button acceptedTab;
    private Button pendingTab;

    private RecyclerView availableRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyRequestsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyRequests.
     */
    // TODO: Rename and change types and number of parameters
    public static MyRequestsFragment newInstance(String param1, String param2) {
        MyRequestsFragment fragment = new MyRequestsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_my_requests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        availableRecyclerView = view.findViewById(R.id.my_request_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        availableRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(view.getContext());
        availableRecyclerView.setLayoutManager(layoutManager);
        ArrayList<Book> myDataset = new ArrayList<Book>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference usersReference = db.collection("users2").document("eJl7kfYl5eRlNIs44Aqt");
        usersReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("READ_DATA", "DocumentSnapshot Data: " + document.getData());
                        HashMap<Object, String> bookIDs = (HashMap<Object, String>) document.get("my_books");

                        for (Map.Entry mapElement : bookIDs.entrySet()) {
                            String key = (String) mapElement.getKey();
                            String value = (String) mapElement.getValue();

                            if (value.equals("requested")) {
                                DocumentReference bookReference = db.collection("books").document(key);
                                bookReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document2 = task.getResult();
                                            if (document2.exists()) {
                                                Log.d("READ_BOOKS", "DocumentSnapshot data: " + document2.getData());
                                                myDataset.add(new Book(document2.get("book_title").toString(), document2.get("author").toString(), document2.get("isbn").toString(), document2.get("status").toString()));
                                                mAdapter.notifyDataSetChanged();

                                            } else {
                                                Log.d("READ_BOOKS", "No such document");
                                            }
                                        } else {
                                            Log.d("READ_BOOKS", "get failed with ", task.getException());
                                        }
                                    }
                                });

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


        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(myDataset);
        availableRecyclerView.setAdapter(mAdapter);
    }
}