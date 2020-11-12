/*
 *  Classname: BorrowedBookFragment
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

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
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

public class BorrowedFragment extends Fragment {
    private RecyclerView borrowedBorrowerRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseAuth mAuth;
    private String userEmail;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_borrowed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        boolean flag = false;
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            userEmail = mAuth.getCurrentUser().getEmail();
        }
        else {
            flag = true;
        }

        borrowedBorrowerRecyclerView = view.findViewById(R.id.borrowed_borrower_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        borrowedBorrowerRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(view.getContext());
        borrowedBorrowerRecyclerView.setLayoutManager(layoutManager);
        ArrayList<Book> myDataset = new ArrayList<Book>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (!flag){
            DocumentReference docRef = db.collection("users2").document(userEmail);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("READ_DATA", "DocumentSnapshot Data: " + document.getData());
                            ArrayList<String> bookIDs = (ArrayList<String>) document.get("borrowed_books");

                            for (String bookID : bookIDs) {
                                DocumentReference docRef2 = db.collection("books").document(bookID);
                                docRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document2 = task.getResult();
                                            if (document2.exists()) {
                                                Log.d("READ_BOOKS", "DocumentSnapshot data: " + document2.getData());
                                                myDataset.add(new Book(document2.get("book_title").toString(), document2.get("author").toString(), document2.get("isbn").toString(), document2.get("status").toString(), document2.get("owner").toString()));
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
        mAdapter = new MyAdapter(myDataset);
        borrowedBorrowerRecyclerView.setAdapter(mAdapter);

    }

}