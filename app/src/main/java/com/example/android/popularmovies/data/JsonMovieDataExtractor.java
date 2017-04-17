package com.example.android.popularmovies.data;

import android.util.Log;
import com.example.android.popularmovies.helper.Constants;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.Review;
import com.example.android.popularmovies.model.Trailer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import static android.content.ContentValues.TAG;

public class JsonMovieDataExtractor {

    public ArrayList<Movie> getExtractedMovieStringsFromJson(String moviesJsonString) throws JSONException {
        JSONObject moviesJson = new JSONObject(moviesJsonString);
        JSONArray moviesArray = moviesJson.getJSONArray(Constants.MOVIE_ARRAY);
        int moviesArrayLength = moviesArray.length();
        ArrayList<Movie> extractedMovieInfo = new ArrayList<>();

        for (int i = 0; i < moviesArrayLength; i++) {
            JSONObject movie = moviesArray.getJSONObject(i);
            String movieId = movie.getString(Constants.MOVIE_ID);
            String title = movie.getString(Constants.MOVIE_TITLE);
            String poster = Constants.POSTER_BASE_PATH + Constants.POSTER_SIZE + movie.getString(Constants.POSTER_PATH);
            String backdrop = Constants.POSTER_BASE_PATH + Constants.BACKDROP_SIZE + movie.getString(Constants.BACKDROP_PATH);
            String voteAverage = movie.getString(Constants.VOTE_AVERAGE);
            String overview = movie.getString(Constants.OVERVIEW);
            String releaseDate = movie.getString(Constants.RELEASE_DATE).substring(0, 4);
            Log.v(TAG, "Built Image URL " + poster);
            Log.v(TAG, "Built Backdrop URL " + backdrop);
            extractedMovieInfo.add(new Movie(movieId, title, poster, backdrop, voteAverage, overview, releaseDate));
        }
        return extractedMovieInfo;
    }

    public ArrayList<Trailer> getExtractedTrailerStringsFromJson(String trailerJsonString) throws JSONException {
        JSONObject trailerJson = new JSONObject(trailerJsonString);
        JSONArray trailerArray = trailerJson.getJSONArray(Constants.MOVIE_ARRAY);
        int trailerArrayLength = trailerArray.length();
        ArrayList<Trailer> extractedTrailerInfo = new ArrayList<>();

        for (int i = 0; i < trailerArrayLength; i++) {
            JSONObject trailer = trailerArray.getJSONObject(i);
            String movieId = trailer.getString(Constants.MOVIE_ID);
            String trailerKey = trailer.getString(Constants.VIDEO_KEY);
            String trailerName = trailer.getString(Constants.VIDEO_NAME);
            extractedTrailerInfo.add(new Trailer(movieId, trailerKey, trailerName));
        }
        return extractedTrailerInfo;
    }

    public ArrayList<Review> getExtractedReviewStringsFromJson(String reviewJsonString) throws JSONException {
        JSONObject reviewJson = new JSONObject(reviewJsonString);
        JSONArray reviewArray = reviewJson.getJSONArray(Constants.MOVIE_ARRAY);
        int reviewArrayLength = reviewArray.length();
        ArrayList<Review> extractedReviewInfo = new ArrayList<>();

        for (int i = 0; i < reviewArrayLength; i++) {
            JSONObject review = reviewArray.getJSONObject(i);
            String movieId = review.getString(Constants.MOVIE_ID);
            String reviewAuthor = review.getString(Constants.REVIEW_AUTHOR);
            String reviewContent = review.getString(Constants.REVIEW_CONTENT);
            extractedReviewInfo.add(new Review(movieId, reviewAuthor, reviewContent));
        }
        return extractedReviewInfo;
    }
}

