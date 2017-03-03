package com.example.android.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private RecyclerView mMovieRecyclerView;
    private MovieAdapter mMovieAdapter;
    private static final String PREFS_NAME = "Sort_Setting";
    private static final String PREFS_KEY = "popular";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sortSetting = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        mMovieRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        mMovieRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false);
        mMovieRecyclerView.setLayoutManager(gridLayoutManager);
        mMovieAdapter = new MovieAdapter(this);
        mMovieRecyclerView.setAdapter(mMovieAdapter);
        String defaultSortSetting = "popular";
        loadMovies(sortSetting.getString(PREFS_KEY, defaultSortSetting));
    }

    private void loadMovies(String sortSetting) {
        String sortBy = sortSetting;
        new FetchMoviesTask().execute(sortBy);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>> {
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
            mMovieAdapter.addMovies(results);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_sort_preference, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String popular = "popular";
        String topRated = "top_rated";
        SharedPreferences sortSetting = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor settingsEditor = sortSetting.edit();

        if (id == R.id.action_sort_most_popular) {
            settingsEditor.putString(PREFS_KEY, popular);
            settingsEditor.commit();
            return true;
        }

        if (id == R.id.action_sort_top_rated) {
            settingsEditor.putString(PREFS_KEY, topRated);
            settingsEditor.commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
