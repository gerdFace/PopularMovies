package com.example.android.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewActivity extends AppCompatActivity {

    private ReviewAdapter reviewAdapter;

    @BindView(R.id.tv_review_author)
    TextView mTvReviewAuthor;
    @BindView(R.id.tv_review_content)
    TextView mTvReviewContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        String selectedMovieId = intent.getStringExtra("selected_movie");
    }
}
