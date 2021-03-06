package com.example.bookit;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.signature.ObjectKey;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * MyNewAdapter is the adapter used for the almost every class of the App where the Book item is shown
 * This Adapter is connected to the book_item_layout
 * @author Nhat Minh Luu
 * @version 1.0
 * @since 1.0
 */

public class MyNewAdapter extends RecyclerView.Adapter<MyNewAdapter.MyViewHolder> {
    private ArrayList<Book> mDataset;
    private  String mDisplay;
    private RecyclerViewClickListener mListener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        // each data item is just a string in this case
        public ImageView mImageView;
        public TextView mBookTitle;
        public TextView mAuthor;
        public TextView mISBN;
        public TextView mStatus;
        public TextView mBorrower;


        private RecyclerViewClickListener mListener;

        public MyViewHolder(View v, RecyclerViewClickListener listener) {
            super(v);
            mImageView = v.findViewById(R.id.book_image);
            mBookTitle = v.findViewById(R.id.book_title);
            mAuthor = v.findViewById(R.id.author);
            mISBN = v.findViewById(R.id.isbn);
            mStatus = v.findViewById(R.id.status);
            mBorrower = v.findViewById(R.id.borrower);

            mListener = listener;
            v.setOnClickListener(this);
            v.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            mListener.onLongClick(v, getAdapterPosition());
            return true;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyNewAdapter(ArrayList<Book> myDataset, String display, RecyclerViewClickListener listener) {
        mDataset = myDataset;
        mListener = listener;
        mDisplay = display;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public MyNewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_item_layout, parent, false);

        MyViewHolder vh = new MyViewHolder(v, mListener);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manger)
    @Override
    public void onBindViewHolder(@NonNull MyNewAdapter.MyViewHolder holder, int position) {
        // -get element from your dataset at this position
        // -replace the contents of the view with that element
        Book currentItem = mDataset.get(position);
        String imageLink = currentItem.getImageLink();
        if (!imageLink.equals("")) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference imgRef = storageRef.child("images/" + imageLink + ".jpg");
            GlideApp.with(holder.mImageView)
                    .load(imgRef)
                    .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                    .into(holder.mImageView);
        } else {
            Log.d("test", "asdf");
            holder.mImageView.setImageDrawable(ContextCompat.getDrawable(holder.mImageView.getContext(), R.drawable.default_book_image));
        }
        holder.mBookTitle.setText(mDataset.get(position).getBookTitle());
        holder.mAuthor.setText(String.format("Author: %s", mDataset.get(position).getAuthor()));
        holder.mISBN.setText(String.format("ISBN: %s", mDataset.get(position).getISBN()));
        holder.mStatus.setText(String.format("Status: %s", mDataset.get(position).getStatus()));
        if(mDisplay.equals("borrower")){
            holder.mBorrower.setText(String.format("Borrower: %s", mDataset.get(position).getBorrower()));
        }
        else if (mDisplay.equals("owner")){
            holder.mBorrower.setText(String.format("Owner: %s", mDataset.get(position).getBorrower()));
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


}