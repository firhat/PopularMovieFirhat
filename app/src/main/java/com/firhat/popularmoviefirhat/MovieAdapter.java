package com.firhat.popularmoviefirhat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Macbook on 7/8/17.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder>{

    final private ListItemClickListener mOnClickListener;

    private List<MovieModel> movieModelList;
    Context mContext;

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public MovieAdapter(Context mContext, List<MovieModel> movieModelList, ListItemClickListener listener) {
        this.mContext = mContext;
        this.movieModelList = movieModelList;
        mOnClickListener = listener;

    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        final boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        MovieViewHolder movieViewHolder = new MovieViewHolder(view);

        return movieViewHolder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        //Log.d(TAG, "#" + position);
        MovieModel movie = movieModelList.get(position);
        //holder.title.setText(movie.getTitle());

        Picasso.with(mContext)
                .load("http://image.tmdb.org/t/p/w185/"+movie.getPoster_path())
                .into(holder.img);

        //holder.genre.setText(movie.getGenre());
        //holder.year.setText(movie.getYear());
        //holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return movieModelList.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Will display the position in the list, ie 0 through getItemCount() - 1
        //TextView title;
        ImageView img;

        public MovieViewHolder(View itemView) {
            super(itemView);

            //title = (TextView) itemView.findViewById(R.id.txt_title);
            img = (ImageView) itemView.findViewById(R.id.img);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
