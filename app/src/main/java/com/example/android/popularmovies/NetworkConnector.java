package com.example.android.popularmovies;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import static android.content.ContentValues.TAG;

/**
 * Created by gerdface on 2/27/17.
 */

public class NetworkConnector {

    public static final String ERROR_CANNOT_CONNECT_TO_THE_MOVIE_DATABASE = "Error: cannot connect to the movie database";
    private final static String TMDB_BASE_URL = "https://api.themoviedb.org/3/movie/";
    private final static String APPENDED_TRAILER_PATH = "videos";
    private final static String TMDB_KEY = "c7e6cb87a63c8cd9691cf319571b0581";
    private final static String KEY = "api_key";
    private final static String APPENDED_REVIEW_PATH = "reviews";


    public URL buildMovieUrl(String sortByUserPreferences) {
        Uri builtPopularMoviesUri = Uri.parse(TMDB_BASE_URL + sortByUserPreferences).buildUpon()
                                       .appendQueryParameter(KEY, TMDB_KEY)
                                       .build();

        return getMovieUrl(builtPopularMoviesUri);
    }

    public URL buildReviewsUrl(String movieId) {
        Uri builtReviewsUri = Uri.parse(TMDB_BASE_URL).buildUpon()
                                 .appendPath(movieId)
                                 .appendPath(APPENDED_REVIEW_PATH)
                                 .appendQueryParameter(KEY, TMDB_KEY)
                                 .build();

        return getMovieUrl(builtReviewsUri);
    }

    public URL buildTrailersUrl(String movieId) {
        Uri builtTrailerUri = Uri.parse(TMDB_BASE_URL).buildUpon()
                                 .appendPath(movieId)
                                 .appendPath(APPENDED_TRAILER_PATH)
                                 .appendQueryParameter(KEY, TMDB_KEY)
                                 .build();

        return getMovieUrl(builtTrailerUri);
    }

    @Nullable
    private URL getMovieUrl(Uri builtMovieUri) {
        URL url = null;
        try {
            url = new URL(builtMovieUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URL " + url);
        return url;
    }


    public static String getResponseFromHttpUrl(URL url) {
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            return scanner.next();
        } catch (IOException e) {
            e.printStackTrace();
            return ERROR_CANNOT_CONNECT_TO_THE_MOVIE_DATABASE;
        }
    }
}
