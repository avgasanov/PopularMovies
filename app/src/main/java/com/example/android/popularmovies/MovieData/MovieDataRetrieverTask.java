package com.example.android.popularmovies.MovieData;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.example.android.popularmovies.MovieUtils.JsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static com.example.android.popularmovies.MovieUtils.NetworkUtils.MOST_POPULAR_ORDER;
import static com.example.android.popularmovies.MovieUtils.NetworkUtils.TOP_RATED_ORDER;
import static com.example.android.popularmovies.MovieUtils.NetworkUtils.buildUrl;
import static com.example.android.popularmovies.MovieUtils.NetworkUtils.getJSONResponseFromUrl;

public class MovieDataRetrieverTask extends AsyncTask<String,Void,Movie[]> {

    private final TaskCompleteListener<List<Movie>> listener;
    private final ProgressBar pb;

    @Override
    protected void onPreExecute() {
        pb.setVisibility(View.VISIBLE);
    }

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
            return null;
        }
    }

    public MovieDataRetrieverTask(TaskCompleteListener<List<Movie>> listener, ProgressBar pb) {
        this.listener = listener;
        this.pb = pb;
    }

    @Override
    protected void onPostExecute(Movie[] movies) {
        pb.setVisibility(View.INVISIBLE);

        super.onPostExecute(movies);

        if (movies != null) {
            listener.onTaskComplete(Arrays.asList(movies));
        } else {
            listener.onTaskComplete(null);
        }
    }

}