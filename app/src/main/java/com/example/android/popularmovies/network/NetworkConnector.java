package com.example.android.popularmovies.network;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import static android.content.ContentValues.TAG;

public class NetworkConnector {

    public URL buildMovieUrl(String sortByUserPreferences) {
        Uri builtPopularMoviesUri = Uri.parse(Constants.TMDB_BASE_URL + sortByUserPreferences).buildUpon()
                                       .appendQueryParameter(Constants.KEY, Constants.TMDB_KEY)
                                       .build();
        return getMovieUrl(builtPopularMoviesUri);
    }

    public URL buildUrl(ArrayList<String> appendPathParameters) {
        Uri.Builder builtTrailerUri = Uri.parse(Constants.TMDB_BASE_URL).buildUpon();

        for (String entry : appendPathParameters) {
            builtTrailerUri.appendPath(entry);
        }

        builtTrailerUri.appendQueryParameter(Constants.KEY, Constants.TMDB_KEY);

        return getMovieUrl(builtTrailerUri.build());
    }

    public URL buildReviewsUrl(String movieId) {
        Uri builtReviewsUri = Uri.parse(Constants.TMDB_BASE_URL).buildUpon()
                                 .appendPath(movieId)
                                 .appendPath(Constants.REVIEWS)
                                 .appendQueryParameter(Constants.KEY, Constants.TMDB_KEY)
                                 .build();

        return getMovieUrl(builtReviewsUri);
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
            return Constants.ERROR_CANNOT_CONNECT_TO_THE_MOVIE_DATABASE;
        }
    }
}
