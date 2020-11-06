package com.example.bookit;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.jetbrains.annotations.NotNull;

public class MyRequestsFixedTabsPagerAdapter extends FragmentStateAdapter {

    public MyRequestsFixedTabsPagerAdapter(Fragment fragment) {
        super(fragment);
    }

    @NotNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new PendingRequestsFragment();
        }
        return new AcceptedRequestsFragment(); // Basically when position = 1
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
