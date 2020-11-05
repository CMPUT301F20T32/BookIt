package com.example.bookit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MyBooksFragment extends Fragment {

    FixedTabsPagerAdapter fixedTabsPagerAdapter;
    ViewPager2 viewPager;
    private int i;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_books, container, false);


    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        fixedTabsPagerAdapter = new FixedTabsPagerAdapter(this);
        viewPager = view.findViewById(R.id.pager);
        viewPager.setAdapter(fixedTabsPagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) ->{
            switch (position) {
                case 0:
                    tab.setText("Available");
                    break;

                case 1:
                    tab.setText("Requested");
                    break;

                case 2:
                    tab.setText("Accepted");
                    break;

                case 3:
                    tab.setText("Borrowed");
                    break;

                default:
                    tab.setText("N/A");
            }
        }
                ).attach();

    }
}