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

/**
 * AddBookFragment refers to the adding a book functionality of the application.
 * The flow of the fragment is as follows:
 * <ul>
 *     <li> The user is prompted with a screen to add a book with two options.
 *     <li> The user can either scan the book or add in the details manually.
 *     <li> Scanning a book is done by {@link ScanBookActivity}.
 *     <li> On pressing the add button, the book is added to Firestore, and is displayed.
 * </ul>
 *
 * @author Sutanshu Seth, Phi Long Lai
 * @version 1.0
 * @since 1.0
 */
public class AddBookFragment extends Fragment {
    private EditText bookTitleEditText;        //Edittext object for the book title
    private EditText authorEditText;           //Edittext object for the book author
    private EditText ISBNEditText;             //Edittext object for the book isbn
    private EditText commentEditText;          //Edittext object for the book comments
    private FloatingActionButton scan;         //FloatingActionButton object to scan a book
    private String owner;                      //String to store the owner of the book
    private ArrayList<String> requesters = new ArrayList<>();       //ArrayList to store the requesters
    private String borrower = "N/A";           //String to store the borrower, N/A if empty
    private String latitude = "";              //String to store latitude of the location of the book
    private String longitude = "";             //String to store longitude of the location of the book
    final String status = "available";         //String to store the status of the book
    private FirebaseUser currentUser;          //FirebaseUser object to store the current user


    /**
     * This method is called to do initial creation of a fragment
     * It inflates the layout of the fragment
     *
     * @param savedInstanceState refers to the cached state of the UI.
     * @param inflater:          The LayoutInflater object that can be used to inflate any views in the fragment
     * @param container:         If non-null, this is the parent view that the fragment's UI should be attached to.
     */
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

    /**
     * This method does the following:
     * <ol>
     *     <li> graphical initializations of the fragment elements </li>
     *     <li> queries FireStore for requests on the users books </li>
     *     <li> specifies an adapter for the array of book requests, myDataSet </li>
     *     <li> contains listeners for the decline and accept buttons </li>
     * </ol>
     *
     * @param savedInstanceState refers to the cached state of the UI.
     * @param view:              The View returned by OnCreateView
     */
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
        RecyclerView.Adapter mAdapter = new MyNewAdapter(myDataset, "borrower", new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
            }

            @Override
            public boolean onLongClick(View view, int position) {
                return false;
            }
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (currentUser != null) {
            final DocumentReference docRef = db.collection("users2").document(currentUser.getEmail());
            final CollectionReference colRef = db.collection("books");

            //Action handled when the add button is clicked
            addBookButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String bookTitle = bookTitleEditText.getText().toString();
                    final String author = authorEditText.getText().toString();
                    final String ISBN = ISBNEditText.getText().toString();
                    final String comment = commentEditText.getText().toString();
                    //validate the input info of the book
                    if (bookTitle.length() == 0 || author.length() == 0 || ISBN.length() == 0) {

                    } else {
                        HashMap<String, Object> data = new HashMap<>();

                        // Get the username of the current user
                        final Boolean ownerScan = false;
                        DocumentReference docRefUsername = db.collection("users2").document(currentUser.getEmail());
                        docRefUsername.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot documentUserInfo = task.getResult();
                                    if (documentUserInfo.exists()) {
                                        String username = documentUserInfo.getString("user_info.username");
                                        Log.d("BOOK_DATA", "Username: " + username);

                                        // Add the book data to Firestore
                                        data.put("isOwnerScan",ownerScan);
                                        data.put("book_title", bookTitle);
                                        data.put("author", author);
                                        data.put("isbn", ISBN);
                                        data.put("status", status);
                                        data.put("comment", comment);
                                        data.put("requesters", requesters);
                                        data.put("borrower", borrower);
                                        data.put("owner", username);
                                        data.put("ownerEmail",currentUser.getEmail());
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

                                        //set fields to empty again
                                        bookTitleEditText.setText("");
                                        authorEditText.setText("");
                                        ISBNEditText.setText("");
                                        commentEditText.setText("");

                                        Toast.makeText(getContext(), "Book is successfully added.", Toast.LENGTH_SHORT).show();

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

    /**
     * This function refers to the result of scanning an isbn code of a book.
     * In the event that the scan worked fine, the editText objects are set
     * with the text related to the book's information. This is then pushed
     * to Firestore later after the "add" button is pressed.
     * @param requestCode
     * @param resultCode
     * @param data
     */
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