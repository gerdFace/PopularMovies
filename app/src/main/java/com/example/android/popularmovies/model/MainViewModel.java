package com.example.android.popularmovies.model;

import android.content.Context;
import android.database.Cursor;
import com.example.android.popularmovies.data.FavoritesContract;
import com.example.android.popularmovies.data.FavoritesProvider;
import com.example.android.popularmovies.helper.Constants;
import java.util.ArrayList;

public class MainViewModel {

    private Context context;

    public MainViewModel(Context context) {
        this.context = context;
    }

    public String getSortOrderString(String sortPreferenceString) {
        String sortPreference;
        if (sortPreferenceString.isEmpty()) {
            sortPreference = Constants.POPULAR_SORT;
        } else {
            sortPreference = sortPreferenceString;
        }
        return sortPreference;
    }

    public ArrayList<Movie> queryFavoriteDatabase() {
        ArrayList<Movie> favoriteMovieList = new ArrayList<>();
        Cursor cursor = context.getContentResolver()
                .query(FavoritesProvider.Favorites.CONTENT_URI, null, null, null, null);
        if (cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                Movie favoriteMovie = new Movie(
                        cursor.getString(cursor.getColumnIndex(FavoritesContract.MOVIE_ID)),
                        cursor.getString(cursor.getColumnIndex(FavoritesContract.MOVIE_TITLE)),
                        cursor.getString(cursor.getColumnIndex(FavoritesContract.POSTER_PATH)),
                        cursor.getString(cursor.getColumnIndex(FavoritesContract.BACKDROP_PATH)),
                        cursor.getString(cursor.getColumnIndex(FavoritesContract.USER_RATING)),
                        cursor.getString(cursor.getColumnIndex(FavoritesContract.OVERVIEW)),
                        cursor.getString(cursor.getColumnIndex(FavoritesContract.RELEASE_DATE))
                );
                favoriteMovieList.add(favoriteMovie);
            }
            cursor.close();
            return favoriteMovieList;
        } else {
            return null;
        }
    }

}
