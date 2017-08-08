package com.firhat.popularmoviefirhat;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firhat.popularmoviefirhat.data.FavoriteContract;


public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    private Context mContext;
    private Cursor mCursor;

    public FavoriteAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        this.mCursor = cursor;
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

        String title = mCursor.getString(mCursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_TITLE));

        holder.nameTextView.setText(title);

    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    class FavoriteViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;


        public FavoriteViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.txt_title);

        }

    }
}