package com.example.android.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.MovieData.Movie;
import com.example.android.popularmovies.MovieUtils.NetworkUtils;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent parentIntent = this.getIntent();

        ImageView poster = findViewById(R.id.iv_thumbnail);
        TextView title = findViewById(R.id.tv_title);
        TextView release_date = findViewById(R.id.tv_release_date);
        TextView average_rate = findViewById(R.id.tv_average_rate);
        TextView synopsis = findViewById(R.id.tv_synopsis);

        if(parentIntent.hasExtra("movie")) {
            Movie detailMovie =  parentIntent.getParcelableExtra("movie");
            String posterURL = detailMovie.getPoster(NetworkUtils.IMAGE_SIZE_STANDART);
            String titleStr = detailMovie.getTitle();
            String overviewStr = detailMovie.getSynopsis();
            Double averageRate = detailMovie.getAverage_rate();
            String releaseDateStr = detailMovie.getRelease_date();

            title.setText(titleStr);
            release_date.setText(releaseDateStr);
            average_rate.setText(String.valueOf(averageRate));
            synopsis.setText(overviewStr);
            Picasso.with(this).load(posterURL).into(poster);
        }
    }
}
