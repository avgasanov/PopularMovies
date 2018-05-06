package com.example.android.popularmovies.MovieData;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;

import com.example.android.popularmovies.MovieUtils.JsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

import static com.example.android.popularmovies.MovieUtils.NetworkUtils.buildReviewsUrl;
import static com.example.android.popularmovies.MovieUtils.NetworkUtils.getJSONResponseFromUrl;

public class ReviewDataLoader extends AsyncTaskLoader<Review[]> {
    public final static String REVIEW_LOADER_MOVIE_ID_TAG = "loader_movie_id";
    private final Bundle args;
    private Review result[];

    public ReviewDataLoader(Context context, Bundle args) {
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
    public Review[] loadInBackground() {
        String movieId = String.valueOf(args.getInt(REVIEW_LOADER_MOVIE_ID_TAG));

        URL jsonRequestUrl = buildReviewsUrl(movieId);

        try {
            String jsonResponse = getJSONResponseFromUrl(jsonRequestUrl);
            result = JsonUtils.parseReviewJson(jsonResponse);
            return result;
        } catch (IOException | JSONException e) {
            return null;
        }
    }

}
