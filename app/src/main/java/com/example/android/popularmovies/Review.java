package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gerdface on 3/30/17.
 */

public class Review implements Parcelable {

    private String movieId;
    private String reviewAuthor;
    private String reviewContent;

    public Review(String movieId, String reviewAuthor, String reviewContent) {
        this.movieId = movieId;
        this.reviewAuthor = reviewAuthor;
        this.reviewContent = reviewContent;
    }

    public String getMovieId() {
        return movieId;
    }

    public String getReviewAuthor() {
        return reviewAuthor;
    }

    public String getReviewContent() {
        return reviewContent;
    }

//    Parcelable Methods

    private Review(Parcel origin) {
        movieId = origin.readString();
        reviewAuthor = origin.readString();
        reviewContent = origin.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.movieId);
        dest.writeString(this.reviewAuthor);
        dest.writeString(this.reviewContent);
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

}
