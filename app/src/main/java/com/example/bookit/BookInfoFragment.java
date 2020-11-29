package com.example.bookit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static android.widget.Toast.LENGTH_SHORT;

/**
 * A simple {@link Fragment} subclass.
 * This Fragment displays the information related to Books and allows the owner
 * to edit the book, hand-in, receive books by scanning
 */
public class BookInfoFragment extends Fragment {

    private TextView owner;
    private TextView bookName;
    private TextView isbn;
    private TextView status;
    private FloatingActionButton editBook;
    private ImageView requestBook;
    private ImageView scanBook;
    private String ownerEmail;
    private FloatingActionButton mapButton;


    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private DocumentReference ownerRef;
    private CollectionReference colRef;
    private DocumentReference bookRef;
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

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        colRef = db.collection("books");
        bookRef = db.collection("books").document(getArguments().getString("bookId"));
        ownerRef = db.collection("users2").document(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        if (getArguments() != null) {

            bookName = view.findViewById(R.id.book_name_text_view);
            isbn = view.findViewById(R.id.isbn_text_view);
            owner = view.findViewById(R.id.owner_text_view);
            status = view.findViewById(R.id.status_text_view);
            requestBook = view.findViewById(R.id.request_book_image_view);
            editBook = view.findViewById(R.id.fab_edit_book);
            scanBook = view.findViewById(R.id.scan_image_view);
            mapButton = view.findViewById(R.id.map_image_view);

            ImageView viewProfile = view.findViewById(R.id.user_profile_image_view);
            FloatingActionButton acceptBook = view.findViewById(R.id.accept_image_view);
            FloatingActionButton declineBooks = view.findViewById(R.id.decline_image_view);

            //Disable the scan button
            scanBook.setEnabled(false);

            // Display the book information
            displayBookInfo();

            // Set the buttons to display
            setButtons(view);

            // Check if the scan button should be enabled or disabled
            checkScanButton();

            assignBookID();

            // Set onClickListeners for buttons
            scanBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), ScanBookActivity.class);
                    startActivityForResult(intent, 1);
                }
            });

            editBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), EditDeleteActivity.class);
                    Log.d("edit", getArguments().getString("bookId"));
                    Log.d("edit", getArguments().getString("isbn"));
                    intent.putExtra("bookID", getArguments().getString("bookId"));
                    intent.putExtra("isbn", getArguments().getString("isbn"));

                    startActivity(intent);
                }
            });

            requestBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requestBook();
                }
            });

            mapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //open activity to view exchange location
                    Intent intent = new Intent(getContext(), LocationActivity.class);
                    intent.putExtra("bookID", getArguments().getString("bookId"));
                    intent.putExtra("type", 2);
                    startActivity(intent);

                }
            });

            acceptBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    acceptBookRequest();
                }
            });

            declineBooks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    declineBookRequest();
                }
            });

            // Show the profile of the user when this button is clicked
            viewProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showProfile();
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
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (getArguments().getString("acceptedRequestsFragment") != null) {
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
                                    Log.d("DOCID", docId);
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
                } else {//9780735213678
                    HashMap<String, Object> editedInfo = new HashMap<>();
                    editedInfo.put("isOwnerScan", true);
                    DocumentReference docRef = db.collection("books").document(docId);
                    docRef.update(editedInfo);
                    Toast.makeText(getActivity(), "Scan recorded, thank you.", LENGTH_SHORT).show();


                }
            } else {
                Toast.makeText(getActivity(), "Scan failed, please try again.", LENGTH_SHORT).show();

            }
        }
    }

    private void assignBookID() {
        db.collection("books")
                .document(getArguments().getString("bookId")).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            //Log.d(TAG, document.getId() + " => " + document.getData());
                            docId = document.getId();

                        } else {
                            Log.d("Error", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    /**
     * This method displays the book information on this fragment
     * It displays the Book Title, ISBN, Status, and the User
     */
    private void displayBookInfo() {

        bookName.setText(getArguments().getString("bookName"));
        isbn.setText(getArguments().getString("isbn"));
        owner.setText(getArguments().getString("ownerId"));
        status.setText(getArguments().getString("status"));

        if ((getArguments().getString("ownerId") == null) && getArguments().get("requesterUsername") != null) {
            owner.setText(getArguments().getString("requesterUsername"));
        }
    }

    /**
     * This method decides what buttons should be visible on the fragment
     * depending from which {@link Fragment}, this Fragment {@link BookInfoFragment}
     * was called.
     *
     * @param view the View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} method
     */
    private void setButtons(View view) {

        if (getArguments().getString("acceptedRequestsFragment") != null) {
            scanBook.setVisibility(View.VISIBLE);
            mapButton.setVisibility(View.VISIBLE);

        }

        if (getArguments().get("manageRequests") != null) {
            view.findViewById(R.id.accept_image_view).setVisibility(View.VISIBLE);
            view.findViewById(R.id.decline_image_view).setVisibility(View.VISIBLE);

        }

        if (getArguments().getString("searchFragment") != null) {
            view.findViewById(R.id.request_book_image_view).setVisibility(View.VISIBLE);
            view.findViewById(R.id.accept_image_view).setVisibility(View.GONE);
            view.findViewById(R.id.decline_image_view).setVisibility(View.GONE);

        }

        // If the book is owned by the owner then show the button to edit the book information
        if (getArguments().getString("ownerBook") != null) {
            editBook.setVisibility(View.VISIBLE);
            requestBook.setVisibility(View.GONE);
        }


        if (getArguments().get("ownerAcceptedBook") != null) {
            scanBook.setVisibility(View.VISIBLE);
        }

    }

    /**
     * This method decides if the scan button should be enabled or disabled
     */
    private void checkScanButton() {

        if (getArguments().getString("acceptedRequestsFragment") != null) {

            DocumentReference docReference = db.collection("books").document(getArguments().getString("bookId"));
            Log.d("bookid", getArguments().getString("bookId"));

            docReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        Log.d("Hello", "???" + document.getId() + " => " + document.getData());
                        docId = document.getId();
                        if (document.getData().get("isOwnerScan") != null && document.getData().get("ownerEmail") != null) {
                            if (document.getData().get("isOwnerScan").toString().equals("true")) {
                                ownerEmail = document.getData().get("ownerEmail").toString();
                                scanBook.setEnabled(true);
                            } else {
                                scanBook.setEnabled(false);
                                Toast.makeText(getActivity(), "Please wait for the owner to scan first.", LENGTH_SHORT).show();
                            }
                        }//12312412481
                        else {
                            //setScan to false, add toast
                            scanBook.setEnabled(false);
                            Toast.makeText(getActivity(), "Please wait for the owner to scan first.", LENGTH_SHORT).show();

                        }

                    } else {
                        scanBook.setEnabled(false);
                        Toast.makeText(getActivity(), "Please wait for the owner to scan first.", LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            //lender
            scanBook.setEnabled(true);
        }


    }

    /**
     * This method updates the book status to accepted in the respective places in Firestore
     * and declines all other requests for that book
     */
    private void acceptBookRequest() {
        //update the book and owner doc
        bookRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        Log.d("READ_DATA", "DocumentSnapshot Data: " + doc.getData());
                        ArrayList<String> emptyArray = new ArrayList<>();
                        ArrayList<String> requesters = (ArrayList<String>) doc.get("requesters");
                        db.collection("books").document(getArguments().getString("bookId")).update("requesters", emptyArray);
                        db.collection("books").document(getArguments().getString("bookId")).update("borrower", getArguments().getString("requesterUsername"));
                        db.collection("books").document(getArguments().getString("bookId")).update("status", "accepted");
                        ownerRef.update("my_books." + getArguments().getString("bookId"), "accepted");

                        //update all the requesters docs
                        for (String requester : requesters) {
                            db.collection("users2")
                                    .whereEqualTo("user_info.username", requester).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                                        if (doc.exists()) {
                                            if (requester.equals(getArguments().getString("requesterUsername"))) {
                                                //accept the requesters request
                                                String requesterID = doc.getId();
                                                db.collection("users2").document(requesterID).update("requested_books", FieldValue.arrayRemove(getArguments().getString("bookId")));
                                                db.collection("users2").document(requesterID).update("accepted_books", FieldValue.arrayUnion(getArguments().getString("bookId")));
                                            } else {
                                                //delete the other requests
                                                String requesterID = doc.getId();
                                                db.collection("users2").document(requesterID).update("requested_books", FieldValue.arrayRemove(getArguments().getString("bookId")));
                                            }
                                        }
                                    }
                                }
                            });
                        }
                        Toast.makeText(getContext(), "Request by: " + getArguments().getString("requesterUsername") + " for: " + getArguments().getString("bookName") + " accepted ", Toast.LENGTH_SHORT).show();

                    } else {
                        Log.d("READ_DATA", "No such document");
                    }
                }
            }
        });

        openLocationActivity();


    }

    /**
     * This method declines a request for the book
     * Updates Firestore accordingly
     */
    private void declineBookRequest() {
        //update the book and owner doc
        bookRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        Log.d("READ_DATA", "DocumentSnapshot Data: " + doc.getData());
                        db.collection("books").document(doc.getId()).update("requesters", FieldValue.arrayRemove(getArguments().getString("requesterUsername")));
                        ArrayList<String> requesters = (ArrayList<String>) doc.get("requesters");
                        if (requesters.size() == 1) {
                            db.collection("books").document(getArguments().getString("bookId")).update("status", "available");
                            ownerRef.update("my_books." + getArguments().getString("bookId"), "available");
                            Toast.makeText(getContext(), "Request by: " + getArguments().getString("requesterUsername") + " for: " + getArguments().getString("bookName") + " declined", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Request by: " + getArguments().getString("requesterUsername") + " for: " + getArguments().getString("bookName") + " declined", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d("READ_DATA", "No such document");
                    }
                }
            }
        });
        //update the requesters doc
        db.collection("users2")
                .whereEqualTo("user_info.username", getArguments().getString("requesterUsername")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("QUERY_DATA", "searching for user doc: " + getArguments().getString("requesterUsername"));
                    DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                    String requesterID = doc.getId();
                    db.collection("users2").document(requesterID).update("requested_books", FieldValue.arrayRemove(getArguments().getString("bookId")));

                }
            }
        });

        getActivity().finish();

    }

    /**
     * This methods opens up the {@link LocationActivity}
     */
    private void openLocationActivity() {
        //open activity to set location for dropoff
        Intent intent = new Intent(getContext(), LocationActivity.class);
        intent.putExtra("bookID", getArguments().getString("bookId"));
        intent.putExtra("type", 1);
        startActivity(intent);
        getActivity().finish();

    }

    /**
     * This method is used to make a request for the book
     * Updates Firestore accordingly
     */
    private void requestBook() {
        Bundle result = new Bundle();
        result.putString("bookId", getArguments().getString("bookId"));
        result.putString("userEmail", getArguments().getString("userEmail"));
        result.putString("ownerId", getArguments().getString("ownerId"));
        result.putString("bookName", getArguments().getString("bookName"));
        result.putString("status", getArguments().getString("status"));
        result.putString("requesterUsername", getArguments().getString("requesterUsername"));

        // Open Dialog to confirm with the user to request the book
        DialogFragment f = new RequestBookDialogFragment();
        f.setTargetFragment(BookInfoFragment.this, 1);
        f.getTargetFragment().setArguments(result);
        f.show(getParentFragmentManager(), "RequestBookDialogFragment");

    }

    /**
     * This method opens up the {@link RetrieveInfoActivity} to display the user information
     */
    private void showProfile() {

        Intent intent = new Intent(getContext(), RetrieveInfoActivity.class);
        if (getArguments().getString("ownerId") != null) {
            intent.putExtra("user", getArguments().getString("ownerId"));

        } else {
            intent.putExtra("user", getArguments().getString("requesterUsername"));
        }

        startActivity(intent);

    }

}


