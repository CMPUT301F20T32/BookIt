package com.example.bookit;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.ListFragment;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SearchFragment extends ListFragment {

    private SearchView searchView;

    private RecyclerView requestedRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<Book> myDataset = new ArrayList<Book>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_search, container, false);
        return v;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        searchView = view.findViewById(R.id.search_box);
        requestedRecyclerView = view.findViewById(R.id.my_search_list);

        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        requestedRecyclerView.setHasFixedSize(true);

        // Use a linear layout manager
        layoutManager = new LinearLayoutManager(view.getContext());
        requestedRecyclerView.setLayoutManager(layoutManager);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference allBookReference = db.collection("minh_books");

        // Set up the Search View
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Filter the database
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter the books into "requested" and "available"
                allBookReference
                        .whereIn("status", Arrays.asList("requested", "available"))
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                // Clear the dataSet
                                myDataset.clear();
                                for(QueryDocumentSnapshot doc: queryDocumentSnapshots)
                                {
                                    Log.d("READ_DATA", "DocumentSnapshot data: " + doc.getData());
                                    String bookTitle = (String) doc.getData().get("book_title");
                                    String author = (String) doc.getData().get("author");
                                    String isbn = (String) doc.getData().get("isbn");
                                    String status = (String) doc.getData().get("status");
                                    // filter with the keyword
                                    if (bookTitle.toLowerCase().contains(newText.toLowerCase()) ||
                                            author.toLowerCase().contains(newText.toLowerCase()) ||
                                            isbn.equals(newText.toLowerCase())){
                                        myDataset.add(new Book(bookTitle, author, isbn, status));
                                    }
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                return true;
            }
        });

        // TODO: NEED TO LOGIN FIRST FOR THIS ONE TO NOT CRASH
        /*
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUser.getEmail();
        */

        // Request functionality (Tap to a book to request one not accepted/ borrowed)
        mAdapter = new MyNewAdapter(myDataset, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Toast.makeText(getContext(), "Position " + position, Toast.LENGTH_SHORT).show();
            }
        });

        // Set up the adapter
        requestedRecyclerView.setAdapter(mAdapter);

    }
}