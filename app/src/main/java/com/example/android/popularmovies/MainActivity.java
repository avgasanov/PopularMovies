package com.example.android.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.android.popularmovies.MovieData.Movie;
import com.example.android.popularmovies.MovieData.MovieAdapter;
import com.example.android.popularmovies.MovieUtils.JsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static com.example.android.popularmovies.MovieUtils.NetworkUtils.*;



public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private MovieAdapter movieAdapter;
    private ArrayList<Movie> movieArrayList;

    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = findViewById(R.id.gridview);

        if(savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
            initializeMovieList(MOST_POPULAR_ORDER);
        } else {
            movieArrayList = savedInstanceState.getParcelableArrayList("movies");
        }

        movieAdapter = new MovieAdapter(this, movieArrayList);
        gridView.setAdapter(movieAdapter);

        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putParcelableArrayList("movies", movieArrayList);
        super.onSaveInstanceState(outState);
    }

    private void initializeMovieList(String sortOrder) {
        Movie[] movieArray = null;
        try {
            movieArray = new getMovieData().execute(sortOrder).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        if (movieArray != null) {
            movieArrayList = new ArrayList<>(Arrays.asList(movieArray));
        }
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
        int id = item.getItemId();
        switch (id) {
            case R.id.action_popular:
            {
                initializeMovieList(MOST_POPULAR_ORDER);
                movieAdapter = new MovieAdapter(this, movieArrayList);
                gridView.setAdapter(movieAdapter);
                Log.v("MENU", "action_popular");
                return true;
            }
            case R.id.action_rated:
            {
                initializeMovieList(TOP_RATED_ORDER);
                movieAdapter = new MovieAdapter(this, movieArrayList);
                gridView.setAdapter(movieAdapter);
                Log.v("MENU", "action_top_rated");
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    class getMovieData extends AsyncTask<String,Void,Movie[]> {

        @Override
        protected Movie[] doInBackground(String... strings) {
            String order = strings[0];
            Movie[] result;
            if (!order.equals(MOST_POPULAR_ORDER) && !order.equals(TOP_RATED_ORDER)) {
                return null;
            }
            URL jsonRequestUrl = buildUrl(order);
            try {
                String jsonResponse = getJSONResponseFromUrl(jsonRequestUrl);
                result = JsonUtils.parseMovieJson(jsonResponse);
                return result;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }


}
