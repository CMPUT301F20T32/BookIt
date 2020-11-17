package com.example.bookit;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class SetLocationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        String value = getIntent().getExtras().getString("bookID");
        Bundle bundle = new Bundle();
        bundle.putString("bookID", value);
        SetLocationFragment fragment = new SetLocationFragment();
        fragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.common_container, fragment);
        transaction.commit();

    }
}
