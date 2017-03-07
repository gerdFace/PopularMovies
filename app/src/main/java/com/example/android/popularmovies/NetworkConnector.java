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
    private final static String TMDB_KEY = "";
    private final static String KEY = "api_key";

    public URL buildMovieUrl(String sortByUserPreferences) {
        Uri builtPopularMoviesUri = Uri.parse(TMDB_BASE_URL + sortByUserPreferences).buildUpon()
                .appendQueryParameter(KEY, TMDB_KEY)
                .build();

        return getMovieUrl(builtPopularMoviesUri);
    }

    @Nullable
    private URL getMovieUrl(Uri builtPopularMoviesUri) {
        URL url = null;
        try {
            url = new URL(builtPopularMoviesUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);
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
