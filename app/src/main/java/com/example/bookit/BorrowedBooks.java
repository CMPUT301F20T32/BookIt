/*
 *  Classname: BorrowedBooks
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
import android.widget.Toast;

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

import static android.widget.Toast.LENGTH_SHORT;

/**
 * A simple {@link Fragment} subclass.
 * <p>
 * This Fragment shows the Books of the Owner, which they have lent or have been borrowed
 * by other users.
 */
public class BorrowedBooks extends Fragment {

    Activity context;
    private Book longClickedBook;
    private RecyclerView.Adapter mAdapter;

    public BorrowedBooks() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_borrowed_books, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Toast.makeText(getActivity(), "Long press to show borrower information", LENGTH_SHORT).show();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        RecyclerView borrowedRecyclerView = view.findViewById(R.id.borrowed_recycler_view);

        /*
         * use this setting to improve performance if you know that changes
         * in content do not change the layout size of the RecyclerView
         */
        borrowedRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        borrowedRecyclerView.setLayoutManager(layoutManager);
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

                                if (value.equals("borrowed")) {
                                    DocumentReference docRef2 = db.collection("books").document(key);
                                    docRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document2 = task.getResult();
                                                if (document2.exists()) {
                                                    Log.d("READ_BOOKS", "DocumentSnapshot data: " + document2.getData());
                                                    myDataset.add(new Book(document2.get("book_title").toString(), document2.get("author").toString(), document2.get("isbn").toString(), document2.get("status").toString(), document2.get("borrower").toString(), document2.getId()));
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


        // specify an adapter
        mAdapter = new MyNewAdapter(myDataset, "borrower", new RecyclerViewClickListener() {
            /**
             * This method is used to represent the onClick action when a user clicks on a book
             * The flow of this method is as follows:
             * <ul>
             *     <li> It sets clickedBook to be the clicked request.
             *     <li> It creates a Toast message of the clicked book.
             * </ul>
             * @param view: view that responds to the Sign Up button being pressed.
             * @param position: int position of the clicked request in myDataSet
             */
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(context, EditDeleteActivity.class);
                intent.putExtra("bookID", myDataset.get(position).getBookID());
                intent.putExtra("CallFrom","BorrowedLender");
                startActivity(intent);
            }

            /**
             * This method is used to represent the onLongClick action when a user clicks on a book
             * The flow of this method is as follows:
             * <ul>
             *     <li> It sets longClickedBook to be the long clicked book.
             *     <li> It starts an activity for the long clicked book.
             * </ul>
             * @param view: view that responds to the Sign Up button being pressed.
             * @param position: int position of the clicked request in myDataSet
             */
            @Override
            public boolean onLongClick(View view, int position) {
                longClickedBook = myDataset.get(position);
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("user", longClickedBook.getBorrower());
                startActivity(intent);
                return true;
            }
        });
        borrowedRecyclerView.setAdapter(mAdapter);

    }
}