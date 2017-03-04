package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private RecyclerView mMovieRecyclerView;
    private MovieAdapter mMovieAdapter;
    private String popularSort = "popular";
    private String topRatedSort = "top_rated";
    private ProgressBar mLoadingProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadingProgressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mMovieRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        mMovieRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false);
        mMovieRecyclerView.setLayoutManager(gridLayoutManager);
        mMovieAdapter = new MovieAdapter(this, this);
        mMovieRecyclerView.setAdapter(mMovieAdapter);
        loadMovies(popularSort);
    }

    private void loadMovies(String sortPreference) {
        String sortBy = sortPreference;
        new FetchMoviesTask().execute(sortBy);
    }

    @Override
    public void onClick(Movie selectedMovie) {
        Context context = this;
        Class movieDetailClassDestination = MovieDetailActivity.class;
//        Bundle movieDetailBundle = new Bundle();
        Intent openMovieDetailActivity = new Intent(context, movieDetailClassDestination);
        openMovieDetailActivity.putExtra("movieDetail", selectedMovie);
        startActivity(openMovieDetailActivity);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>> {
        @Override
        protected void onPreExecute() {
            mLoadingProgressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            String param = params[0];
            URL movieRequestUrl = NetworkConnector.buildMovieUrl(param);

            try {
                String jsonMovieResponse = NetworkConnector.getResponseFromHttpUrl(movieRequestUrl);

                ArrayList<Movie> extractedMovieInformation = JsonMovieDataExtractor
                        .getExtractedMovieStringsFromJson(jsonMovieResponse);

                return extractedMovieInformation;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> results) {
            mLoadingProgressBar.setVisibility(View.INVISIBLE);
            mMovieAdapter.addMovies(results);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.poster_sort_preference, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemId = item.getItemId();

        if (menuItemId == R.id.action_sort_most_popular) {
            mMovieAdapter.clearMovies();
            loadMovies(popularSort);
        }

        if (menuItemId == R.id.action_sort_top_rated) {
            mMovieAdapter.clearMovies();
            loadMovies(topRatedSort);
        }
        return super.onOptionsItemSelected(item);
    }
}
