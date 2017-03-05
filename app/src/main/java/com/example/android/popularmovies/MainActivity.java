package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private RecyclerView mMovieRecyclerView;
    private MovieAdapter mMovieAdapter;
    private String popularSort = "popular";
    private String topRatedSort = "top_rated";
    private ProgressBar mLoadingProgressBar;
    private SharedPreferences lastUsedSortPreference;
    private static final String PREF_NAME = "last_sort_setting";
    private static final String PREF_KEY = "last_sort_string";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Spinner sortSpinner = (Spinner) findViewById(R.id.sort_spinner);
//        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(this,
//                R.array.sort_settings_array, android.R.menu.simple_spinner_item);
//        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        sortSpinner.setAdapter(sortAdapter);
//        sortSpinner.setOnItemSelectedListener(this);
        lastUsedSortPreference = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        mLoadingProgressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);
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
//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.poster_sort_preference, menu);
        MenuItem item = menu.findItem(R.id.sort_spinner);
        Spinner spinner = (Spinner) item.getActionView();
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(this,
                R.array.sort_settings_array, android.R.layout.simple_spinner_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(sortAdapter);
//        spinner.setOnItemSelectedListener(this);
        return true;
    }

//    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

//
//        switch (position) {
//            case 0:
//                mMovieAdapter.clearMovies();
//                loadMovies(popularSort);
//                sortEditor.putString(PREF_KEY, popularSort);
//                sortEditor.commit();
//                break;
//            case 1:
//                mMovieAdapter.clearMovies();
//                loadMovies(topRatedSort);
//                sortEditor.putString(PREF_KEY, topRatedSort);
//                sortEditor.commit();
//                break;
//
//        }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String itemSelected = item.toString();
        SharedPreferences.Editor sortEditor = lastUsedSortPreference.edit();
    if (itemSelected.equals("Most Popular")) {
            mMovieAdapter.clearMovies();
            loadMovies(popularSort);
            sortEditor.putString(PREF_KEY, popularSort);
            sortEditor.commit();
        }

        if (itemSelected.equals("Top-Rated")) {
            mMovieAdapter.clearMovies();
            loadMovies(topRatedSort);
            sortEditor.putString(PREF_KEY, topRatedSort);
            sortEditor.commit();
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public void onNothingSelected(AdapterView<?> parent) {
//
//    }
}
