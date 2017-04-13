package com.example.android.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by gerdface on 2/28/17.
 */

public class Movie implements Parcelable {
    private String id;
    private String title;
    private String poster;
    private String backdrop;
    private String voteAverage;
    private String overview;
    private String releaseDate;


    public Movie(String id, String title, String poster, String backdrop, String voteAverage, String overview, String releaseDate) {
        this.id = id;
        this.title = title;
        this.poster = poster;
        Log.v(TAG, "Added poster URL " + poster + " to movies");
        this.backdrop = backdrop;
        Log.v(TAG, "Added backdrop URL " + backdrop + " to movies");
        this.voteAverage = voteAverage;
        this.overview = overview;
        this.releaseDate = releaseDate;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPoster() {
        return poster;
    }

    public String getBackdrop() {
        return backdrop;
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


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(poster);
        dest.writeString(backdrop);
        dest.writeString(voteAverage);
        dest.writeString(overview);
        dest.writeString(releaseDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private Movie(Parcel origin) {
        id = origin.readString();
        title = origin.readString();
        poster = origin.readString();
        backdrop = origin.readString();
        voteAverage = origin.readString();
        overview = origin.readString();
        releaseDate = origin.readString();
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
