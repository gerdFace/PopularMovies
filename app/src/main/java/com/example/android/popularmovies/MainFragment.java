package com.example.android.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class MainFragment extends Fragment {

    private static final String TAG = MainFragment.class.getSimpleName();
    private MovieAdapter mMovieAdapter;
    private SharedPreferences mLastUsedSortPreference;
    private MovieAdapterOnClickHandler mCallback;
    private View mView;
    private MainViewModel mainViewModel;

    @BindView(R.id.rv_movies)
    RecyclerView mMovieRecyclerView;

    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingProgressBar;

    @BindView(R.id.error_message)
    TextView mErrorMessage;

    public MainFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public interface MovieAdapterOnClickHandler {
        void onClick(Movie selectedMovie);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        ButterKnife.bind(this, view);
        Log.i(TAG, "onCreateView");
        mView = view;

        mainViewModel = new MainViewModel(getActivity());

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

    public int getOrientation() {
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
            new FetchMoviesTask().execute(sortBy);
            showMoviesView();
        }
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
                    return mainViewModel.queryFavoriteDatabase();

                default:
                    return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> results) {
            mLoadingProgressBar.setVisibility(View.INVISIBLE);
            if (results != null) {
                mMovieAdapter.addMovies(results);
            } else {
                showErrorMessage(Constants.FAVORITE_SORT);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.poster_sort_preference, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemId = item.getItemId();

        if (menuItemId == R.id.action_sort_most_popular && !isAlreadySorted(Constants.POPULAR_SORT)) {
            updateSortPreference(Constants.POPULAR_SORT);
            mMovieAdapter.clearMovies();
            fetchMovies(Constants.POPULAR_SORT);

        } else if (menuItemId == R.id.action_sort_top_rated && !isAlreadySorted(Constants.TOP_RATED_SORT)) {
            fetchMovies(Constants.TOP_RATED_SORT);
            mMovieAdapter.clearMovies();
            updateSortPreference(Constants.TOP_RATED_SORT);

        } else if (menuItemId == R.id.action_sort_favorites) {
            fetchMovies(Constants.FAVORITE_SORT);
            mMovieAdapter.clearMovies();
            updateSortPreference(Constants.FAVORITE_SORT);
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isAlreadySorted(String sortedBy) {
        return mLastUsedSortPreference.getString(Constants.PREF_KEY, "").equals(sortedBy);
    }

    public void updateSortPreference(String sortPreference) {
        SharedPreferences.Editor sortEditor = mLastUsedSortPreference.edit();
        sortEditor.putString(Constants.PREF_KEY, sortPreference);
        sortEditor.apply();
    }

    public void showMoviesView() {
        mErrorMessage.setVisibility(View.INVISIBLE);
        mMovieRecyclerView.setVisibility(View.VISIBLE);
    }

    public void showErrorMessage(String messageToDisplay) {
        mErrorMessage.setVisibility(View.VISIBLE);
        mMovieRecyclerView.setVisibility(View.INVISIBLE);

        if (messageToDisplay.equals(Constants.FAVORITE_SORT)) {
            mErrorMessage.setText(getString(R.string.no_favorites_message));
        } else {
            mErrorMessage.setText(getString(R.string.no_internet_error_message));
        }
    }






}
