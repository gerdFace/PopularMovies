package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
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
import com.example.android.popularmovies.adapter.MovieAdapter;
import com.example.android.popularmovies.data.FavoritesContract;
import com.example.android.popularmovies.data.FavoritesProvider;
import com.example.android.popularmovies.data.JsonMovieDataExtractor;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.helper.Constants;
import com.example.android.popularmovies.helper.HttpPathListCreator;
import com.example.android.popularmovies.network.NetworkConnector;

import java.net.URL;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    @BindView(R.id.rv_movies)
    RecyclerView mMovieRecyclerView;

    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingProgressBar;

    @BindView(R.id.error_message)
    TextView mErrorMessage;

    private MovieAdapter mMovieAdapter;
    private SharedPreferences mLastUsedSortPreference;
    private boolean mUseThreeColumnGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mUseThreeColumnGrid = getResources().getBoolean(R.bool.display_three_column_grid);

        mLastUsedSortPreference = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        mMovieRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, checkOrientation(mUseThreeColumnGrid), LinearLayoutManager.VERTICAL, false);
        mMovieRecyclerView.setLayoutManager(gridLayoutManager);
        mMovieAdapter = new MovieAdapter(this, this);
        mMovieRecyclerView.setAdapter(mMovieAdapter);

        loadMovies(mLastUsedSortPreference.getString(Constants.PREF_KEY, ""));
    }

    private void loadMovies(String sortPreference) {
        mLoadingProgressBar.setVisibility(View.VISIBLE);
        fetchMovies(getSortOrder(sortPreference));
    }

    private int checkOrientation(boolean shouldDisplayThreeColumns) {
        int portrait = 2;
        int landscape = 3;

        if (shouldDisplayThreeColumns) {
            return landscape;
        } else {
            return portrait;
        }
    }

    @NonNull
    private String getSortOrder(String sortPreferenceString) {
        String sortPreference;
        if (sortPreferenceString.isEmpty()) {
            sortPreference = Constants.POPULAR_SORT;
        } else {
            sortPreference = sortPreferenceString;
        }
        return sortPreference;
    }

    private void fetchMovies(String sortBy) {
        if (!checkIsOnline() && sortBy.equals(Constants.FAVORITE_SORT)) {
            showNoInternetErrorMessageView();
        } else {
            new FetchMoviesTask().execute(sortBy);
            showMoviesView();
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

    private class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            switch (params[0]) {

                case Constants.POPULAR_SORT:
                case Constants.TOP_RATED_SORT:
                    String sortPreference = params[0];
                    NetworkConnector networkConnector = new NetworkConnector();
                    URL movieRequestUrl = networkConnector.buildUrl(new HttpPathListCreator().createListForHttpPath(Constants.MOVIES, sortPreference));

                    try {
                        String jsonMovieResponse = networkConnector.getResponseFromHttpUrl(movieRequestUrl);

                        return new JsonMovieDataExtractor().getExtractedMovieStringsFromJson(jsonMovieResponse);

                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }

                case Constants.FAVORITE_SORT:
                    ArrayList<Movie> favoriteMovieList = new ArrayList<>();
                    Cursor cursor = getApplication().getContentResolver()
                            .query(FavoritesProvider.Favorites.CONTENT_URI, null, null, null, null);
                    mMovieAdapter.clearMovies();
                    if (cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        while (cursor.moveToNext()) {
                            Movie favoriteMovie = new Movie(
                                    cursor.getString(cursor.getColumnIndex(FavoritesContract.MOVIE_TITLE)),
                                    cursor.getString(cursor.getColumnIndex(FavoritesContract.POSTER_PATH)),
                                    cursor.getString(cursor.getColumnIndex(FavoritesContract.USER_RATING)),
                                    cursor.getString(cursor.getColumnIndex(FavoritesContract.OVERVIEW)),
                                    cursor.getString(cursor.getColumnIndex(FavoritesContract.RELEASE_DATE)),
                                    cursor.getString(cursor.getColumnIndex(FavoritesContract.MOVIE_ID))
                            );
                            favoriteMovieList.add(favoriteMovie);
                        }
                        cursor.close();
                        return favoriteMovieList;
                    } else {
                        return null;
                    }

                default:
                    return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> results) {
            mLoadingProgressBar.setVisibility(View.INVISIBLE);
//    TODO Fix progressbar
            if (results != null) {
                mMovieAdapter.addMovies(results);
            } else {
                showNoFavoritesMessageView();
            }
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
        SharedPreferences.Editor sortEditor = mLastUsedSortPreference.edit();

        if (menuItemId == R.id.action_sort_most_popular) {
            mMovieAdapter.clearMovies();
            loadMovies(Constants.POPULAR_SORT);
            sortEditor.putString(Constants.PREF_KEY, Constants.POPULAR_SORT);
            sortEditor.apply();
        }

        if (menuItemId == R.id.action_sort_top_rated) {
            mMovieAdapter.clearMovies();
            loadMovies(Constants.TOP_RATED_SORT);
            sortEditor.putString(Constants.PREF_KEY, Constants.TOP_RATED_SORT);
            sortEditor.apply();
        }

        if (menuItemId == R.id.action_sort_favorites) {
            mMovieAdapter.clearMovies();
            loadMovies(Constants.FAVORITE_SORT);
            sortEditor.putString(Constants.PREF_KEY, Constants.FAVORITE_SORT);
            sortEditor.apply();

        }
        return super.onOptionsItemSelected(item);
    }

    public void showMoviesView() {
        mErrorMessage.setVisibility(View.INVISIBLE);
        mMovieRecyclerView.setVisibility(View.VISIBLE);
    }

    public void showNoInternetErrorMessageView() {
        mErrorMessage.setText(getString(R.string.no_internet_error_message));
        mErrorMessage.setVisibility(View.VISIBLE);
        mMovieRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void showNoFavoritesMessageView() {
        mErrorMessage.setText(getString(R.string.no_favorites_message));
        mErrorMessage.setVisibility(View.VISIBLE);
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
