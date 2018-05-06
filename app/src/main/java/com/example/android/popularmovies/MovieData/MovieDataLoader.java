package com.example.android.popularmovies.MovieData;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;

import com.example.android.popularmovies.MovieUtils.JsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

import static com.example.android.popularmovies.MovieUtils.NetworkUtils.MOST_POPULAR_ORDER;
import static com.example.android.popularmovies.MovieUtils.NetworkUtils.TOP_RATED_ORDER;
import static com.example.android.popularmovies.MovieUtils.NetworkUtils.buildUrl;
import static com.example.android.popularmovies.MovieUtils.NetworkUtils.getJSONResponseFromUrl;

public class MovieDataLoader extends AsyncTaskLoader<Movie[]> {
    public final static String MOVIE_DATA_LOADER_TAG = "MDLOAD";
    private final Bundle args;
    private Movie[] result;

    public MovieDataLoader(Context context, Bundle args) {
        super(context);
        this.args = args;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (args == null) {
            return;
        }

        if (result != null) {
            deliverResult(result);
        } else {
            forceLoad();
        }
    }

    @Override
    public Movie[] loadInBackground() {
        String order = args.getString(MOVIE_DATA_LOADER_TAG);

        if (order == null) {
            return null;
        }

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

}
