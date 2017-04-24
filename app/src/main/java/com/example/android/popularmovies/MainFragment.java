package com.example.android.popularmovies;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.android.popularmovies.adapter.MovieAdapter;
import com.example.android.popularmovies.data.FavoritesProvider;
import com.example.android.popularmovies.data.JsonMovieDataExtractor;
import com.example.android.popularmovies.helper.Constants;
import com.example.android.popularmovies.helper.HttpPathListCreator;
import com.example.android.popularmovies.helper.TestInternetConnectivity;
import com.example.android.popularmovies.model.MainViewModel;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.network.NetworkConnector;
import java.net.URL;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import static android.content.Context.MODE_PRIVATE;
import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Movie>>{

    private static final String TAG = MainFragment.class.getSimpleName();
    private MovieAdapter mMovieAdapter;
    private SharedPreferences mLastUsedSortPreference;
    private MainViewModel mainViewModel;
    public static final int MOVIE_LOADER =  11;

    @BindView(R.id.rv_movies)
    RecyclerView mMovieRecyclerView;

    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingProgressBar;

    @BindView(R.id.error_message)
    TextView mErrorMessage;

    public MainFragment() {
    }

    public interface MovieAdapterOnClickHandler {
        void onClick(Movie selectedMovie);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getSortPreference().equals(Constants.FAVORITE_SORT)) {
            fetchMovies(Constants.FAVORITE_SORT);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        ButterKnife.bind(this, view);
        Log.i(TAG, "onCreateView");

        mainViewModel = new MainViewModel();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchMovies(getSortPreference());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMovieRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), getOrientation(), LinearLayoutManager.VERTICAL, false);
        mMovieRecyclerView.setLayoutManager(gridLayoutManager);
        mMovieAdapter = new MovieAdapter(getActivity());
        mMovieRecyclerView.setAdapter(mMovieAdapter);
    }

    private int getOrientation() {
        if (getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE) {
            return 3;
        } else {
            return 2;
        }
    }

    private String getSortPreference() {
        mLastUsedSortPreference = getActivity().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        String sortPreference = mLastUsedSortPreference.getString(Constants.PREF_KEY, "");
        return mainViewModel.getSortOrderString(sortPreference);
    }

    private void fetchMovies(String sortBy) {
        if (!TestInternetConnectivity.isDeviceOnline(getActivity()) && !sortBy.equals(Constants.FAVORITE_SORT)) {
            showErrorMessage(Constants.NO_INTERNET);
        } else {
            Bundle searchPreferenceBundle = new Bundle();
            searchPreferenceBundle.putString(Constants.PREF_BUNDLE_KEY, sortBy);
            LoaderManager loaderManager = getActivity().getSupportLoaderManager();
            Loader<ArrayList<Movie>> movieLoader = loaderManager.getLoader(MOVIE_LOADER);

            if (movieLoader == null) {
                loaderManager.initLoader(MOVIE_LOADER, searchPreferenceBundle, this);
            } else {
                loaderManager.restartLoader(MOVIE_LOADER, searchPreferenceBundle, this);
            }
            showMoviesView();
        }
    }

    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<ArrayList<Movie>>(getActivity()) {

            ArrayList<Movie> mMovieList;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (args == null) {
                    return;
                }

                if (mMovieList != null) {
                    deliverResult(mMovieList);
                } else {
                    forceLoad();
                }
            }

            @Override
            public ArrayList<Movie> loadInBackground() {
                String searchPreferenceFromBundle = args.getString(Constants.PREF_BUNDLE_KEY);
                if (searchPreferenceFromBundle == null || TextUtils.isEmpty(searchPreferenceFromBundle)) {
                    return null;
                }

                switch (searchPreferenceFromBundle) {

                    case Constants.POPULAR_SORT:
                    case Constants.TOP_RATED_SORT:
                        NetworkConnector networkConnector = new NetworkConnector();
                        URL movieRequestUrl = networkConnector.buildUrl(new HttpPathListCreator().createListForHttpPath(Constants.MOVIES, searchPreferenceFromBundle));

                        try {
                            String jsonMovieResponse = networkConnector.getResponseFromHttpUrl(movieRequestUrl);

                            return new JsonMovieDataExtractor().getExtractedMovieStringsFromJson(jsonMovieResponse);

                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }

                    case Constants.FAVORITE_SORT:
                        Log.d(TAG, "Loading from local db");
                        Cursor cursor = getActivity().getContentResolver()
                                .query(FavoritesProvider.Favorites.CONTENT_URI, null, null, null, null);

                        if (cursor != null) {
                            Log.d(TAG, "NonNull cursor");
                            mMovieAdapter.clearMovies();
                            ArrayList<Movie> favoriteList = mainViewModel.queryFavoriteDatabase(cursor);
                            cursor.close();
                            return favoriteList;
                        } else {
                            return null;
                        }

                    default:
                        return null;
                }
            }

            @Override
            public void deliverResult(ArrayList<Movie> data) {
                mMovieList = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> movies) {
        mLoadingProgressBar.setVisibility(View.INVISIBLE);
        if (movies != null) {
            mMovieAdapter.addMovies(movies);
            showMoviesView();
        } else {
            showErrorMessage(Constants.FAVORITE_SORT);
        }

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {
        Log.d(TAG, "Loader restarting");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.poster_sort_preference, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemId = item.getItemId();

        if (menuItemId == R.id.action_sort_most_popular) {
            updateSortPreference(Constants.POPULAR_SORT);
            mMovieAdapter.clearMovies();
            fetchMovies(Constants.POPULAR_SORT);

        } else if (menuItemId == R.id.action_sort_top_rated) {
            mMovieAdapter.clearMovies();
            updateSortPreference(Constants.TOP_RATED_SORT);
            fetchMovies(Constants.TOP_RATED_SORT);

        } else if (menuItemId == R.id.action_sort_favorites) {
            mMovieAdapter.clearMovies();
            updateSortPreference(Constants.FAVORITE_SORT);
            fetchMovies(Constants.FAVORITE_SORT);
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateSortPreference(String sortPreference) {
        SharedPreferences.Editor sortEditor = mLastUsedSortPreference.edit();
        sortEditor.putString(Constants.PREF_KEY, sortPreference);
        sortEditor.apply();
    }

    private void showMoviesView() {
        mErrorMessage.setVisibility(View.INVISIBLE);
        mMovieRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage(String messageToDisplay) {
        mErrorMessage.setVisibility(View.VISIBLE);
        mMovieRecyclerView.setVisibility(View.INVISIBLE);

        if (messageToDisplay.equals(Constants.FAVORITE_SORT)) {
            mErrorMessage.setText(getString(R.string.no_favorites_message));
        } else {
            mErrorMessage.setText(getString(R.string.no_internet_error_message));
        }
    }






}
