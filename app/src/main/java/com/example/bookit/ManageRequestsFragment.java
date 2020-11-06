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

import android.graphics.Color;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.firebase.firestore.FieldValue.arrayRemove;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ManageRequestsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManageRequestsFragment extends Fragment {

    private RecyclerView manageRequestRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Book clickedBook;
    private Button acceptButton, declineButton;
    private String bookID;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ManageRequestsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ManageRequestsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ManageRequestsFragment newInstance(String param1, String param2) {
        ManageRequestsFragment fragment = new ManageRequestsFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_requests, container, false);
    }

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

        DocumentReference docRef = db.collection("users2").document(currentUser.getEmail());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("READ_DATA", "DocumentSnapshot Data: " + document.getData());
                        HashMap<Object, String> bookIDs = (HashMap<Object, String>) document.get("my_books");
                        if(!bookIDs.entrySet().isEmpty()){
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

                                                    String bookTitle = document2.get("book_title").toString();

                                                    List<String> requesters = (List<String>) document2.get("requesters");
                                                    for (int i = 0; i < requesters.size(); i++) {
                                                        myDataset.add(new Book(bookTitle, requesters.get(i), document2.get("owner").toString()));
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

        //TODO highlight selected request instead of toast message
        mAdapter = new ManageRequestsAdapter(myDataset, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                clickedBook = myDataset.get(position);
                Toast.makeText(getContext(), "You have selected " + clickedBook.getBookTitle() + " requested by " + clickedBook.getRequester(),
                        Toast.LENGTH_SHORT).show();
            }
        });


    //on click listener for the decline button
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickedBook != null) {
                    //update the database
                    db.collection("books")
                            .whereEqualTo("book_title", clickedBook.getBookTitle())
                            .whereEqualTo("owner", clickedBook.getOwner())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                                        if (doc.exists()) {
                                            Log.d("READ_DATA", "DocumentSnapshot Data: " + doc.getData());
                                            bookID = doc.getId();
                                            db.collection("books").document(bookID).update("requesters", FieldValue.arrayRemove(clickedBook.getRequester()));
                                            ArrayList<String> requesters = (ArrayList<String>) doc.get("requesters");
                                            if (requesters.size() == 1) {
                                                if (requesters.get(0).equals(clickedBook.getRequester())) {
                                                    db.collection("books").document(bookID).update("status", "available");
                                                    docRef.update("my_books." + bookID, "available");
                                                    myDataset.remove(clickedBook);
                                                    mAdapter.notifyDataSetChanged();
                                                    Toast.makeText(getContext(), "Request by: " + clickedBook.getRequester() + "for: " + clickedBook.getBookTitle() + "declines", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                myDataset.remove(clickedBook);
                                                mAdapter.notifyDataSetChanged();
                                                Toast.makeText(getContext(), "Request by: " + clickedBook.getRequester() + " for: " + clickedBook.getBookTitle() + " declined", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Log.d("READ_DATA", "No such document");
                                        }
                                    } else {
                                        Log.d("QUERY_DATA", "query failed with ", task.getException());
                                    }
                                }
                            });
                }
            }
        });

        //on click listener for the accept button
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickedBook != null){
                    //update the database
                    db.collection("books")
                            .whereEqualTo("book_title", clickedBook.getBookTitle())
                            .whereEqualTo("owner", clickedBook.getOwner()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                                        if (doc.exists()) {
                                            Log.d("READ_DATA", "DocumentSnapshot Data: " + doc.getData());
                                            ArrayList<String> emptyArray = new ArrayList<>();
                                            bookID = doc.getId();
                                            db.collection("books").document(bookID).update("requesters", emptyArray).addOnCompleteListener( new OnCompleteListener<Void>(){
                                                @Override
                                                public  void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        db.collection("books").document(bookID).update("status", "accepted").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    docRef.update("my_books." + bookID, "accepted").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                myDataset.remove(clickedBook);
                                                                                for (Book book : myDataset) {
                                                                                    if (book.getBookTitle() == clickedBook.getBookTitle()) {
                                                                                        myDataset.remove(book);
                                                                                        mAdapter.notifyDataSetChanged();
                                                                                    }
                                                                                }
                                                                                mAdapter.notifyDataSetChanged();
                                                                                Toast.makeText(getContext(), "Request by: " + clickedBook.getRequester() + " for: " + clickedBook.getBookTitle() + " accepted ", Toast.LENGTH_SHORT).show();
                                                                            } else {
                                                                                Log.d("UPDATE_DATA", "update failed with ", task.getException());
                                                                            }
                                                                        }});
                                                                    mAdapter.notifyDataSetChanged();
                                                                } else {
                                                                    Log.d("UPDATE_DATA", "update failed with ", task.getException());
                                                                }
                                                            }});
                                                        mAdapter.notifyDataSetChanged();
                                                    } else {
                                                        Log.d("UPDATE_DATA", "update failed with ", task.getException());
                                                    }
                                                }});
                                        } else { Log.d("QUERY_DATA", "No such document"); }
                                    } else { Log.d("QUERY_DATA", "query failed with ", task.getException()); }
                                    mAdapter.notifyDataSetChanged();
                                }});
                }
            }
            });

        // Set up the adapter
        manageRequestRecyclerView.setAdapter(mAdapter);
    }

}