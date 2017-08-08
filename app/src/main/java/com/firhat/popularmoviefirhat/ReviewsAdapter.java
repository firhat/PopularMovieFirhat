package com.firhat.popularmoviefirhat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Macbook on 7/8/17.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsViewHolder>{

    final private ListItemClickListener mOnClickListener;

    private List<ReviewsModel> reviewsModelList;
    Context mContext;

    public interface ListItemClickListener {
        void onReviewListItemClick(int clickedItemIndex);
    }

    public ReviewsAdapter(Context mContext, List<ReviewsModel> reviewsModelList, ListItemClickListener listener) {
        this.mContext = mContext;
        this.reviewsModelList = reviewsModelList;
        mOnClickListener = listener;

    }

    @Override
    public ReviewsViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.reviews_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        final boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        ReviewsViewHolder reviewsViewHolder = new ReviewsViewHolder(view);

        return reviewsViewHolder;
    }

    @Override
    public void onBindViewHolder(ReviewsViewHolder holder, int position) {
        //Log.d(TAG, "#" + position);
        ReviewsModel review = reviewsModelList.get(position);
        holder.txtAuthor.setText(review.getAuthor());
        holder.txtContent.setText(review.getContent());

//        Picasso.with(mContext)
//                .load("http://image.tmdb.org/t/p/w185/"+movie.getPoster_path())
//                .into(holder.img);

        //holder.genre.setText(movie.getGenre());
        //holder.year.setText(movie.getYear());
        //holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return reviewsModelList.size();
    }

    class ReviewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Will display the position in the list, ie 0 through getItemCount() - 1
        TextView txtAuthor;
        TextView txtContent;

        public ReviewsViewHolder(View itemView) {
            super(itemView);

            txtAuthor = (TextView) itemView.findViewById(R.id.txt_author);
            txtContent = (TextView) itemView.findViewById(R.id.txt_content);
            //img = (ImageView) itemView.findViewById(R.id.img);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onReviewListItemClick(clickedPosition);
        }
    }
}
