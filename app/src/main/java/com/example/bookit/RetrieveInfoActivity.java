package com.example.bookit;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class RetrieveInfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve);

        //pass the information needed to the fragment of this activity
        String value = getIntent().getExtras().getString("user");
        Bundle bundle = new Bundle();
        bundle.putString("user", value);
        RetrieveInfoFragment retrieveInfoFragment = new RetrieveInfoFragment();
        /*if(getIntent().hasExtra("CallFrom")){
            bundle.putString("CallFrom",getIntent().getExtras().getString("CallFrom"));
        }*/
        retrieveInfoFragment.setArguments(bundle);

        //move to a fragment where all information is displayed
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.retrieve_container, retrieveInfoFragment);// give your fragment container id in first parameter
        transaction.commit();
    }
}
