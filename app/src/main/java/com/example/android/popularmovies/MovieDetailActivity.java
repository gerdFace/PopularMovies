package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.example.android.popularmovies.model.Movie;
import static com.example.android.popularmovies.helper.Constants.SELECTED_MOVIE_TAG;

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        if (savedInstanceState == null) {
            MovieDetailFragment fragment = MovieDetailFragment.newInstance((Movie) getIntent()
                    .getParcelableExtra(SELECTED_MOVIE_TAG));
            getSupportFragmentManager().beginTransaction().add(R.id.detail_container, fragment).commit();
        }
    }
}
