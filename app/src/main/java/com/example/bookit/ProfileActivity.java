
package com.example.bookit;


import android.app.FragmentTransaction;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

    public class ProfileActivity extends AppCompatActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_profile);

            MyProfileFragment myProfileFragment = new MyProfileFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_container, myProfileFragment); // give your fragment container id in first parameter
            transaction.commit();
        }
    }
