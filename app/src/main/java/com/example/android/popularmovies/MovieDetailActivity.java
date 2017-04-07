package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
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

import com.squareup.picasso.Picasso;
import java.net.URL;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailActivity extends AppCompatActivity implements TrailerAdapter.TrailerAdapterOnClickHandler, View.OnClickListener{

    private static final String TAG = MovieDetailActivity.class.getSimpleName();
    private TrailerAdapter trailerAdapter;

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

    @BindView(R.id.tv_read_reviews)
    TextView mTvReadReviews;

    @BindView(R.id.rv_trailers)
    RecyclerView mTrailerRecyclerView;

    @BindView(R.id.favorite_icon)
    ImageView mFavoriteIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        Context context = this;
        Intent intent = getIntent();
        Movie selectedMovie = intent.getParcelableExtra("movieDetail");
        String movieId = selectedMovie.getId();
        String movieTitle = selectedMovie.getTitle();
        String moviePoster = selectedMovie.getPoster();
        String movieVoteAverage = selectedMovie.getVoteAverage();
        String movieOverview = selectedMovie.getOverview();
        String movieReleaseDate = selectedMovie.getReleaseDate();

        mTvMovieTitle.setText(movieTitle);
            //  Credit Picasso library by Square, http://square.github.io/picasso/
        Picasso.with(context).load(moviePoster).into(mIvMoviePoster);
        mTvMovieAverageRating.setText(getString(R.string.average_rating_text) + movieVoteAverage + getString(R.string.rating_total));
        mTvMovieOverview.setText(movieOverview);
        mTvMovieReleaseDate.setText(movieReleaseDate);

        mTrailerRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mTrailerRecyclerView.setLayoutManager(layoutManager);
        trailerAdapter = new TrailerAdapter(this);
        mTrailerRecyclerView.setAdapter(trailerAdapter);
        new FetchTrailersTask().execute(movieId);
        setFavoriteIcon(movieId);
        mTvReadReviews.setOnClickListener(this);
        mFavoriteIcon.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = getIntent();
        Movie selectedMovie = intent.getParcelableExtra("movieDetail");
        String movieId = selectedMovie.getId();

        switch (v.getId()) {
            case R.id.tv_read_reviews:
                moveToReviewActivity(movieId);
                break;

            case R.id.favorite_icon:
                if (isMovieInFavoritesList(movieId)) {
                    int deletedRows = this.getContentResolver().delete(FavoritesProvider.Favorites
                        .CONTENT_URI,
                        FavoritesContract.MOVIE_ID + "=?",
                            new String[] {movieId});

                    if (deletedRows > 0) {
                        Log.v(TAG, "Number rows removed: " + deletedRows);
                        mFavoriteIcon.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        Toast.makeText(this, getResources().getString(R.string.favorite_removed_toast),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(FavoritesContract.MOVIE_ID, movieId);
                    contentValues.put(FavoritesContract.MOVIE_TITLE, selectedMovie.getTitle());
                    contentValues.put(FavoritesContract.MOVIE_ID, selectedMovie.getOverview());
                    contentValues.put(FavoritesContract.MOVIE_ID, selectedMovie.getPoster());
                    contentValues.put(FavoritesContract.MOVIE_ID, selectedMovie.getReleaseDate());
                    contentValues.put(FavoritesContract.MOVIE_ID, selectedMovie.getVoteAverage());

                    Uri newFavoriteUri = this.getContentResolver().insert(FavoritesProvider
                            .Favorites.CONTENT_URI, contentValues);

                    if (!newFavoriteUri.equals(Uri.EMPTY)) {
                        Log.v(TAG, "Uri added at: " + newFavoriteUri.toString());
                        mFavoriteIcon.setImageResource(R.drawable.ic_favorite_black_24dp);
                        Toast.makeText(this, getResources().getString(R.string.favorite_added_toast),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, getResources().getString(R.string.failed_to_add_favorite_toast),
                                Toast.LENGTH_SHORT).show();
                        throw new android.database.SQLException("Failed to insert at URI: " + newFavoriteUri);
                    }
                }
        }
    }

    @Override
    public void onClick(Trailer selectedTrailer, int shareOrPlay) {
        if (shareOrPlay == 0){
            shareTrailer(selectedTrailer);
        }
        if (shareOrPlay == 1) {
            playTrailer(selectedTrailer);
        }
    }

    private void setFavoriteIcon(String movieId) {
        if (isMovieInFavoritesList(movieId)) {
            mFavoriteIcon.setImageResource(R.drawable.ic_favorite_black_24dp);
        } else {
            mFavoriteIcon.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }
    }

    private boolean checkIfYouTubeAppIsInstalled() {
        String youTubePackage = "com.google.android.youtube";
        PackageManager packageManager = getPackageManager();
        try {
            packageManager.getPackageInfo(youTubePackage, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }

//    Credit: Roger Garzon Nieto
//    @ http://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent
    private void playTrailer(Trailer selectedTrailer) {
        if (checkIfYouTubeAppIsInstalled()) {
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" +
                    selectedTrailer.getTrailerKey()));
            startActivity(appIntent);
        } else {
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + selectedTrailer.getTrailerKey()));
            startActivity(webIntent);
        }
    }

    private void shareTrailer(Trailer selectedTrailer) {
        String youtubeUri = "https://www.youtube.com/watch?v=" + selectedTrailer.getTrailerKey();
        String mimeType = "text/plain";
        Intent shareTrailerIntent = new Intent()
                .setAction(Intent.ACTION_SEND)
                .setType(mimeType)
                .putExtra(Intent.EXTRA_TEXT, youtubeUri);
        startActivity(shareTrailerIntent);
    }

    private void moveToReviewActivity(String movieId) {
        Context context = this;
        Class destinationClassReviewActivity = ReviewActivity.class;
        Intent openReviewActivity = new Intent(context, destinationClassReviewActivity);
        openReviewActivity.putExtra("selected_movie", movieId.toString());
        startActivity(openReviewActivity);
    }


    private class FetchTrailersTask extends AsyncTask<String, Void, ArrayList<Trailer>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //            mLoadingProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Trailer> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String movieId = params[0];
            NetworkConnector networkConnector = new NetworkConnector();
            URL movieRequestUrl = networkConnector.buildTrailersUrl(movieId);

            try {
                String jsonMovieResponse = networkConnector.getResponseFromHttpUrl(movieRequestUrl);

                return JsonMovieDataExtractor.getExtractedTrailerStringsFromJson(jsonMovieResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Trailer> results) {
            trailerAdapter.addTrailers(results);
        }
    }

    private boolean isMovieInFavoritesList(String movieId) {
        Cursor cursor = getApplicationContext().getContentResolver()
                .query(FavoritesProvider.Favorites.CONTENT_URI, new String[]{FavoritesContract.MOVIE_ID},
                        null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String cursorMovieId = cursor.getString(cursor.getColumnIndex(FavoritesContract.MOVIE_ID));
            while (cursor.moveToNext()) {
                if (movieId.equals(cursorMovieId)) {
                    return true;
                }
            }
        }
        cursor.close();
        return false;
    }
}
