package com.example.bookit;


import android.view.View;

/**
 * This interface is used to re-implement the onClick method for the RecyclerView which is
 * connected to MyNewAdapter
 * This function also return the position of the item being clicked on the view
 *
 * @author Nhat Minh Luu
 * @version 1.0
 * @since 1.0
 */
public interface RecyclerViewClickListener {
    void onClick(View view, int position);

    boolean onLongClick(View view, int position);
}