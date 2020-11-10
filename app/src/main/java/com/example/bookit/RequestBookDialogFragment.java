package com.example.bookit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

/**
 * This Dialog Fragment is used to to request a book. When a user clicks on a search result from
 * their search this Dialog Fragment shows up to ask if they want to request that book.
 */
public class RequestBookDialogFragment extends DialogFragment {
    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        String bookId = getTargetFragment().getArguments().getString("bookId");
        String userEmail = getTargetFragment().getArguments().getString("userEmail");
        String userId = getTargetFragment().getArguments().getString("userId");
        String ownerId = getTargetFragment().getArguments().getString("ownerId");

        Log.d("DATA", bookId);
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Request Book?")
                .setPositiveButton("Yes!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User selects Yes
                        // Update the borrowers requested_books
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("users2").document(userEmail)
                                .update("requested_books", FieldValue.arrayUnion(bookId));

                        // Update the status of the book in the books collection
                        db.collection("books").document(bookId).update("status", "requested");

                        // Update the requesters array in the Book document
                        Log.d("DATA", "User id" + userId);
                        db.collection("books").document(bookId).update("requesters", FieldValue.arrayUnion(userId));

                        // Update my_books in the user document
                        db.collection("users2")
                                .whereEqualTo("user_info.username", ownerId)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            if (!task.getResult().isEmpty()) {
                                                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                                String ownerEmail = document.getId().toLowerCase();
                                                db.collection("users2").document(ownerEmail).update("my_books." + bookId, "requested");
                                                Log.d("DATA", "Owner Email " + ownerEmail);

                                            } else {
                                                Log.d("USER_EMAIL", "No such document");

                                            }
                                        } else {
                                            Log.d("USER_EMAIL", "get failed with ", task.getException());

                                        }
                                    }
                                });


                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        // Do nothing
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}

