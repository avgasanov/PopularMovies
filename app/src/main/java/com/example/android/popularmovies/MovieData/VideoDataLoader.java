package com.example.android.popularmovies.MovieData;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;

import com.example.android.popularmovies.MovieUtils.JsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

import static com.example.android.popularmovies.MovieUtils.NetworkUtils.buildVideoUrl;
import static com.example.android.popularmovies.MovieUtils.NetworkUtils.getJSONResponseFromUrl;

public class VideoDataLoader extends AsyncTaskLoader<Video[]>{
    public final static String VIDEO_LOADER_MOVIE_ID_TAG = "video_loader_movie_id";
    private final Bundle args;
    private Video[] result;


    public VideoDataLoader(Context context, Bundle args) {
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
    public Video[] loadInBackground() {
        int movieId = args.getInt(VIDEO_LOADER_MOVIE_ID_TAG);

        URL jsonRequestUrl = buildVideoUrl(String.valueOf(movieId));

        try {
            String jsonResponse = getJSONResponseFromUrl(jsonRequestUrl);
            result = JsonUtils.parseVideoJson(jsonResponse);
            return result;
        } catch (IOException | JSONException e) {
            return null;
        }
    }
}
