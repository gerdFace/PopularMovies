package com.example.android.popularmovies;

import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.android.popularmovies.adapter.ReviewAdapter;
import com.example.android.popularmovies.adapter.TrailerAdapter;
import com.example.android.popularmovies.data.JsonMovieDataExtractor;
import com.example.android.popularmovies.helper.Constants;
import com.example.android.popularmovies.helper.HttpPathListCreator;
import com.example.android.popularmovies.helper.TestInternetConnectivity;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.MovieDetailViewModel;
import com.example.android.popularmovies.model.Review;
import com.example.android.popularmovies.model.Trailer;
import com.example.android.popularmovies.network.NetworkConnector;
import com.squareup.picasso.Picasso;
import java.net.URL;
import java.util.ArrayList;

public class MovieDetailFragment extends Fragment implements TrailerAdapter.TrailerAdapterOnClickHandler, View.OnClickListener{
    private static final String TAG = MovieDetailActivity.class.getSimpleName();
    private Movie selectedMovie;
    private static String movieId;
    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;
    private MovieDetailViewModel movieDetailViewModel;
    TextView mOverViewHeader;
    TextView mTrailerHeader;
    TextView mReviewHeader;
    TextView mNoInternetErrorMessage;
    TextView mNoInternetErrorMessageTrailers;
    TextView mTvMovieTitle;
    TextView mTvMovieReleaseDate;
    TextView mTvMovieAverageRating;
    TextView mTvMovieOverview;
    TextView mTvNoReviews;
    TextView mTvNoTrailers;
    RecyclerView mTrailerRecyclerView;
    RecyclerView mReviewRecyclerView;
    ProgressBar mLoadingProgressBar;
    ImageView mFavoriteIcon;
    ImageView mBackdropImage;

    public MovieDetailFragment() {

    }

    public static MovieDetailFragment newInstance(Movie selectedMovie) {
        MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.SELECTED_MOVIE_TAG, selectedMovie);
        movieDetailFragment.setArguments(bundle);
        return movieDetailFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        selectedMovie = getArguments().getParcelable(Constants.SELECTED_MOVIE_TAG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = DataBindingUtil.inflate(inflater, R.layout.fragment_movie_detail, container, false).getRoot();

        mOverViewHeader = (TextView) rootView.findViewById(R.id.overview_header);
        mTrailerHeader = (TextView) rootView.findViewById(R.id.trailer_header);
        mReviewHeader = (TextView) rootView.findViewById(R.id.review_header);
        mNoInternetErrorMessage = (TextView) rootView.findViewById(R.id.tv_no_internet_error_message);
        mNoInternetErrorMessageTrailers = (TextView) rootView.findViewById(R.id.tv_no_internet_error_message_trailers);
        mTvMovieTitle = (TextView) rootView.findViewById(R.id.tv_movie_title);
        mTvMovieReleaseDate = (TextView) rootView.findViewById(R.id.tv_movie_release_date);
        mTvMovieAverageRating = (TextView) rootView.findViewById(R.id.tv_movie_average_rating);
        mTvMovieOverview = (TextView) rootView.findViewById(R.id.tv_movie_overview);
        mTvNoReviews = (TextView) rootView.findViewById(R.id.no_reviews_found);
        mTvNoTrailers = (TextView) rootView.findViewById(R.id.no_trailers_found);
        mTrailerRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_trailers);
        mReviewRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_reviews);
        mLoadingProgressBar = (ProgressBar) rootView.findViewById(R.id.pb_loading_indicator);
        mFavoriteIcon = (ImageView) rootView.findViewById(R.id.favorite_icon);
        mBackdropImage = (ImageView) rootView.findViewById(R.id.backdrop_image);

        movieId = selectedMovie.getId();

        //  Credit Picasso library by Square, http://square.github.io/picasso/
        Picasso.with(getActivity()).load(selectedMovie.getBackdrop()).into(mBackdropImage);
        mTvMovieTitle.setText(selectedMovie.getTitle());
        mTvMovieAverageRating.setText(getString(R.string.average_rating_text) + selectedMovie.getVoteAverage() + getString(R.string.rating_total));
        mTvMovieOverview.setText(selectedMovie.getOverview());
        mTvMovieReleaseDate.setText(selectedMovie.getReleaseDate());

        movieDetailViewModel = new MovieDetailViewModel(selectedMovie, getActivity());

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setFavoriteIcon(movieDetailViewModel.isMovieInFavoritesList(movieId));

        mFavoriteIcon.setOnClickListener(this);

        LinearLayoutManager trailerLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mTrailerRecyclerView.setHasFixedSize(true);
        mTrailerRecyclerView.setLayoutManager(trailerLayoutManager);
        trailerAdapter = new TrailerAdapter(getActivity(), this);
        mTrailerRecyclerView.setAdapter(trailerAdapter);

        LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mReviewRecyclerView.setHasFixedSize(true);
        mReviewRecyclerView.setLayoutManager(reviewLayoutManager);
        reviewAdapter = new ReviewAdapter();
        mReviewRecyclerView.setAdapter(reviewAdapter);

        checkIfDeviceIsConnectedToInternet();
    }

    private void checkIfDeviceIsConnectedToInternet() {
        if (TestInternetConnectivity.isDeviceOnline(getActivity())) {
            new FetchTrailersTask().execute(new HttpPathListCreator().createListForHttpPath(Constants.MOVIES, movieId, Constants.TRAILERS));
            new FetchReviewTask().execute(new HttpPathListCreator().createListForHttpPath(Constants.MOVIES, movieId, Constants.REVIEWS));
        } else {
            Log.v(TAG, "Can't load, no internet connection");
            showErrorMessageView();
        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.favorite_icon) {
            boolean isFavorite = false;
            if (movieDetailViewModel.isMovieInFavoritesList(movieId)) {
                movieDetailViewModel.deleteMovieFromFavorites();
                isFavorite = false;
                Toast.makeText(getActivity(), getResources().getString(R.string.favorite_removed_toast),
                               Toast.LENGTH_SHORT).show();
            } else if (!movieDetailViewModel.isMovieInFavoritesList(movieId)) {
                movieDetailViewModel.addMovieToFavorites();
                isFavorite = true;
                Toast.makeText(getActivity(), getResources().getString(R.string.favorite_added_toast),
                               Toast.LENGTH_SHORT).show();
            }
            setFavoriteIcon(isFavorite);
        }
    }

    @Override
    public void onPlayTrailerClicked(Trailer selectedTrailer) {
        movieDetailViewModel.playTrailer(selectedTrailer);
    }

    @Override
    public void onShareTrailerClicked(Trailer selectedTrailer) {
        movieDetailViewModel.shareTrailer(selectedTrailer);
    }

    private void setFavoriteIcon(boolean isFavoriteMovie) {
        if (isFavoriteMovie) {
            mFavoriteIcon.setImageResource(R.drawable.ic_favorite_black_48dp);
        } else {
            mFavoriteIcon.setImageResource(R.drawable.ic_favorite_border_black_48dp);
        }
    }

    private class FetchTrailersTask extends AsyncTask<ArrayList<String>, Void, ArrayList<Trailer>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Trailer> doInBackground(ArrayList<String>... trailerPath) {

            if (trailerPath.length == 0) {
                return null;
            }

            NetworkConnector networkConnector = new NetworkConnector();
            URL movieRequestUrl = networkConnector.buildUrl(trailerPath[0]);

            try {
                String jsonMovieResponse = networkConnector.getResponseFromHttpUrl(movieRequestUrl);

                return new JsonMovieDataExtractor().getExtractedTrailerStringsFromJson(jsonMovieResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Trailer> results) {
            if (results != null && !results.isEmpty()) {
                trailerAdapter.addTrailers(results);
                showTrailersView();
            } else {
                showNoTrailersView();
            }
        }
    }

    private class FetchReviewTask extends AsyncTask<ArrayList<String>, Void, ArrayList<Review>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Review> doInBackground(ArrayList<String>... reviewPath) {

            if (reviewPath.length == 0) {
                return null;
            }

            NetworkConnector networkConnector = new NetworkConnector();
            URL reviewRequestUrl = networkConnector.buildUrl(reviewPath[0]);

            try {
                String jsonMovieResponse = networkConnector.getResponseFromHttpUrl(reviewRequestUrl);

                return new JsonMovieDataExtractor().getExtractedReviewStringsFromJson(jsonMovieResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Review> results) {
            if (results != null && !results.isEmpty()) {
                reviewAdapter.addReviews(results);
                showReviewsView();
            } else {
                showNoReviewsView();
            }
        }
    }

    public void showReviewsView() {
        mReviewRecyclerView.setVisibility(View.VISIBLE);
        mTvNoReviews.setVisibility(View.INVISIBLE);
        mNoInternetErrorMessage.setVisibility(View.INVISIBLE);
        mNoInternetErrorMessageTrailers.setVisibility(View.INVISIBLE);
    }

    private void showTrailersView() {
        mTrailerRecyclerView.setVisibility(View.VISIBLE);
        mNoInternetErrorMessage.setVisibility(View.INVISIBLE);
        mNoInternetErrorMessageTrailers.setVisibility(View.INVISIBLE);
        mTvNoTrailers.setVisibility(View.INVISIBLE);
    }

    public void showErrorMessageView() {
        mNoInternetErrorMessage.setVisibility(View.VISIBLE);
        mNoInternetErrorMessageTrailers.setVisibility(View.VISIBLE);
        mReviewRecyclerView.setVisibility(View.INVISIBLE);
        mTrailerRecyclerView.setVisibility(View.INVISIBLE);
        mTvNoReviews.setVisibility(View.INVISIBLE);
        mTvNoTrailers.setVisibility(View.INVISIBLE);
    }

    private void showNoReviewsView() {
        mReviewRecyclerView.setVisibility(View.INVISIBLE);
        mTvNoReviews.setVisibility(View.VISIBLE);
    }

    private void showNoTrailersView() {
        mTrailerRecyclerView.setVisibility(View.INVISIBLE);
        mTvNoTrailers.setVisibility(View.VISIBLE);
    }
}
