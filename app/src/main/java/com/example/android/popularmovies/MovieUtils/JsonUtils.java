package com.example.android.popularmovies.MovieUtils;

import android.util.Log;

import com.example.android.popularmovies.MovieData.Movie;
import com.example.android.popularmovies.MovieData.Review;
import com.example.android.popularmovies.MovieData.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static java.net.HttpURLConnection.HTTP_OK;

public class JsonUtils {

    private final static String OWM_MESSAGE_CODE = "cod";

    private final static String JSON_ID = "id";
    private final static String JSON_RESULTS = "results";
    private final static String JSON_MOVIE_TITLE = "original_title";
    private final static String JSON_POSTER = "poster_path";
    private final static String JSON_SYNOPSIS = "overview";
    private final static String JSON_RATE = "vote_average";
    private final static String JSON_RELEASE_DATE = "release_date";

    private final static String JSON_REVIEW_AUTHOR = "author";
    private final static String JSON_REVIEW_URL = "url";
    private final static String JSON_REVIEW_CONTENT = "content";

    private final static String JSON_VIDEO_KEY = "key";
    private final static String JSON_VIDEO_NAME = "name";
    private final static String JSON_VIDEO_SITE = "site";
    private final static String JSON_VIDEO_SIZE = "size";
    private final static String JSON_VIDEO_TYPE = "type";

    /**
     * This method returns array of Movie objects with required data for the first stage of
     * popular movies project.
     * Documentation could be found on:
     * https://developers.themoviedb.org/3/discover/movie-discover
     * @param jsonString - is server response with movies data
     * @return Movie[] - is array of Movies as the result of parsing json response from server
     * @throws JSONException
     */
    public static Movie[] parseMovieJson(String jsonString) throws JSONException {
        if (jsonString == null) {
            return null;
        }

        JSONObject movieJsonObject = new JSONObject(jsonString);
        if (movieJsonObject.has(OWM_MESSAGE_CODE) && movieJsonObject.getInt(OWM_MESSAGE_CODE) != HTTP_OK) {
            return null;
        }
        JSONArray moviesArray = movieJsonObject.getJSONArray(JSON_RESULTS);
        Movie[] result = new Movie[moviesArray.length()];
        for (int i=0; i < moviesArray.length(); i++) {
            JSONObject oneMovieFromMoviesArray = moviesArray.getJSONObject(i);

            int movieId = oneMovieFromMoviesArray.getInt(JSON_ID);
            String movieTitle = oneMovieFromMoviesArray.optString(JSON_MOVIE_TITLE);
            String moviePoster = oneMovieFromMoviesArray.optString(JSON_POSTER);
            String movieSynopsis = oneMovieFromMoviesArray.optString(JSON_SYNOPSIS);
            double movieRate = oneMovieFromMoviesArray.optDouble(JSON_RATE);
            String movieReleaseDate = oneMovieFromMoviesArray.optString(JSON_RELEASE_DATE);

            result[i] = new Movie(movieId, movieTitle, moviePoster, movieSynopsis, movieRate, movieReleaseDate, null);
        }
        return result;
    }

    public static Review[] parseReviewJson(String jsonString) throws JSONException {
        if (jsonString == null) {
            return null;
        }

        Log.v("REVJSON", "Review request: " + jsonString);

        JSONObject reviewJsonObject = new JSONObject(jsonString);
        if (reviewJsonObject.has(OWM_MESSAGE_CODE) && reviewJsonObject.getInt(OWM_MESSAGE_CODE) != HTTP_OK) {
            return null;
        }

        JSONArray reviewsArray = reviewJsonObject.getJSONArray(JSON_RESULTS);
        Review[] result = new Review[reviewsArray.length()];
        for (int i=0; i < reviewsArray.length(); i++) {
            JSONObject oneReviewFromReviewsArray = reviewsArray.getJSONObject(i);

            String reviewId = oneReviewFromReviewsArray.optString(JSON_ID);
            String reviewAuthor = oneReviewFromReviewsArray.optString(JSON_REVIEW_AUTHOR);
            String reviewContent = oneReviewFromReviewsArray.optString(JSON_REVIEW_CONTENT);
            String reviewUrl = oneReviewFromReviewsArray.optString(JSON_REVIEW_URL);

            result[i] = new Review(reviewId, reviewAuthor, reviewContent, reviewUrl);
        }

        return result;
    }

    public static Video[] parseVideoJson(String jsonString) throws JSONException {
        if (jsonString == null) {
            return null;
        }

        Log.v("VIDJSON", "Video request: " + jsonString);

        JSONObject videoJsonObject = new JSONObject(jsonString);
        if (videoJsonObject.has(OWM_MESSAGE_CODE) && videoJsonObject.getInt(OWM_MESSAGE_CODE) != HTTP_OK) {
            return null;
        }

        JSONArray videosArray = videoJsonObject.getJSONArray(JSON_RESULTS);
        Video[] result = new Video[videosArray.length()];
        for (int i=0; i < videosArray.length(); i++) {
            JSONObject oneVideoFromVideosArray = videosArray.getJSONObject(i);

            String videoId = oneVideoFromVideosArray.getString(JSON_ID);
            String videoKey = oneVideoFromVideosArray.getString(JSON_VIDEO_KEY);
            String videoName = oneVideoFromVideosArray.getString(JSON_VIDEO_NAME);
            String videoSite = oneVideoFromVideosArray.getString(JSON_VIDEO_SITE);
            int videoSize = oneVideoFromVideosArray.getInt(JSON_VIDEO_SIZE);
            String videoType = oneVideoFromVideosArray.getString(JSON_VIDEO_TYPE);

            result[i] = new Video(videoId, videoKey, videoName, videoSite, videoSize, videoType);
        }

        if (result == null) {
            Log.v("VIDJSON", "video result: 0");
        } else {
            Log.v("VIDJSON", "video result: " + String.valueOf(result.length));
        }

        return result;
    }

}

