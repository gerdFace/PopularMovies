package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.adapter.ReviewAdapter;
import com.example.android.popularmovies.data.JsonMovieDataExtractor;
import com.example.android.popularmovies.model.Review;
import com.example.android.popularmovies.network.NetworkConnector;

import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewActivity extends AppCompatActivity {

    private ReviewAdapter reviewAdapter;

    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingProgressBar;

    @BindView(R.id.tv_no_internet_error_message)
    TextView mNoInternetErrorMessage;

    @BindView(R.id.rv_reviews)
    RecyclerView mReviewRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        String selectedMovieId = intent.getStringExtra("selected_movie");
        reviewAdapter = new ReviewAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mReviewRecyclerView.setHasFixedSize(true);
        mReviewRecyclerView.setLayoutManager(linearLayoutManager);
        mReviewRecyclerView.setAdapter(reviewAdapter);

        fetchMovies(selectedMovieId);
    }

    private void fetchMovies(String movieId) {
        if (!checkIsOnline()) {
            showErrorMessageView();
        } else {
            showMoviesView();
            new FetchReviewTask().execute(movieId);
        }
    }

    private class FetchReviewTask extends AsyncTask<String, Void, ArrayList<Review>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Review> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String movieId = params[0];
            NetworkConnector networkConnector = new NetworkConnector();
            URL reviewRequestUrl = networkConnector.buildReviewsUrl(movieId);

            try {
                String jsonMovieResponse = networkConnector.getResponseFromHttpUrl(reviewRequestUrl);

                return JsonMovieDataExtractor.getExtractedReviewStringsFromJson(jsonMovieResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Review> results) {
            mLoadingProgressBar.setVisibility(View.INVISIBLE);
            reviewAdapter.addReviews(results);
        }
    }

    public void showMoviesView() {
        mNoInternetErrorMessage.setVisibility(View.INVISIBLE);
        mReviewRecyclerView.setVisibility(View.VISIBLE);
    }

    public void showErrorMessageView() {
        mNoInternetErrorMessage.setVisibility(View.VISIBLE);
        mReviewRecyclerView.setVisibility(View.INVISIBLE);
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
