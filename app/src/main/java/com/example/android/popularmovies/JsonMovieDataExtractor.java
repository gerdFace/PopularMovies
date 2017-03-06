package com.example.android.popularmovies;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import static android.content.ContentValues.TAG;

/**
 * Created by gerdface on 2/28/17.
 */

public class JsonMovieDataExtractor {

    private static final String POSTER_BASE_PATH = "http://image.tmdb.org/t/p/";
    private static final String POSTER_SIZE = "w500";

    public static ArrayList<Movie> getExtractedMovieStringsFromJson(String moviesJsonString) throws JSONException {
        final String MOVIE_ARRAY = "results";
        final String MOVIE_TITLE = "original_title";
        final String POSTER_PATH = "poster_path";
        final String OVERVIEW = "overview";
        final String RELEASE_DATE = "release_date";
        final String VOTE_AVERAGE = "vote_average";


        JSONObject moviesJson = new JSONObject(moviesJsonString);
        JSONArray moviesArray = moviesJson.getJSONArray(MOVIE_ARRAY);
        int moviesArrayLength = moviesArray.length();
        ArrayList<Movie> extractedMovieInfo = new ArrayList<>();

        for (int i = 0; i < moviesArrayLength; i++) {
            JSONObject movie = moviesArray.getJSONObject(i);
            String title = movie.getString(MOVIE_TITLE);
            String poster = POSTER_BASE_PATH + POSTER_SIZE + movie.getString(POSTER_PATH);
            String voteAverage = movie.getString(VOTE_AVERAGE);
            String overview = movie.getString(OVERVIEW);
            String releaseDate = movie.getString(RELEASE_DATE).substring(0, 4);
            Log.v(TAG, "Built Image URL " + poster);
            extractedMovieInfo.add(new Movie(title, poster, voteAverage, overview, releaseDate));
        }
        return extractedMovieInfo;
    }
}

