package com.example.android.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by gerdface on 2/28/17.
 */

public class Movie implements Parcelable {
    private String title;
    private String poster;
    private String voteAverage;
    private String overview;
    private String releaseDate;
    private String id;

    public Movie(String title, String poster, String voteAverage, String overview, String releaseDate, String id) {
        this.title = title;
        this.poster = poster;
        Log.v(TAG, "Added poster URL " + poster + " to movies");
        this.voteAverage = voteAverage;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getPoster() {
        return poster;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getId() {
        return id;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(poster);
        dest.writeString(voteAverage);
        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeString(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private Movie(Parcel origin) {
        title = origin.readString();
        poster = origin.readString();
        voteAverage = origin.readString();
        overview = origin.readString();
        releaseDate = origin.readString();
        id = origin.readString();
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel origin) {
            return new Movie(origin);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

}
