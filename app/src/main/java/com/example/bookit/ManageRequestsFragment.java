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
    private String clickedBookTitle;
    private Button acceptButton, declineButton;
    private DocumentReference ownerRef, bookRef;
    private ArrayList<String> requesters;

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
                                                        //myDataset.add(new Book(bookID, requesters.get(i), document2.get("owner").toString()));
                                                        myDataset.add(new Book((String) document2.getData().get("book_title"), (String) document2.getData().get("author"), (String) document2.getData().get("isbn"), (String) document2.getData().get("status"), requesters.get(i), bookID));
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
                clickedBook = myDataset.get(position);
                bookRef = db.collection("books").document(clickedBook.getBookID());
                bookRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot bookDocument = task.getResult();
                            clickedBookTitle = bookDocument.get("book_title").toString();
                            Toast.makeText(getContext(), "You have selected " + clickedBookTitle + " requested by " + clickedBook.getBorrower(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                Intent intent = new Intent(getContext(), BookInfoActivity.class);

                // In this case getBorrower() actually returns the requester
                intent.putExtra("requesterUsername", myDataset.get(position).getBorrower());

                intent.putExtra("bookId", myDataset.get(position).getBookID());
                intent.putExtra("bookName", myDataset.get(position).getBookTitle());
                intent.putExtra("status", myDataset.get(position).getStatus());
                intent.putExtra("isbn", myDataset.get(position).getISBN());
                intent.putExtra("bookID", myDataset.get(position).getBookID());
                intent.putExtra("manageRequests", "true");
                intent.putExtra("currentUSer", currentUser.getEmail());

                startActivity(intent);
            }
        });

        // Set up the adapter
        manageRequestRecyclerView.setAdapter(mAdapter);
    }

}