package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

/**
 * Created by gerdface on 2/27/17.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private final MovieAdapterOnClickHandler mMovieClickHandler;
    private static final String TAG = MovieAdapter.class.getSimpleName();
    ArrayList<Movie> movieList = new ArrayList<>();
    Context context;

    public interface MovieAdapterOnClickHandler {
        void onClick(Movie selectedMovie);
    }

    public MovieAdapter(Context context, MovieAdapterOnClickHandler clickHandler) {
        this.context = context;
        mMovieClickHandler = clickHandler;
    }

    public void addMovies(ArrayList<Movie> movies) {
        movieList.addAll(movies);
        notifyDataSetChanged();
    }

    public void clearMovies() {
        movieList.clear();
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
        return movieList.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        ImageView imageView;
        Context context;

        public MovieViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            imageView = (ImageView) itemView.findViewById(R.id.iv_list_item_movie_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int movieAdapterPositionOfMovieSelected = getAdapterPosition();
            Movie selectedMovie = movieList.get(movieAdapterPositionOfMovieSelected);
            mMovieClickHandler.onClick(selectedMovie);
        }
    }
}
