package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

/**
 * Created by gerdface on 2/27/17.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private static final String TAG = MovieAdapter.class.getSimpleName();
    ArrayList<Movie> movieList = new ArrayList<>();
    Context context;

    public MovieAdapter(Context context) {
        this.context = context;
    }

    public void addMovies (ArrayList<Movie> movies) {
        movieList.addAll(movies);
        notifyDataSetChanged();
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutItemId = R.layout.movie_list_item;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(layoutItemId, parent, false);

        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        String moviePosterUrl = movieList.get(position).getPoster();
        Picasso.with(context).load(moviePosterUrl).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        if (null == movieList) {
            return 0;
        }
        return movieList.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        Context context;

        public MovieViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            imageView = (ImageView) itemView.findViewById(R.id.iv_list_item_movie_poster);
        }
    }
}
