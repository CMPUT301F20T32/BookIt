package com.example.bookit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * NotificationAdapter is the adapter used for NotificationsFragment
 * This Adapter is connected to the notification_layout
 * @author Nhat Minh Luu
 * @version 1.0
 * @since 1.0
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {
    private ArrayList<Notification> mDataset;

    private RecyclerViewClickListener mListener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // each data item is just a string in this case
        public TextView mText;
        public TextView mDatetime;


        private RecyclerViewClickListener mListener;

        public MyViewHolder(View v, RecyclerViewClickListener listener) {
            super(v);
            mText = v.findViewById(R.id.notification_text);
            mDatetime = v.findViewById(R.id.notification_time);

            mListener = listener;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition());
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public NotificationAdapter(ArrayList<Notification> myDataset, RecyclerViewClickListener listener) {
        mDataset = myDataset;
        mListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public NotificationAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_layout, parent, false);

        MyViewHolder vh = new MyViewHolder(v, mListener);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manger)
    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.MyViewHolder holder, int position) {
        // -get element from your dataset at this position
        // -replace the contents of the view with that element
        Notification currentItem = mDataset.get(position);
        //holder.mImageView.setImageResource(currentItem.getImag);
        holder.mText.setText(mDataset.get(position).getText());
        holder.mDatetime.setText(mDataset.get(position).getDatetime());
        //holder.mDatetime.setText(mDataset.get(position).getDatetime().toString());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}