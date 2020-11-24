package com.example.bookit;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class EditDeleteActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        String value = getIntent().getExtras().getString("bookID");

        Bundle bundle = new Bundle();
        bundle.putString("bookID", value);
        EditBookFragment editBookFragment = new EditBookFragment();
        if(getIntent().hasExtra("CallFrom")){
            bundle.putString("CallFrom",getIntent().getExtras().getString("CallFrom"));
        }
        editBookFragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.common_container, editBookFragment);// give your fragment container id in first parameter
        transaction.commit();
    }
}
