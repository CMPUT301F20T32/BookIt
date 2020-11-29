/*
 *  Classname: ManageRequestsFragment
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
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;


/**
 * EditProfileFragment refers to the edit My Profile functionality of the application.
 * The flow of the fragment is as follows:
 * <ul>
 *     <li> The profile fields are displayed</li>
 *     <li> If the user taps the edit button, the profile fields are validated</li>
 *     <li> Upon validation, the fields are updated in Firestore </li>
 *     <li> The fragment navigates to myProfileFragment</li>
 * </ul>
 *
 * @author Alisha Crasta
 * @author Nhat Minh Luu
 * @version 1.0
 * @since 1.0
 */
public class ManageRequestsFragment extends Fragment {

    Activity context;
    private RecyclerView manageRequestRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Book clickedBook;
    private Book longClickedBook;
    private String clickedBookTitle;
    private Button acceptButton, declineButton;
    private DocumentReference ownerRef, bookRef;
    private ArrayList<String> requesters;
    private int currentPos = -1;
    private int lastPos = -1;
    private View lastView;

    public ManageRequestsFragment() {
        // Required empty public constructor
    }

    /**
     * This method is called to do initial creation of a fragment
     * It inflates the layout of the fragment
     *
     * @param savedInstanceState refers to the cached state of the UI.
     * @param inflater:          The LayoutInflater object that can be used to inflate any views in the fragment
     * @param container:         If non-null, this is the parent view that the fragment's UI should be attached to.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_requests, container, false);
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
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Toast.makeText(getActivity(), "Long press to show requester information", LENGTH_SHORT).show();

        acceptButton = view.findViewById(R.id.accept_request_button);
        declineButton = view.findViewById(R.id.decline_request_button);
        manageRequestRecyclerView = view.findViewById(R.id.manage_request_recycler);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        manageRequestRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(view.getContext());
        manageRequestRecyclerView.setLayoutManager(layoutManager);
        ArrayList<Book> myDataset = new ArrayList<Book>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        ownerRef = db.collection("users2").document(currentUser.getEmail());
        ownerRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("READ_DATA", "DocumentSnapshot Data: " + document.getData());
                        HashMap<Object, String> bookIDs = (HashMap<Object, String>) document.get("my_books");
                        if (!bookIDs.entrySet().isEmpty()) {
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
                                                    Log.d("READ_BOOKS", "DocumentSnapshot data: " + document2.getData());

                                                    String bookID = document2.getId();

                                                    List<String> requesters = (List<String>) document2.get("requesters");
                                                    for (int i = 0; i < requesters.size(); i++) {
                                                        myDataset.add(new Book(bookID, requesters.get(i), document2.get("owner").toString()));
                                                    }

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
                        }
                    } else {
                        Log.d("READ_DATA", "No such document");
                    }
                } else {
                    Log.d("READ_DATA", "get failed with", task.getException());
                }
            }
        });


        mAdapter = new ManageRequestsAdapter(myDataset, new RecyclerViewClickListener() {
            /**
             * This method is used to represent the onClick action when a user clicks on a request
             * The flow of this method is as follows:
             * <ul>
             *     <li> It sets clickedBook to be the clicked request.
             *     <li> It creates a Toast message of the clicked request.
             * </ul>
             * @param view: view that responds to the Sign Up button being pressed.
             * @param position: int position of the clicked request in myDataSet
             */
            @Override
            public void onClick(View view, int position) {
                if (position != lastPos) {
                    if (lastView != null) {
                        lastView.setBackgroundColor(Color.TRANSPARENT);
                    }
                    view.setBackgroundColor(Color.LTGRAY);
                }
                clickedBook = myDataset.get(position);
                bookRef = db.collection("books").document(clickedBook.getBookID());
                bookRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot bookDocument = task.getResult();
                            clickedBookTitle = bookDocument.get("book_title").toString();

                        }
                    }
                });
                lastView = view;
                lastPos = position;
            }

            /**
             * This method is used to represent the onClick action when a user clicks on a request
             * The flow of this method is as follows:
             * <ul>
             *     <li> It sets longClickedBook to be the long clicked request.
             *     <li> It starts an activity for the long clicked request.
             * </ul>
             * @param view: view that responds to the Sign Up button being pressed.
             * @param position: int position of the clicked request in myDataSet
             */
            @Override
            public boolean onLongClick(View view, int position) {
                longClickedBook = myDataset.get(position);
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("user", longClickedBook.getRequester());
                startActivity(intent);
                return false;
            }
        });


        //on click listener for the decline button
        declineButton.setOnClickListener(new View.OnClickListener() {
            /**
             * This method is used to represent the onClick action of the decline button
             * The flow of this method is as follows:
             * <ul>
             *     <li> The method queries Firebase to get the bookid
             *     <li> Then updates the book status and requesters in FireStore
             *     <ul>
             *         <li> If the declined request is the only request on the book, change status to available
             *         <li> delete the requester from book requesters
             *     </ul>
             * </ul>
             * @param v: view that responds to the decline button being pressed.
             */
            @Override
            public void onClick(View v) {
                if (lastView != null) {
                    lastView.setBackgroundColor(Color.TRANSPARENT);
                }

                lastView = null;
                lastPos = -1;
                if (clickedBook != null) {
                    Book book = clickedBook;
                    String bookID = clickedBook.getBookID();
                    String requester = clickedBook.getRequester();
                    clickedBook = null;

                    //update the book and owner doc
                    bookRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot doc = task.getResult();
                                if (doc.exists()) {
                                    Log.d("READ_DATA", "DocumentSnapshot Data: " + doc.getData());
                                    db.collection("books").document(doc.getId()).update("requesters", FieldValue.arrayRemove(requester));
                                    ArrayList<String> requesters = (ArrayList<String>) doc.get("requesters");
                                    if (requesters.size() == 1) {
                                        db.collection("books").document(bookID).update("status", "available");
                                        ownerRef.update("my_books." + bookID, "available");
                                        myDataset.remove(book);
                                        mAdapter.notifyDataSetChanged();
                                        Toast.makeText(getContext(), "Request by: " + requester + " for: " + clickedBookTitle + " declined", Toast.LENGTH_SHORT).show();
                                    } else {
                                        myDataset.remove(book);
                                        mAdapter.notifyDataSetChanged();
                                        Toast.makeText(getContext(), "Request by: " + requester + " for: " + clickedBookTitle + " declined", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Log.d("READ_DATA", "No such document");
                                }
                            }
                        }
                    });
                    //update the requesters doc
                    db.collection("users2")
                            .whereEqualTo("user_info.username", requester).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                Log.d("QUERY_DATA", "searching for user doc: " + requester);
                                DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                                String requesterID = doc.getId();
                                db.collection("users2").document(requesterID).update("requested_books", FieldValue.arrayRemove(bookID));

                            }
                        }
                    });
                }

            }
        });


        /**
         * This method is used to represent the onClick action of the accept button
         * The flow of this method is as follows:
         * <ul>
         *     <li> The method queries Firebase to get the bookid
         *     <li> Then updates the book status and requesters in FireStore
         *     <ul>
         *         <li> Deletes all other requests
         *         <li> Changes book status to accepted
         *     </ul>
         * </ul>
         * @param v: view that responds to the accept button being pressed.
         */
        //on click listener for the accept button
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastView != null) {
                    lastView.setBackgroundColor(Color.TRANSPARENT);
                }

                lastView = null;
                lastPos = -1;
                if (clickedBook != null) {
                    Book book = clickedBook;
                    String bookID = clickedBook.getBookID();
                    String clickedBookRequester = clickedBook.getRequester();
                    myDataset.remove(clickedBook);
                    mAdapter.notifyDataSetChanged();
                    clickedBook = null;

                    //update the book and owner doc
                    bookRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot doc = task.getResult();
                                if (doc.exists()) {
                                    Log.d("READ_DATA", "DocumentSnapshot Data: " + doc.getData());
                                    ArrayList<String> emptyArray = new ArrayList<>();
                                    requesters = (ArrayList<String>) doc.get("requesters");
                                    db.collection("books").document(bookID).update("requesters", emptyArray);
                                    db.collection("books").document(bookID).update("borrower", clickedBookRequester);
                                    db.collection("books").document(bookID).update("status", "accepted");
                                    ownerRef.update("my_books." + bookID, "accepted");

                                    //update all the requesters docs
                                    for (String requester : requesters) {
                                        db.collection("users2")
                                                .whereEqualTo("user_info.username", requester).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                                                    if (doc.exists()) {

                                                        if (requester.equals(clickedBookRequester)) {
                                                            //accept the requesters request
                                                            String requesterID = doc.getId();
                                                            db.collection("users2").document(requesterID).update("requested_books", FieldValue.arrayRemove(bookID));
                                                            db.collection("users2").document(requesterID).update("accepted_books", FieldValue.arrayUnion(bookID));
                                                        } else {

                                                            //delete the other requests
                                                            String requesterID = doc.getId();
                                                            db.collection("users2").document(requesterID).update("requested_books", FieldValue.arrayRemove(bookID));
                                                        }
                                                    }
                                                }
                                            }
                                        });
                                    }

                                    for (int i = 0; i < myDataset.size(); i++) {
                                        Book book = myDataset.get(i);
                                        if (bookID.equals(book.getBookID())) {
                                            myDataset.remove(book);
                                            mAdapter.notifyDataSetChanged();
                                        }
                                    }

                                    Toast.makeText(getContext(), "Request by: " + clickedBookRequester + " for: " + clickedBookTitle + " accepted ", Toast.LENGTH_SHORT).show();
                                    //open activity to set location for dropoff
                                    Intent intent = new Intent(getContext(), LocationActivity.class);
                                    intent.putExtra("bookID", bookID);
                                    intent.putExtra("type", 1);
                                    startActivity(intent);

                                } else {
                                    Log.d("READ_DATA", "No such document");
                                }
                            }
                        }
                    });


                }
            }
        });

        // Set up the adapter
        manageRequestRecyclerView.setAdapter(mAdapter);
    }

}