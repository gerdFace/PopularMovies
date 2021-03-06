package com.example.android.popularmovies.model;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import com.example.android.popularmovies.data.FavoritesContract;
import com.example.android.popularmovies.data.FavoritesProvider;
import com.example.android.popularmovies.helper.Constants;
import static android.content.ContentValues.TAG;

public class MovieDetailViewModel {

    private Context context;
    private Movie selectedMovie;

    public MovieDetailViewModel(Movie selectedMovie, Context context) {
        this.context = context;
        this.selectedMovie = selectedMovie;
    }

    //    Credit: Roger Garzon Nieto
    //    @ http://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent
    public void playTrailer(Trailer selectedTrailer) {
        if (checkIfYouTubeAppIsInstalled()) {
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.YOUTUBE_APP_URI +
                                                                                selectedTrailer.getTrailerKey()));
            context.startActivity(appIntent);
        } else {
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                                          Uri.parse(Constants.YOUTUBE_PATH + selectedTrailer.getTrailerKey()));
            context.startActivity(webIntent);
        }
    }

    public void shareTrailer(Trailer selectedTrailer) {
        String youtubeUri = Constants.YOUTUBE_PATH + selectedTrailer.getTrailerKey();
        String mimeType = "text/plain";
        Intent shareTrailerIntent = new Intent()
                .setAction(Intent.ACTION_SEND)
                .setType(mimeType)
                .putExtra(Intent.EXTRA_TEXT, youtubeUri);
        context.startActivity(shareTrailerIntent);
    }

    private boolean checkIfYouTubeAppIsInstalled() {
        PackageManager packageManager = context.getPackageManager();
        try {
            packageManager.getPackageInfo(Constants.YOUTUBE_PACKAGE, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean isMovieInFavoritesList(String movieId) {
        Cursor cursor = context.getContentResolver()
                .query(FavoritesProvider.Favorites.CONTENT_URI, new String[]{FavoritesContract.MOVIE_ID},
                       null, null, null);

        if (cursor.moveToFirst()) {
            String cursorMovieId = cursor.getString(cursor.getColumnIndex(FavoritesContract.MOVIE_ID));
            if (movieId.equals(cursorMovieId)) {
                return true;
            } else {
                while (cursor.moveToNext()) {
                    cursorMovieId = cursor.getString(cursor.getColumnIndex(FavoritesContract.MOVIE_ID));
                    if (movieId.equals(cursorMovieId)) {
                        return true;
                    }
                }
            }
        }
        cursor.close();
        return false;
    }

    public void deleteMovieFromFavorites() {
        int deletedRows = context.getContentResolver().delete(FavoritesProvider.Favorites
                  .CONTENT_URI,
                  FavoritesContract.MOVIE_ID + "=?",
                  new String[] {selectedMovie.getId()});
        if (deletedRows > 0) {
            Log.v(TAG, "Number rows removed: " + deletedRows);
        } else {
            Log.v(TAG, "Failed to delete number rows: " + deletedRows);
        }
    }

    public void addMovieToFavorites() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FavoritesContract.MOVIE_ID, selectedMovie.getId());
        contentValues.put(FavoritesContract.MOVIE_TITLE, selectedMovie.getTitle());
        contentValues.put(FavoritesContract.POSTER_PATH, selectedMovie.getPoster());
        contentValues.put(FavoritesContract.BACKDROP_PATH, selectedMovie.getBackdrop());
        contentValues.put(FavoritesContract.USER_RATING, selectedMovie.getVoteAverage());
        contentValues.put(FavoritesContract.OVERVIEW, selectedMovie.getOverview());
        contentValues.put(FavoritesContract.RELEASE_DATE, selectedMovie.getReleaseDate());

        Uri newFavoriteUri = context.getContentResolver().insert(FavoritesProvider.Favorites.CONTENT_URI, contentValues);
        if (!newFavoriteUri.equals(Uri.EMPTY)) {
            Log.v(TAG, "Uri added at: " + newFavoriteUri.toString());
        } else {
            throw new android.database.SQLException("Failed to insert at URI: " + newFavoriteUri);
        }
    }
}
