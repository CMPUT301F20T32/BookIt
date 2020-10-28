package com.example.bookit;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.jetbrains.annotations.NotNull;

public class FixedTabsPagerAdapter extends FragmentStateAdapter {
    public FixedTabsPagerAdapter(Fragment fragment) {
        super(fragment);
    }

    @NotNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new RequestedBooks();
            case 2:
                return new AcceptedBooks();
            case 3:
                return new BorrowedBooks();
            default:
                return new AvailableBooks(); // Basically when position = 0
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
