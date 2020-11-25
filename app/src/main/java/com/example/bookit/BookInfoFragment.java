package com.example.bookit;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static android.widget.Toast.LENGTH_SHORT;

/**
 * A simple {@link Fragment} subclass.
 * This Fragment displays the information related to Books and allows the owner
 * to edit the book, hand-in, receive books by scanning
 */
public class BookInfoFragment extends Fragment {

    private FirebaseUser currentUser;
    private String username;
    private String docId;

    public BookInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_book_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference colRef = db.collection("books");

        if (getArguments() != null) {

            TextView bookName = view.findViewById(R.id.book_name_text_view);
            TextView isbn = view.findViewById(R.id.isbn_text_view);
            TextView owner = view.findViewById(R.id.owner_text_view);
            TextView status = view.findViewById(R.id.status_text_view);
            ImageView requestBook = view.findViewById(R.id.request_book_image_view);
            ImageView viewProfile = view.findViewById(R.id.user_profile_image_view);
            FloatingActionButton editBook = view.findViewById(R.id.fab_edit_book);
            ImageView scanBook = view.findViewById(R.id.scan_image_view);

            // Display the book information
            bookName.setText(getArguments().getString("bookName"));
            isbn.setText(getArguments().getString("isbn"));
            owner.setText(getArguments().getString("ownerId"));
            status.setText(getArguments().getString("status"));

            // If the book is owned by the owner then show the button to edit the book information
            if (getArguments().getString("ownerBook") != null) {
                editBook.setVisibility(View.VISIBLE);
                requestBook.setVisibility(View.GONE);

                if (getArguments().get("ownerAcceptedBook") != null) {
                    scanBook.setVisibility(View.VISIBLE);

                    colRef.whereEqualTo("isbn", getArguments().getString("isbn")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //Log.d(TAG, document.getId() + " => " + document.getData());
                                    docId = document.getId();
                                    if (document.getData().get("isOwnerScan") != null) {
                                        if (document.getData().get("isOwnerScan").toString().equals("true")) {
                                            scanBook.setEnabled(true);
                                        }
                                    }//12312412481
                                    else {
                                        //setScan to false, add toast
                                        scanBook.setEnabled(false);
                                        Toast.makeText(getActivity(), "Please wait for the owner to scan first.", LENGTH_SHORT).show();

                                    }
                                }
                            } else {
                                scanBook.setEnabled(false);
                                Toast.makeText(getActivity(), "Please wait for the owner to scan first.", LENGTH_SHORT).show();
                                //setScan to false, add toast
                            }
                        }
                    });

                    scanBook.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), ScanBookActivity.class);
                            startActivityForResult(intent, 1);
                        }
                    });

                }


                editBook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), EditDeleteActivity.class);
                        intent.putExtra("bookID", getArguments().getString("isbn"));
                        startActivity(intent);
                    }
                });

            }


            requestBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle result = new Bundle();
                    result.putString("bookId", getArguments().getString("bookId"));
                    result.putString("userEmail", getArguments().getString("userEmail"));
                    result.putString("ownerId", getArguments().getString("ownerId"));
                    result.putString("bookName", getArguments().getString("bookName"));
                    result.putString("status", getArguments().getString("status"));
                    result.putString("userId", getArguments().getString("userId"));

                    // Open Dialog to confirm with the user to request the book
                    DialogFragment f = new RequestBookDialogFragment();
                    f.setTargetFragment(BookInfoFragment.this, 1);
                    f.getTargetFragment().setArguments(result);
                    f.show(getParentFragmentManager(), "RequestBookDialogFragment");
                }
            });

            // Show the profile of the user when this button is clicked
            viewProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), RetrieveInfoActivity.class);
                    intent.putExtra("user", getArguments().getString("ownerId"));
                    startActivity(intent);
                }
            });


        }


        Toolbar myProfileToolbar = view.findViewById(R.id.toolbar);
        //on click listener for back navigation
        myProfileToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (getArguments().get("ownerAcceptedBook") != null) {
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
                                    DocumentReference docRef = db.collection("books").document(docId);
                                    docRef.update(editedInfo);
                                    Toast.makeText(getActivity(), "Information recorded, thank you.", LENGTH_SHORT).show();
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    db.collection("users2").document(currentUser.getEmail())
                                            .update("borrowed_books", FieldValue.arrayUnion(docId));
                                    db.collection("users2").document(currentUser.getEmail())
                                            .update("accepted_books", FieldValue.arrayRemove(docId));

                                }
                            }
                        }
                    });
                } else {//9780735213678
                    HashMap<String, Object> editedInfo = new HashMap<>();
                    editedInfo.put("isOwnerScan", true);
                    DocumentReference docRef = db.collection("books").document(docId);
                    docRef.update(editedInfo);
                    Toast.makeText(getActivity(), "Scan recorded, thank you.", LENGTH_SHORT).show();

                    db.collection("users2").document(currentUser.getEmail()).update("my_books." + docId, "borrowed");


                }
            } else {
                Toast.makeText(getActivity(), "Scan failed, please try again.", LENGTH_SHORT).show();

            }
        }
    }
}