package com.example.bookit;

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

/**
 * A simple {@link Fragment} subclass.
 * <p>
 * This Fragment shows the books that the borrower has requested and have not been accepted or declined
 */
public class PendingRequestsFragment extends Fragment {

    private RecyclerView.Adapter mAdapter;

    public PendingRequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pending_requests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        RecyclerView myRequestsBorrowedRecyclerView = view.findViewById(R.id.pending_requests_borrower_recycler_view);

        /*
         * use this setting to improve performance if you know that changes
         * in content do not change the layout size of the RecyclerView
         */
        myRequestsBorrowedRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        myRequestsBorrowedRecyclerView.setLayoutManager(layoutManager);
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
                            ArrayList<String> bookIDs = (ArrayList<String>) document.get("requested_books");

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
        myRequestsBorrowedRecyclerView.setAdapter(mAdapter);

    }
}