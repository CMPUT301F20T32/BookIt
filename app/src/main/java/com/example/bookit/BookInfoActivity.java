package com.example.bookit;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This Activity shows the Book Information to a user
 * This Activity primarily acts as a container for {@link BookInfoFragment}
 */
public class BookInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);

        Bundle result = new Bundle();
        Intent intent = getIntent();

        // Putting data in the intent for BookInfoFragment
        result.putString("userEmail", intent.getStringExtra("userEmail"));
        result.putString("ownerBook", intent.getStringExtra("ownerBook"));
        result.putString("ownerAcceptedBook", intent.getStringExtra("ownerAcceptedBook"));
        result.putString("manageRequests", intent.getStringExtra("manageRequests"));
        result.putString("currentUser", intent.getStringExtra("currentUser"));
        result.putString("searchFragment", intent.getStringExtra("searchFragment"));
        result.putString("acceptedRequestsFragment", intent.getStringExtra("acceptedRequestsFragment"));
        result.putString("bookId", intent.getStringExtra("bookId"));
        result.putString("ownerId", intent.getStringExtra("ownerId"));
        result.putString("bookName", intent.getStringExtra("bookName"));
        result.putString("status", intent.getStringExtra("status"));
        result.putString("isbn", intent.getStringExtra("isbn"));
        result.putString("requesterUsername", intent.getStringExtra("requesterUsername"));

        BookInfoFragment bookInfoFragment = new BookInfoFragment();
        bookInfoFragment.setArguments(result);

        // Open the fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_book_info, bookInfoFragment, "bookInfoFragment")
                .commit();
    }
}