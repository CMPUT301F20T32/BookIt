/*
 *  Classname: EditBookFragment
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
import android.graphics.Color;
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
import androidx.appcompat.widget.Toolbar;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static android.app.Activity.RESULT_OK;
import static android.widget.Toast.LENGTH_SHORT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditBookFragment extends Fragment {

    final String TAG = "Edited";
    private EditText editTitle;
    private EditText editAuthor;
    private EditText editISBN;
    private EditText editComment;
    private Toolbar toolBar;
    private String bookID;
    private String docId;
    private FloatingActionButton scan;
    private String call;
    //private String status;
    private FirebaseUser currentUser;
    private String username;
    private String ownerEmail;
    private String borrower;
    private String borrowerEmail;
    private String isbn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bookID = getArguments().getString("bookID");
        isbn = getArguments().getString("isbn");
        call = getArguments().getString("CallFrom");


        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_book, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        CollectionReference colRef = db.collection("books");

        editTitle = view.findViewById(R.id.bookTitleEditText);
        editAuthor = view.findViewById(R.id.authorEditText);
        editISBN = view.findViewById(R.id.isbnEditText);
        editComment = view.findViewById(R.id.commentsEditText);
        toolBar = view.findViewById(R.id.toolBar);
        final FloatingActionButton saveButton = view.findViewById(R.id.saveChangesButton);
        final FloatingActionButton deleteButton = view.findViewById(R.id.deleteButton);
        scan = view.findViewById(R.id.scanButton);
        final FloatingActionButton locationButton = view.findViewById(R.id.locationButton);
        if (call != null) {
            scan.setEnabled(false);
            editTitle.setEnabled(false);
            editTitle.setClickable(false);
            editISBN.setEnabled(false);
            editISBN.setClickable(false);
            editAuthor.setEnabled(false);
            editAuthor.setClickable(false);
            editComment.setEnabled(false);
            editComment.setClickable(false);
            toolBar.setTitle("Exchange book");
            saveButton.setEnabled(false);
            deleteButton.setEnabled(false);
            saveButton.setVisibility(view.GONE);
            deleteButton.setVisibility(view.GONE);

            if (call.equals("AcceptedBorrower")) {
                DocumentReference docReference = db.collection("books").document(bookID);

                docReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            Log.d(TAG, "???" + document.getId() + " => " + document.getData());
                            docId = document.getId();
                            if (document.getData().get("isOwnerScan") != null && document.getData().get("ownerEmail") != null) {
                                if (document.getData().get("isOwnerScan").toString().equals("true")) {
                                    ownerEmail = document.getData().get("ownerEmail").toString();
                                    scan.setEnabled(true);
                                }
                                else{
                                    scan.setEnabled(false);
                                    Toast.makeText(getActivity(), "Please wait for the owner to scan first.", LENGTH_SHORT).show();
                                }
                            }//12312412481
                            else {
                                //setScan to false, add toast
                                scan.setEnabled(false);
                                Toast.makeText(getActivity(), "Please wait for the owner to scan first.", LENGTH_SHORT).show();

                            }

                        } else {
                            scan.setEnabled(false);
                            Toast.makeText(getActivity(), "Please wait for the owner to scan first.", LENGTH_SHORT).show();
                        }
                    }
                });

            } else if (call.equals("AcceptedLender") || call.equals("BorrowedBorrower")){
                //lender
                scan.setEnabled(true);
            } else if (call.equals("BorrowedLender")) {
                DocumentReference documentReference = db.collection("books").document(bookID);

                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            Log.d(TAG, "???" + doc.getId() + " => " + doc.getData());
                            docId = doc.getId();
                            if (doc.getData().get("isOwnerScan") != null && doc.getData().get("borrower") != null) {
                                if (doc.getData().get("isOwnerScan").toString().equals("true")) {
                                    borrower = doc.getData().get("borrower").toString();
                                    scan.setEnabled(true);
                                }
                                else{
                                    scan.setEnabled(false);
                                    Toast.makeText(getActivity(), "Please wait for the borrower to scan first.", LENGTH_SHORT).show();
                                }
                            }//12312412481
                            else {
                                //setScan to false, add toast
                                scan.setEnabled(false);
                                Toast.makeText(getActivity(), "Please wait for the borrower to scan first.", LENGTH_SHORT).show();

                            }

                        } else {
                            scan.setEnabled(false);
                            Toast.makeText(getActivity(), "Please wait for the borrower to scan first.", LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } else {
            //set title bar title
//            editTitle.setEditableFactory();
            scan.setVisibility(view.GONE);
            scan.setEnabled(false);
            locationButton.setVisibility(view.GONE);
            locationButton.setEnabled(false);
        }
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ScanBookActivity.class);
                intent.putExtra("CallFrom", call);
                startActivityForResult(intent, 1);
            }
        });

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open activity to view exchange location
                Intent intent = new Intent(getContext(), LocationActivity.class);
                intent.putExtra("bookID", bookID);
                intent.putExtra("type", 2);
                startActivity(intent);
            }
        });

        //currentUser = FirebaseAuth.getInstance().getCurrentUser();

        db.collection("books")
                .document(bookID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            docId = document.getId();
                            if (document.get("book_title") != null) {
                                editTitle.setText(document.get("book_title").toString());
                            } else {
                                editTitle.setText("");
                            }
                            if (document.get("author") != null) {
                                editAuthor.setText(document.get("author").toString());
                            } else {
                                editAuthor.setText("");
                            }
                            if (document.get("isbn") != null) {
                                editISBN.setText(document.get("isbn").toString());
                            } else {
                                editISBN.setText("");
                            }
                            if (document.get("comment") != null) {
                                editComment.setText(document.get("comment").toString());
                            } else {
                                editComment.setText("");
                            }
                            //status = document.getData().get("status").toString();

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTitle.getText().toString().isEmpty() || editAuthor.getText().toString().isEmpty() ||
                        editISBN.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "One or more fields are empty.", LENGTH_SHORT).show();
                    return;
                }

                HashMap<String, Object> editedInfo = new HashMap<>();
                editedInfo.put("book_title", editTitle.getText().toString());
                editedInfo.put("author", editAuthor.getText().toString());
                editedInfo.put("isbn", editISBN.getText().toString());
                editedInfo.put("comment", editComment.getText().toString());
                DocumentReference docRef = db.collection("books").document(docId);
                docRef.update(editedInfo);

                editTitle.setText("");
                editAuthor.setText("");
                editISBN.setText("");
                editComment.setText("");

                getActivity().finish();
            }

        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference docRef = db.collection("books").document(docId);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                if (document.get("status").toString().equals("accepted") ||
                                        document.get("status").toString().equals("borrowed")) {
                                    Toast.makeText(getActivity(), "Cannot delete this book because of its status.", LENGTH_SHORT).show();
                                    return;
                                } else {
                                    docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("Deleted", "DocumentSnapshot successfully deleted!");
                                        }
                                    })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w("Failed", "Error deleting document!");
                                                }
                                            });

                                    //delete book from my_books field in users2
                                    DocumentReference docRef2 = db.collection("users2").document(currentUser.getEmail());
                                    HashMap<String, Object> deleteField = new HashMap<>();
                                    deleteField.put("my_books." + docId, FieldValue.delete());
                                    docRef2.update(deleteField).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("Deleted", "DocumentSnapshot successfully deleted!");
                                        }
                                    })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w("Failed", "Error deleting document!");
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

                getActivity().finish();
            }
        });

        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (call.equals("AcceptedBorrower")) {
                    DocumentReference docRefUsername = db.collection("users2").document(currentUser.getEmail());
                    docRefUsername.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentUserInfo = task.getResult();
                                if (documentUserInfo.exists()) {
                                    username = documentUserInfo.getString("user_info.username");
                                    HashMap<String, Object> editedInfo = new HashMap<>();
                                    editedInfo.put("borrower", username);
                                    editedInfo.put("status", "borrowed");
                                    editedInfo.put("isOwnerScan", false);
                                    DocumentReference docRef = db.collection("books").document(docId);
                                    docRef.update(editedInfo);
                                    Toast.makeText(getActivity(), "Information recorded, thank you.", LENGTH_SHORT).show();
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    db.collection("users2").document(currentUser.getEmail())
                                            .update("borrowed_books", FieldValue.arrayUnion(docId));
                                    db.collection("users2").document(currentUser.getEmail())
                                            .update("accepted_books", FieldValue.arrayRemove(docId));
                                    db.collection("users2").document(ownerEmail).update("my_books." + docId, "borrowed");


                                }
                            }
                        }
                    });
                } else if (call.equals("AcceptedLender") || call.equals("BorrowedBorrower")){//9780735213678
                    HashMap<String, Object> editedInfo = new HashMap<>();
                    editedInfo.put("isOwnerScan", true);
                    DocumentReference docRef = db.collection("books").document(docId);
                    docRef.update(editedInfo);
                    Toast.makeText(getActivity(), "Scan recorded, thank you.", LENGTH_SHORT).show();
                } else if (call.equals("BorrowedLender")) {
                    CollectionReference colRefBorrower = db.collection("users2");
                    colRefBorrower.whereEqualTo("user_info.username", borrower).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d("User", document.getId() + " => " + document.getData());
                                            borrowerEmail = document.getId();
                                        }
                                    }
                                }
                            });
                    DocumentReference docRefOwner = db.collection("users2").document(currentUser.getEmail());
                    docRefOwner.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot docOwnerInfo = task.getResult();
                                if (docOwnerInfo.exists()) {
                                    HashMap<String, Object> editedInfo = new HashMap<>();
                                    editedInfo.put("borrower", "");
                                    editedInfo.put("status", "available");
                                    editedInfo.put("isOwnerScan", false);
                                    db.collection("books").document(docId).update(editedInfo);
                                    Toast.makeText(getActivity(), "Information recorded, thank you.", LENGTH_SHORT).show();
                                    db.collection("users2").document(borrowerEmail)
                                            .update("borrowed_books", FieldValue.arrayRemove(docId));
                                    db.collection("users2").document(currentUser.getEmail())
                                            .update("my_books." + docId, "available");
                                }
                            }
                        }
                    });
                }
            } else {
                Toast.makeText(getActivity(), "Scan failed, please try again.", LENGTH_SHORT).show();

            }
        }
    }
}