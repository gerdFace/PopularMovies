package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by gerdface on 4/5/17.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private ArrayList<Review> reviewList = new ArrayList<>();

    public void addReviews(ArrayList<Review> reviews) {
        this.reviewList = reviews;
        notifyDataSetChanged();
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutItemId = R.layout.review_list_item;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(layoutItemId, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        String reviewAuthor = reviewList.get(position).getReviewAuthor();
        String reviewContent = reviewList.get(position).getReviewContent();
        holder.mReviewAuthor.setText(reviewAuthor);
        holder.mReviewContent.setText(reviewContent);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView mReviewAuthor;
        TextView mReviewContent;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            Context context = itemView.getContext();
            mReviewAuthor = (TextView) itemView.findViewById(R.id.tv_review_author);
            mReviewContent = (TextView) itemView.findViewById(R.id.tv_review_content);
        }
    }
}

