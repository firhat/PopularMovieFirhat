package com.firhat.popularmoviefirhat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Macbook on 7/8/17.
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailersViewHolder>{

    final private ListItemClickListener mOnClickListener;

    private List<TrailersModel> trailersModelList;
    Context mContext;

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public TrailersAdapter(Context mContext, List<TrailersModel> trailersModelList, ListItemClickListener listener) {
        this.mContext = mContext;
        this.trailersModelList = trailersModelList;
        mOnClickListener = listener;

    }

    @Override
    public TrailersViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.trailers_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        final boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        TrailersViewHolder trailersViewHolder = new TrailersViewHolder(view);

        return trailersViewHolder;
    }

    @Override
    public void onBindViewHolder(TrailersViewHolder holder, int position) {
        //Log.d(TAG, "#" + position);
        TrailersModel trailers = trailersModelList.get(position);
        holder.title.setText(trailers.getName());

//        Picasso.with(mContext)
//                .load("http://image.tmdb.org/t/p/w185/"+movie.getPoster_path())
//                .into(holder.img);

        //holder.genre.setText(movie.getGenre());
        //holder.year.setText(movie.getYear());
        //holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return trailersModelList.size();
    }

    class TrailersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Will display the position in the list, ie 0 through getItemCount() - 1
        TextView title;

        public TrailersViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.trailerName);
            //img = (ImageView) itemView.findViewById(R.id.img);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
