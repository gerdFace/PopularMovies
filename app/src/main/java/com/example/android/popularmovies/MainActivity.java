package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.example.android.popularmovies.helper.Constants;
import com.example.android.popularmovies.model.Movie;

public class MainActivity extends AppCompatActivity implements MainFragment.MovieAdapterOnClickHandler {

    private boolean mTwoPane = false;
    private MainFragment mMainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.detail_container) != null) {
            mTwoPane = true;
        }

        if (savedInstanceState != null) {
            mMainFragment = (MainFragment) getSupportFragmentManager().getFragment(savedInstanceState,
                                                                                   Constants.MAIN_FRAGMENT_ID);
        } else {
            mMainFragment = new MainFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container, mMainFragment,
                                                                   Constants.MAIN_FRAGMENT_TAG).commit();
        }
    }

    @Override
    public void onClick(Movie selectedMovie) {
        if (mTwoPane) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.SELECTED_MOVIE_TAG, selectedMovie);
            MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
            movieDetailFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.detail_container,
                                                                   movieDetailFragment).commit();
        } else {
            Context context = this;
            Class movieDetailClassDestination = MovieDetailActivity.class;
            Intent openMovieDetailActivity = new Intent(context, movieDetailClassDestination);
            openMovieDetailActivity.putExtra(Constants.SELECTED_MOVIE_TAG, selectedMovie);
            startActivity(openMovieDetailActivity);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, Constants.MAIN_FRAGMENT_ID, mMainFragment);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        getSupportFragmentManager().putFragment(outState, Constants.MAIN_FRAGMENT_ID, mMainFragment);
    }
}
