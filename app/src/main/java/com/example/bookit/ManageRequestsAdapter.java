package com.example.bookit;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ManageRequestsAdapter extends RecyclerView.Adapter<ManageRequestsAdapter.MyViewHolder> {
    private ArrayList<Book> mDataset;
    private RecyclerViewClickListener mListener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // each data item is just a string in this case

        public TextView mBookTitle;
        public TextView mRequester;
        private RecyclerViewClickListener mListener;

        public MyViewHolder(View v, RecyclerViewClickListener listener) {
            super(v);
            //mImageView = v.findViewById(R.id.book_image);\

            mBookTitle = v.findViewById(R.id.book_title_request);
            mRequester = v.findViewById(R.id.requester_request);
            mListener = listener;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition());
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ManageRequestsAdapter(ArrayList<Book> myDataset, RecyclerViewClickListener listener) {
        mDataset = myDataset;
        mListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ManageRequestsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.manage_item_layout, parent, false);

        MyViewHolder vh = new MyViewHolder(v, mListener);
        return vh;
    }

    private int currentSelectedPosition = -1;
    private int lastSelectedPosition = -1;

    // Replace the contents of a view (invoked by the layout manger)
    @Override
    public void onBindViewHolder(@NonNull ManageRequestsAdapter.MyViewHolder holder, int position) {
        // -get element from your dataset at this position
        // -replace the contents of the view with that element

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference bookRef = db.collection("books").document(mDataset.get(position).getBookID());
        bookRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot bookDocument = task.getResult();
                    holder.mBookTitle.setText(bookDocument.get("book_title").toString());
                }
            }});

        holder.mRequester.setText("Requested by: " + mDataset.get(position).getRequester());
        holder.itemView.setBackgroundColor(currentSelectedPosition == position ? Color.GRAY : Color.TRANSPARENT);

        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                lastSelectedPosition = currentSelectedPosition;
                currentSelectedPosition = position;
                notifyItemChanged(lastSelectedPosition);
                notifyItemChanged(currentSelectedPosition);
                return false;
            }
        });
    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}