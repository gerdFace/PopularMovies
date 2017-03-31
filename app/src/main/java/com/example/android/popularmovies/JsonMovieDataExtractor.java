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
    private static final String MOVIE_ARRAY = "results";
    private static final String MOVIE_TITLE = "original_title";
    private static final String POSTER_PATH = "poster_path";
    private static final String OVERVIEW = "overview";
    private static final String RELEASE_DATE = "release_date";
    private static final String VOTE_AVERAGE = "vote_average";
    private static final String MOVIE_ID = "id";
    private static final String TRAILER_ARRAY = "results";
    private static final String VIDEO_ID = "id";
    private static final String VIDEO_KEY = "key";
    private static final String VIDEO_NAME = "name";

    // TODO: Duplicate functionality inm getExtractedTrailerStringsFromJson
    public static ArrayList<Movie> getExtractedMovieStringsFromJson(String moviesJsonString) throws JSONException {
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
            String movieId = movie.getString(MOVIE_ID);
            Log.v(TAG, "Built Image URL " + poster);
            extractedMovieInfo.add(new Movie(title, poster, voteAverage, overview, releaseDate, movieId));
        }
        return extractedMovieInfo;
    }

    public static ArrayList<Trailer> getExtractedTrailerStringsFromJson(String trailerJsonString) throws JSONException {
        JSONObject trailerJson = new JSONObject(trailerJsonString);
        JSONArray trailerArray = trailerJson.getJSONArray(TRAILER_ARRAY);
        int trailerArrayLength = trailerArray.length();
        ArrayList<Trailer> extractedTrailerInfo = new ArrayList<>();

        for (int i = 0; i < trailerArrayLength; i++) {
            JSONObject trailer = trailerArray.getJSONObject(i);
            String videoId = trailer.getString(VIDEO_ID);
            String videoKey = trailer.getString(VIDEO_KEY);
            String movieId = trailer.getString(VIDEO_NAME);
            extractedTrailerInfo.add(new Trailer(videoId, videoKey, movieId));
        }
        return extractedTrailerInfo;
    }
}

