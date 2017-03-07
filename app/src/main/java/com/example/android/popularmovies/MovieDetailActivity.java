package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailActivity extends AppCompatActivity {

    @BindView(R.id.iv_movie_poster)
    ImageView mIvMoviePoster;
    @BindView(R.id.tv_movie_title)
    TextView mTvMovieTitle;
    @BindView(R.id.tv_movie_release_date)
    TextView mTvMovieReleaseDate;
    @BindView(R.id.tv_movie_average_rating)
    TextView mTvMovieAverageRating;
    @BindView(R.id.tv_movie_overview)
    TextView mTvMovieOverview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Movie selectedMovie = intent.getParcelableExtra("movieDetail");
        String movieTitle = selectedMovie.getTitle();
        String moviePoster = selectedMovie.getPoster();
        String movieVoteAverage = selectedMovie.getVoteAverage();
        String movieOverview = selectedMovie.getOverview();
        String movieReleaseDate = selectedMovie.getReleaseDate();
        Context context = this;

        mTvMovieTitle.setText(movieTitle);
//        Credit Picasso library by Square, http://square.github.io/picasso/
        Picasso.with(context).load(moviePoster).into(mIvMoviePoster);
        mTvMovieAverageRating.setText(getString(R.string.average_rating_text) + movieVoteAverage + getString(R.string.rating_total));
        mTvMovieOverview.setText(movieOverview);
        mTvMovieReleaseDate.setText(movieReleaseDate);
    }
}
