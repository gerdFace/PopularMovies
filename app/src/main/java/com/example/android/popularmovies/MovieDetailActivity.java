package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.android.popularmovies.adapter.TrailerAdapter;
import com.example.android.popularmovies.data.JsonMovieDataExtractor;
import com.example.android.popularmovies.helper.TestInternetConnectivity;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.MovieDetailViewModel;
import com.example.android.popularmovies.model.Trailer;
import com.example.android.popularmovies.helper.Constants;
import com.example.android.popularmovies.helper.HttpPathListCreator;
import com.example.android.popularmovies.network.NetworkConnector;
import com.squareup.picasso.Picasso;
import java.net.URL;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailActivity extends AppCompatActivity implements TrailerAdapter.TrailerAdapterOnClickHandler, View.OnClickListener{

    private static final String TAG = MovieDetailActivity.class.getSimpleName();
    private TrailerAdapter trailerAdapter;

//    @BindView(R.id.iv_movie_poster)
//    ImageView mIvMoviePoster;

    @BindView(R.id.tv_movie_title)
    TextView mTvMovieTitle;

    @BindView(R.id.tv_movie_release_date)
    TextView mTvMovieReleaseDate;

    @BindView(R.id.tv_movie_average_rating)
    TextView mTvMovieAverageRating;

    @BindView(R.id.tv_movie_overview)
    TextView mTvMovieOverview;

    @BindView(R.id.tv_read_reviews)
    TextView mTvReadReviews;

    @BindView(R.id.rv_trailers)
    RecyclerView mTrailerRecyclerView;

    @BindView(R.id.favorite_icon)
    ImageView mFavoriteIcon;

    @BindView(R.id.backdrop_image)
    ImageView mBackdropImage;

    private MovieDetailViewModel movieDetailViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DataBindingUtil.setContentView(this, R.layout.activity_movie_detail);

        ButterKnife.bind(this);

//        Context context = this;

        Intent intent = getIntent();
        Movie selectedMovie = intent.getParcelableExtra("movieDetail");
        String movieId = selectedMovie.getId();
        String movieTitle = selectedMovie.getTitle();
//        String moviePoster = selectedMovie.getPoster();
        String movieBackdrop = selectedMovie.getBackdrop();
        String movieVoteAverage = selectedMovie.getVoteAverage();
        String movieOverview = selectedMovie.getOverview();
        String movieReleaseDate = selectedMovie.getReleaseDate();

        movieDetailViewModel = new MovieDetailViewModel(selectedMovie, this);

        setFavoriteIcon(movieDetailViewModel.isMovieInFavoritesList(movieId));

        Picasso.with(this).load(movieBackdrop).into(mBackdropImage);
        mTvMovieTitle.setText(movieTitle);
//        Picasso.with(context).load(moviePoster).into(mIvMoviePoster);  //  Credit Picasso library by Square, http://square.github.io/picasso/
        mTvMovieAverageRating.setText(getString(R.string.average_rating_text) + movieVoteAverage + getString(R.string.rating_total));
        mTvMovieOverview.setText(movieOverview);
        mTvMovieReleaseDate.setText(movieReleaseDate);
        setTitle(selectedMovie.getTitle());

        mTrailerRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mTrailerRecyclerView.setLayoutManager(layoutManager);

        trailerAdapter = new TrailerAdapter(this, this);

        mTrailerRecyclerView.setAdapter(trailerAdapter);

        mTvReadReviews.setOnClickListener(this);
        mFavoriteIcon.setOnClickListener(this);

        loadTrailers(new HttpPathListCreator().createListForHttpPath(Constants.MOVIES, movieId, Constants.TRAILERS));
    }

    private void loadTrailers(ArrayList<String> trailerPath) {
        if (TestInternetConnectivity.isDeviceOnline(this)) {
            new FetchTrailersTask().execute(trailerPath);
        } else {
            Log.v(TAG, "Can't load, no internet connection");
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = getIntent();
        Movie selectedMovie = intent.getParcelableExtra("movieDetail");
        String movieId = selectedMovie.getId();

        switch (v.getId()) {
            case R.id.tv_read_reviews:
                movieDetailViewModel.moveToReviewActivity(movieId);
                break;

            case R.id.favorite_icon:
                if (movieDetailViewModel.isMovieInFavoritesList(movieId)) {
                    setFavoriteIcon(movieDetailViewModel.deleteMovieFromFavorites(movieId));
                    Toast.makeText(this, getResources().getString(R.string.favorite_removed_toast),
                                   Toast.LENGTH_SHORT).show();
                } else {
                    setFavoriteIcon(movieDetailViewModel.addMovieToFavorites(movieId));
                    Toast.makeText(this, getResources().getString(R.string.favorite_added_toast),
                                   Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onPlayTrailerClicked(Trailer selectedTrailer) {
        movieDetailViewModel.playTrailer(selectedTrailer);
    }

    @Override
    public void onShareTrailerClicked(Trailer selectedTrailer) {
        movieDetailViewModel.shareTrailer(selectedTrailer);
    }

    private void setFavoriteIcon(boolean isFavoriteMovie) {
        if (isFavoriteMovie) {
            mFavoriteIcon.setImageResource(R.drawable.ic_favorite_black_24dp);
        } else {
            mFavoriteIcon.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }
    }

    private class FetchTrailersTask extends AsyncTask<ArrayList<String>, Void, ArrayList<Trailer>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Trailer> doInBackground(ArrayList<String>... trailerPath) {

            if (trailerPath.length == 0) {
                return null;
            }

            NetworkConnector networkConnector = new NetworkConnector();
            URL movieRequestUrl = networkConnector.buildUrl(trailerPath[0]);

            try {
                String jsonMovieResponse = networkConnector.getResponseFromHttpUrl(movieRequestUrl);

                return new JsonMovieDataExtractor().getExtractedTrailerStringsFromJson(jsonMovieResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Trailer> results) {
            if (results != null) {
                trailerAdapter.addTrailers(results);
            }
        }
    }
}
