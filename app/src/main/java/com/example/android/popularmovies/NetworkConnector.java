package com.example.android.popularmovies;

import android.net.Uri;
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

    final static String TMDB_BASE_URL = "https://api.themoviedb.org/3/movie/";
    final static String TMDB_KEY = "c7e6cb87a63c8cd9691cf319571b0581";
    final static String KEY = "api_key";

    public static URL buildMovieUrl (String sortByUserPreferences) {
        Uri builtPopularMoviesUri = Uri.parse(TMDB_BASE_URL + sortByUserPreferences).buildUpon()
                .appendQueryParameter(KEY, TMDB_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtPopularMoviesUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);
        return url;
    }


    public static String getResponseFromHttpUrl (URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
