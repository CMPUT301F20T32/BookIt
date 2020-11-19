package com.example.bookit;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class LocationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        String value = getIntent().getExtras().getString("bookID");
        Bundle bundle = new Bundle();
        bundle.putString("bookID", value);

        /*
         * type 1 = set drop off location (owner)
         * type 2 = get drop off location (borrower)
         */
        int type = getIntent().getExtras().getInt("type");

        if (type == 1) {
            SetLocationFragment fragment = new SetLocationFragment();
            fragment.setArguments(bundle);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.common_container, fragment);
            transaction.commit();

        }

        else if (type == 2){
            GetLocationFragment fragment = new GetLocationFragment();
            fragment.setArguments(bundle);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.common_container, fragment);
            transaction.commit();
        }

    }
}
