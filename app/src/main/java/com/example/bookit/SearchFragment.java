/*
 *  Classname: SearchFragment
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
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.ListFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * SearchFragment refers to the Searching Book and Making Request functionalities of the application.
 * The flow of the fragment is as follows:
 * <ul>
 *     <li> User can search for the book using the searchBar on the top</li>
 *     <li> The books can be searched by bookTitle, author, or ISBN </li>
 *     <li> After the user get the result from searching, user can make a request</li>
 *     <li> for that book by clicking on the book</li>
 * </ul>
 *
 * @author Vyome Agarwal
 * @author Nhat Minh Luu
 * @version 1.0
 * @since 1.0
 */

public class SearchFragment extends ListFragment {
    private Book longClickedBook;

    private SearchView searchView;

    private RecyclerView requestedRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<Book> myDataset = new ArrayList<Book>();
    private ArrayList<String> bookIds = new ArrayList<String>();
    private ArrayList<String> ownerIds = new ArrayList<String>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        return v;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Toast.makeText(getActivity(), "Long press to show owner information", LENGTH_SHORT).show();
        Bundle result = new Bundle();
        Bundle userBundle = new Bundle();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userEmail = mAuth.getCurrentUser().getEmail();

        searchView = view.findViewById(R.id.search_box);
        requestedRecyclerView = view.findViewById(R.id.my_search_list);

        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        requestedRecyclerView.setHasFixedSize(true);

        // Use a linear layout manager
        layoutManager = new LinearLayoutManager(view.getContext());
        requestedRecyclerView.setLayoutManager(layoutManager);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference allBookReference = db.collection("books");

        // Set up the Search View
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Filter the database
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                // Get the username of the Borrower
                DocumentReference docRef = db.collection("users2").document(userEmail);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d("DATA", document.getString("user_info.username"));
                                String userId = document.getString("user_info.username").toLowerCase();
                                result.putString("userId", userId);
                                userBundle.putString("userName", document.getString("user_info.username"));

                            } else {
                                Log.d("USER_EMAIL", "No such document");

                            }
                        } else {
                            Log.d("USER_EMAIL", "get failed with ", task.getException());

                        }
                    }
                });


                // Filter the books into "requested" and "available"
                allBookReference
                        .whereIn("status", Arrays.asList("requested", "available"))
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                // Clear the dataSet
                                myDataset.clear();
                                bookIds.clear();
                                ownerIds.clear();
                                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                    Log.d("READ_DATA", "DocumentSnapshot data: " + doc.getData());
                                    String bookTitle = (String) doc.getData().get("book_title");
                                    String author = (String) doc.getData().get("author");
                                    String isbn = (String) doc.getData().get("isbn");
                                    String status = (String) doc.getData().get("status");
                                    String ownerId = (String) doc.getData().get("owner");
                                    String bookId = (String) doc.getData().get("bookID");
                                    String imageLink = (String) doc.getData().get("image_link");
                                    // filter with the keyword
                                    if ((bookTitle.toLowerCase().contains(newText.toLowerCase()) ||
                                            author.toLowerCase().contains(newText.toLowerCase()) ||
                                            isbn.equals(newText.toLowerCase()))
                                            & (userBundle.getString("userName").equals(ownerId) == false)) {  // exclude the book owned by User
                                        myDataset.add(new Book(bookTitle, author, isbn, status, ownerId, bookId, imageLink));
                                        bookIds.add(doc.getId());
                                        ownerIds.add(ownerId);
                                    }
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                return true;
            }
        });

        // Request functionality (Tap to a book to request one not accepted/ borrowed)
        mAdapter = new MyNewAdapter(myDataset, "owner", new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {

                result.putString("bookId", bookIds.get(position));
                result.putString("userEmail", userEmail);
                result.putString("ownerId", ownerIds.get(position));
                DialogFragment f = new RequestBookDialogFragment();
                f.setTargetFragment(SearchFragment.this, 1);
                f.getTargetFragment().setArguments(result);
                f.show(getParentFragmentManager(), "RequestBookDialogFragment");

                Log.d("DATA", myDataset.get(position).getBookTitle());
                Log.d("DATA", bookIds.get(position));
            }

            @Override
            public boolean onLongClick(View view, int position) {
                longClickedBook = myDataset.get(position);
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                intent.putExtra("user", longClickedBook.getBorrower());
                startActivity(intent);
                return true;
            }
        });

        // Set up the adapter
        requestedRecyclerView.setAdapter(mAdapter);

    }
}