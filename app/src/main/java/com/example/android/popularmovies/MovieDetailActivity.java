package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {

    TextView mTitle;
    ImageView mPoster;
    TextView mVoteAverage;
    TextView mOverview;
    TextView mReleaseDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Intent intent = getIntent();
        Movie selectedMovie = intent.getParcelableExtra("movieDetail");
        String movieTitle = selectedMovie.getTitle();
        String moviePoster = selectedMovie.getPoster();
        String movieVoteAverage = getString(R.string.average_rating) + selectedMovie.getVoteAverage() + getString(R.string.rating_total);
        String movieOverview = selectedMovie.getOverview();
        String movieReleaseDate = selectedMovie.getReleaseDate().substring(0, 4);
        Context context = this;

        mTitle = (TextView) findViewById(R.id.tv_movie_title);
        mPoster = (ImageView) findViewById(R.id.iv_movie_poster);
        mVoteAverage = (TextView) findViewById(R.id.tv_movie_average_rating);
        mOverview = (TextView) findViewById(R.id.tv_movie_overview);
        mReleaseDate = (TextView) findViewById(R.id.tv_movie_release_date);

        mTitle.setText(movieTitle);
        Picasso.with(context).load(moviePoster).into(mPoster);
        mVoteAverage.setText(movieVoteAverage);
        mOverview.setText(movieOverview);
        mReleaseDate.setText(movieReleaseDate);
    }
}
