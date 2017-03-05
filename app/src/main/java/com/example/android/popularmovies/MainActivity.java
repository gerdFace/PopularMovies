package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
import android.widget.TextView;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private RecyclerView mMovieRecyclerView;
    private MovieAdapter mMovieAdapter;
    private String popularSort = "popular";
    private String topRatedSort = "top_rated";
    private ProgressBar mLoadingProgressBar;
    private SharedPreferences lastUsedSortPreference;
    private TextView mNoInternetErrorMessage;
    private static final String PREF_NAME = "last_sort_setting";
    private static final String PREF_KEY = "last_sort_string";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lastUsedSortPreference = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        mLoadingProgressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mNoInternetErrorMessage = (TextView) findViewById(R.id.tv_no_internet_error_message);
        mMovieRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        mMovieRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false);
        mMovieRecyclerView.setLayoutManager(gridLayoutManager);
        mMovieAdapter = new MovieAdapter(this, this);
        mMovieRecyclerView.setAdapter(mMovieAdapter);

        loadMovies(lastUsedSortPreference.getString(PREF_KEY, ""));
    }

    private void loadMovies(String sortPreference) {
        String sortBy;

        if (sortPreference.isEmpty()) {
            sortBy = popularSort;
        } else {
            sortBy = sortPreference;
        }

        if (!checkIsOnline()) {
            showErrorMessageView();
        } else {
            showMoviesView();
            new FetchMoviesTask().execute(sortBy);
        }
    }

    @Override
    public void onClick(Movie selectedMovie) {
        Context context = this;
        Class movieDetailClassDestination = MovieDetailActivity.class;
        Intent openMovieDetailActivity = new Intent(context, movieDetailClassDestination);
        openMovieDetailActivity.putExtra("movieDetail", selectedMovie);
        startActivity(openMovieDetailActivity);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingProgressBar.setVisibility(View.VISIBLE);
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
        SharedPreferences.Editor sortEditor = lastUsedSortPreference.edit();

        if (menuItemId == R.id.action_sort_most_popular) {
            mMovieAdapter.clearMovies();
            loadMovies(popularSort);
            sortEditor.putString(PREF_KEY, popularSort);
            sortEditor.apply();        }

        if (menuItemId == R.id.action_sort_top_rated) {
            mMovieAdapter.clearMovies();
            loadMovies(topRatedSort);
            sortEditor.putString(PREF_KEY, topRatedSort);
            sortEditor.apply();
        }
        return super.onOptionsItemSelected(item);
    }

    public void showMoviesView() {
        mNoInternetErrorMessage.setVisibility(View.INVISIBLE);
        mMovieRecyclerView.setVisibility(View.VISIBLE);
    }

    public void showErrorMessageView() {
        mNoInternetErrorMessage.setVisibility(View.VISIBLE);
        mMovieRecyclerView.setVisibility(View.INVISIBLE);
    }

    /*Connectivity check method from http://stackoverflow.com/questions/1560788/
    how-to-check-internet-access-on-android-inetaddress-never-times-out?page=1&tab=votes#tab-top*/
    public boolean checkIsOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
