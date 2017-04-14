package com.example.android.popularmovies.model;

public class MovieBuilder {

    private String id;
    private String title;
    private String poster;
    private String backdrop;
    private String voteAverage;
    private String overview;
    private String releaseDate;

    public MovieBuilder() {
    }

    public MovieBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public MovieBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public MovieBuilder withPoster(String poster) {
        this.poster = poster;
        return this;
    }

    public MovieBuilder withBackdrop(String backdrop) {
        this.backdrop = backdrop;
        return this;
    }

    public MovieBuilder withVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
        return this;
    }

    public MovieBuilder withOverview(String overview) {
        this.overview = overview;
        return this;
    }

    public MovieBuilder withReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
        return this;
    }

    public Movie create() {
        return new Movie(title, poster, backdrop, voteAverage, overview, releaseDate, id);
    }
}
