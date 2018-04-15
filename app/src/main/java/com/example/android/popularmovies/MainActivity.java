package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.MovieData.Movie;
import com.example.android.popularmovies.MovieData.MovieAdapter;
import com.example.android.popularmovies.MovieData.MovieDataRetrieverTask;
import com.example.android.popularmovies.MovieData.TaskCompleteListener;
import com.example.android.popularmovies.MovieUtils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;


import static com.example.android.popularmovies.MovieUtils.NetworkUtils.*;



public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ArrayList<Movie> movieArrayList;
    private Context context;

    private GridView gridView;
    private TextView errorView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        gridView = findViewById(R.id.gridview);
        errorView = findViewById(R.id.tv_error);
        progressBar = findViewById(R.id.pb_loading_indicator);

        gridView.setOnItemClickListener(this);

        if(savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
            if (!isConnected(context)) {
                showError();
            } else {
                showGrid();
                new MovieDataRetrieverTask(new FetchMyDataTaskCompleteListener(), progressBar).execute(MOST_POPULAR_ORDER);
            }
        } else {
            movieArrayList = savedInstanceState.getParcelableArrayList("movies");
            initializeAdapter();
        }


    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putParcelableArrayList("movies", movieArrayList);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Movie detailMovie = movieArrayList.get(position);
        Class detailActivity = DetailActivity.class;
        Intent detailIntent = new Intent(MainActivity.this, detailActivity);
        detailIntent.putExtra("movie", detailMovie);
        startActivity(detailIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!NetworkUtils.isConnected(context)) {
            showError();
            return true;
        }

        int id = item.getItemId();
        switch (id) {
            case R.id.action_popular:
            {
             new MovieDataRetrieverTask(new FetchMyDataTaskCompleteListener(), progressBar).execute(MOST_POPULAR_ORDER);
                return true;
            }
            case R.id.action_rated:
            {
                new MovieDataRetrieverTask(new FetchMyDataTaskCompleteListener(), progressBar).execute(TOP_RATED_ORDER);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeAdapter() {
        MovieAdapter movieAdapter = new MovieAdapter(this, movieArrayList);
        gridView.setAdapter(movieAdapter);
    }

    public class FetchMyDataTaskCompleteListener implements TaskCompleteListener<List<Movie>>
    {
        @Override
        public void onTaskComplete(List<Movie> result)
        {
            if (result == null) {
                showError();
            } else {
                showGrid();
                movieArrayList = new ArrayList<>(result);
                initializeAdapter();
            }
        }
    }

    private void showError() {
        gridView.setVisibility(View.INVISIBLE);
        errorView.setVisibility(View.VISIBLE);
    }

    private void showGrid() {
        errorView.setVisibility(View.INVISIBLE);
        gridView.setVisibility(View.VISIBLE);
    }
}
