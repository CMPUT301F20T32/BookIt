package com.example.bookit;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RequestedBooks#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestedBooks extends Fragment {

    private RecyclerView requestedRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RequestedBooks() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RequestedBooks.
     */
    // TODO: Rename and change types and number of parameters
    public static RequestedBooks newInstance(String param1, String param2) {
        RequestedBooks fragment = new RequestedBooks();
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
        return inflater.inflate(R.layout.fragment_requested_books, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        requestedRecyclerView = view.findViewById(R.id.requested_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        requestedRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(view.getContext());
        requestedRecyclerView.setLayoutManager(layoutManager);

        // This is just dummy data
        BookItemLayout[] myDataset = {new BookItemLayout("Harry Potter and the Philsopher's Stone", "J.K Rowling", "123456789", "Available"),
                new BookItemLayout("Cracking the Coding Interview", "Gayle Mcdowell", "1010101010", "Requested", "Vyome Agarwal"),
                new BookItemLayout("Percy Jackson and the Lighting Thief", "Rick Riordan", "987654321", "Requested", "Tony Stark"),};

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(myDataset);
        requestedRecyclerView.setAdapter(mAdapter);

    }
}