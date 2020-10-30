package com.example.bookit;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class EditProfileFragment  extends Fragment {

    // declare xml elements
    private TextView fullName, username, email, phone;
    private TextView nameHeader, usernameHeader, emailHeader, phoneHeader;
    private FloatingActionButton saveChangesButton;
    private Toolbar myProfileToolbar;
    private ImageView userIcon, emailIcon, phoneIcon;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
