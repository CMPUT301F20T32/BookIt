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
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static android.widget.Toast.LENGTH_SHORT;

import java.util.HashMap;

public class EditBookFragment extends Fragment {

    final String TAG = "Edited";
    private EditText editTitle;
    private EditText editAuthor;
    private EditText editISBN;
    private EditText editComment;
    private Toolbar toolBar;
    private String isbnkey;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        isbnkey = getArguments().getString("bookID");
        if (isbnkey != null) {
            Toast.makeText(getContext(), "Data received!", LENGTH_SHORT).show();
        }
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

        editTitle = view.findViewById(R.id.bookTitleEditText);
        editAuthor = view.findViewById(R.id.authorEditText);
        editISBN = view.findViewById(R.id.isbnEditText);
        editComment = view.findViewById(R.id.commentsEditText);
        toolBar = view.findViewById(R.id.toolBar);
        final FloatingActionButton saveButton = view.findViewById(R.id.saveChangesButton);
        final FloatingActionButton deleteButton = view.findViewById(R.id.deleteButton);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference colRef = db.collection("bookTest");

        db.collection("bookTest")
                .whereEqualTo("isbn", isbnkey)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                if(document.getData().get("book_title") != null) {
                                    editTitle.setText(document.getData().get("book_title").toString());
                                } else {
                                    editTitle.setText(""); }
                                if(document.getData().get("author") != null) {
                                    editAuthor.setText(document.getData().get("author").toString());
                                } else {
                                    editAuthor.setText(""); }
                                if(document.getData().get("isbn") != null) {
                                    editISBN.setText(document.getData().get("isbn").toString());
                                } else {
                                    editISBN.setText(""); }
                                if(document.getData().get("comment") != null) {
                                    editComment.setText(document.getData().get("comment").toString());
                                } else {
                                    editComment.setText(""); }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTitle.getText().toString().isEmpty() || editAuthor.getText().toString().isEmpty() ||
                        editISBN.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), "One or more fields are empty.", LENGTH_SHORT).show();
                    return;
                }

                HashMap<String, Object> editedInfo =  new HashMap<>();
                editedInfo.put("book_title", editTitle.getText().toString());
                editedInfo.put("author", editAuthor.getText().toString());
                editedInfo.put("isbn", editISBN.getText().toString());
                editedInfo.put("comment", editComment.getText().toString());
                DocumentReference docRef = db.collection("bookTest").document(isbnkey);
                docRef.update(editedInfo);

                editTitle.setText("");
                editAuthor.setText("");
                editISBN.setText("");
                editComment.setText("");

                getFragmentManager().popBackStack();

                getActivity().finish();
            }

        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colRef.document(isbnkey)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                Toast.makeText(getContext(), "Book deleted!", LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error deleting document", e);
                                Toast.makeText(getContext(), "Failed to delete!", LENGTH_SHORT).show();
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
}