package com.firhat.popularmoviefirhat;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.firhat.popularmoviefirhat.data.FavoriteContract;
import com.squareup.picasso.Picasso;


public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    final private FavoriteAdapter.ListItemClickListener mOnClickListener;

    private Context mContext;
    private Cursor mCursor;

    public interface ListItemClickListener {
        void onFavListItemClick(int clickedItemIndex);
    }

    public FavoriteAdapter(Context context, ListItemClickListener listener) {
        this.mContext = context;
        mOnClickListener = listener;
    }



    @Override
    public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.favorite_item, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavoriteViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position))
            return;

        if (isNetworkAvailable()){
            holder.nameTextView.setVisibility(View.GONE);
            holder.img.setVisibility(View.VISIBLE);

            String path = mCursor.getString(mCursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_POSTER_PATH));
            Picasso.with(mContext)
                    .load("http://image.tmdb.org/t/p/w185/"+path)
                    .into(holder.img);
        }else{
            holder.nameTextView.setVisibility(View.VISIBLE);
            holder.img.setVisibility(View.GONE);

            String title = mCursor.getString(mCursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_TITLE));
            holder.nameTextView.setText(title);
        }

    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    class FavoriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView nameTextView;
        ImageView img;


        public FavoriteViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.txt_title);
            img = (ImageView) itemView.findViewById(R.id.img_favorite);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onFavListItemClick(clickedPosition);
        }

    }

    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            // Network is present and connected
            isAvailable = true;
        }
        return isAvailable;
    }
}