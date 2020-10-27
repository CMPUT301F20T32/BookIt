package com.example.bookit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookit.BookItemLayout;
import com.example.bookit.R;

import org.w3c.dom.Text;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private BookItemLayout[] mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView mImageView;
        public TextView mBookTitle;
        public TextView mAuthor;
        public TextView mISBN;
        public TextView mStatus;
        public TextView mBorrower;

        public MyViewHolder(View v) {
            super(v);
            //mImageView = v.findViewById(R.id.book_image);
            mBookTitle = v.findViewById(R.id.book_title);
            mAuthor = v.findViewById(R.id.author);
            mISBN = v.findViewById(R.id.isbn);
            mStatus = v.findViewById(R.id.status);
            mBorrower = v.findViewById(R.id.borrower);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(BookItemLayout[] myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_item_layout, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manger)
    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
        // -get element from your dataset at this position
        // -replace the contents of the view with that element
        BookItemLayout currentItem = mDataset[position];
        //holder.mImageView.setImageResource(currentItem.getImag);
        holder.mBookTitle.setText(mDataset[position].getBookTitle());
        holder.mAuthor.setText(mDataset[position].getAuthor());
        holder.mISBN.setText(mDataset[position].getISBN());
        holder.mStatus.setText(mDataset[position].getStatus());
        holder.mBorrower.setText(mDataset[position].getBorrower());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}
