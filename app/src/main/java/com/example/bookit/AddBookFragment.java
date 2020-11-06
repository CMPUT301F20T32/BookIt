/*
 *  Classname: AddBookFragment
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
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class AddBookFragment extends Fragment {
    final String TAG = "Added";
    private EditText bookTitleEditText;
    private EditText authorEditText;
    private EditText ISBNEditText;
    private EditText commentEditText;
    private String owner;
    private ArrayList<String> requesters;
    private String borrower="N/A";
    final String status = "available";
    FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_book, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bookTitleEditText = view.findViewById(R.id.editTextBookTitle);
        authorEditText = view.findViewById(R.id.editTextAuthor);
        ISBNEditText = view.findViewById(R.id.editTextISBN);
        commentEditText = view.findViewById(R.id.editTextComments);
        final FloatingActionButton addBookButton = view.findViewById(R.id.addButton);

        requesters = new ArrayList<>();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        ArrayList<Book> myDataset = new ArrayList<Book>();
        RecyclerView.Adapter mAdapter = new MyAdapter(myDataset);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("books");

        if (currentUser != null) {
            owner = currentUser.toString();

            addBookButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String bookTitle = bookTitleEditText.getText().toString();
                    final String author = authorEditText.getText().toString();
                    final String ISBN = ISBNEditText.getText().toString();
                    final String comment = commentEditText.getText().toString();
                    HashMap<String, Object> data = new HashMap<>();

                    if (bookTitle.length()>0 && author.length()>0 && ISBN.length()>0) {
                        data.put("book_title", bookTitle);
                        data.put("author", author);
                        data.put("isbn", ISBN);
                        data.put("status", status);
                        data.put("comment", comment);

                        collectionReference
                                .document(ISBN)
                                .set(data)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "Data has been added successfully!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "Data could not be added!" + e.toString());
                                    }
                                });
                        bookTitleEditText.setText("");
                        authorEditText.setText("");
                        ISBNEditText.setText("");
                        commentEditText.setText("");
                    }
                }
            });

            collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                    myDataset.clear();
                    for (QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                        Log.d(TAG, String.valueOf(doc.getData().get("book_title")));
                        Log.d(TAG, String.valueOf(doc.getData().get("author")));
                        String ISBN = doc.getId();
                        String bookTitle = (String) doc.getData().get("book_title");
                        String author = (String) doc.getData().get("author");
                        myDataset.add(new Book(bookTitle, author, ISBN, status));
                    }
                    mAdapter.notifyDataSetChanged();
                }
            });

        } else {
            Toast.makeText(getContext(), "Need to login first.", Toast.LENGTH_SHORT).show();
            bookTitleEditText.setText("");
            authorEditText.setText("");
            ISBNEditText.setText("");
            commentEditText.setText("");
        }

        //bookTitleEditText.setText("");
        //authorEditText.setText("");
        //ISBNEditText.setText("");
        //commentEditText.setText("");

    }
}