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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class AddBookFragment extends Fragment {
    private EditText bookTitleEditText;
    private EditText authorEditText;
    private EditText ISBNEditText;
    private EditText commentEditText;
    private FloatingActionButton scan;
    private String owner;
    private ArrayList<String> requesters = new ArrayList<>();
    private String borrower = "N/A";
    private String latitude = "";
    private String longitude = "";
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
        scan = view.findViewById(R.id.scanButton);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ScanBookActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        final FloatingActionButton addBookButton = view.findViewById(R.id.addButton);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();


        ArrayList<Book> myDataset = new ArrayList<Book>();
        RecyclerView.Adapter mAdapter = new MyNewAdapter(myDataset, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
            }
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (currentUser != null) {
            final DocumentReference docRef = db.collection("users2").document(currentUser.getEmail());
            final CollectionReference colRef = db.collection("books");
            owner = currentUser.getEmail();

            addBookButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String bookTitle = bookTitleEditText.getText().toString();
                    final String author = authorEditText.getText().toString();
                    final String ISBN = ISBNEditText.getText().toString();
                    final String comment = commentEditText.getText().toString();
                    if (bookTitle.length() == 0 || author.length() == 0 || ISBN.length() == 0) {

                    } else {
                        HashMap<String, Object> data = new HashMap<>();

                        // Get the username of the current user
                        DocumentReference docRefUsername = db.collection("users2").document(owner);
                        docRefUsername.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot documentUserInfo = task.getResult();
                                    if (documentUserInfo.exists()) {
                                        String username = documentUserInfo.getString("user_info.username");
                                        Log.d("BOOK_DATA", "Username: " + username);

                                        // Add the book data to Firestore
                                        data.put("book_title", bookTitle);
                                        data.put("author", author);
                                        data.put("isbn", ISBN);
                                        data.put("status", status);
                                        data.put("comment", comment);
                                        data.put("requesters", requesters);
                                        data.put("borrower", borrower);
                                        data.put("owner", username);
                                        data.put("latitude", latitude);
                                        data.put("longitude", longitude);

                                        colRef.add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Log.d("Added", "DocumentSnapshot written with ID: " + documentReference.getId());
                                                docRef.update("my_books." + documentReference.getId(), status);

                                            }
                                        })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w("Failed", "Error adding document", e);
                                                    }
                                                });

                                        bookTitleEditText.setText("");
                                        authorEditText.setText("");
                                        ISBNEditText.setText("");
                                        commentEditText.setText("");

                                    }

                                }
                            }
                        });

                    }
                }
            });
        } else {
            Toast.makeText(getContext(), "Need to login first.", Toast.LENGTH_SHORT).show();
            bookTitleEditText.setText("");
            authorEditText.setText("");
            ISBNEditText.setText("");
            commentEditText.setText("");
        }

    }

    //9780199536962
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                bookTitleEditText.setText(data.getStringExtra("title"));
                String author = data.getStringExtra("authors");
                if (author.charAt(0) == '[') {
                    author = author.substring(2, author.length() - 2);
                    author = author.replace("\"", "");
                }
                authorEditText.setText(author);
                ISBNEditText.setText(data.getStringExtra("isbn"));
            } else {
                bookTitleEditText.setText("");
                authorEditText.setText("");
                ISBNEditText.setText("");
            }
        }
    }
}