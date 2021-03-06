/*
 *  Classname: RequestedBooks
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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * <p>
 * This Fragment is used to show the books that an owner owns which have been requested
 */
public class RequestedBooks extends Fragment {

    Activity context;

    private RecyclerView.Adapter mAdapter;

    public RequestedBooks() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_requested_books, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        RecyclerView requestedRecyclerView = view.findViewById(R.id.requested_recycler_view);

        /*
         * use this setting to improve performance if you know that changes
         * in content do not change the layout size of the RecyclerView
         */
        requestedRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        requestedRecyclerView.setLayoutManager(layoutManager);

        ArrayList<Book> myDataset = new ArrayList<Book>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (mAuth.getCurrentUser() != null) {
            String userEmail = mAuth.getCurrentUser().getEmail();
            DocumentReference docRef = db.collection("users2").document(userEmail);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                                    DocumentReference docRef2 = db.collection("books").document(key);
                                    docRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document2 = task.getResult();
                                                if (document2.exists()) {
                                                    String link= "";
                                                    if (document2.get("image_link") != null) {
                                                        link = document2.get("image_link").toString();
                                                    }
                                                    Log.d("READ_BOOKS", "DocumentSnapshot data: " + document2.getData());
                                                    myDataset.add(new Book(document2.get("book_title").toString(), document2.get("author").toString(), document2.get("isbn").toString(), document2.get("status").toString(), "", document2.getId(), link));
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

        }


        // specify an adapter (see also next example)
        mAdapter = new MyNewAdapter(myDataset, "borrower", new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(context, EditDeleteActivity.class);
                intent.putExtra("bookID", myDataset.get(position).getBookID());
                startActivity(intent);
            }

            @Override
            public boolean onLongClick(View view, int position) {
                return false;
            }
        });
        requestedRecyclerView.setAdapter(mAdapter);

    }
}