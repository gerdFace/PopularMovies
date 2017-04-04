package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gerdface on 3/30/17.
 */

public class Trailer implements Parcelable {

    private String movieId;
    private String trailerKey;
    private String trailerName;

    public Trailer(String movieId, String trailerKey, String trailerName) {
        this.movieId = movieId;
        this.trailerKey = trailerKey;
        this.trailerName = trailerName;
    }

    public String getMovieId() {
        return movieId;
    }

    public String getTrailerKey() {
        return trailerKey;
    }

    public String getTrailerName() {
        return trailerName;
    }

    //    Parcelable Methods

    private Trailer(Parcel origin) {
        movieId = origin.readString();
        trailerKey = origin.readString();
        trailerName = origin.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.movieId);
        dest.writeString(this.trailerKey);
        dest.writeString(this.trailerName);
    }

    public static final Parcelable.Creator<Trailer> CREATOR = new Parcelable.Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };
}
