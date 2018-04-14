package com.example.android.popularmovies.MovieUtils;

import com.example.android.popularmovies.MovieData.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static java.net.HttpURLConnection.HTTP_OK;

public class JsonUtils {

    private final static String OWM_MESSAGE_CODE = "cod";

    private final static String JSON_RESULTS = "results";
    private final static String JSON_MOVIE_TITLE = "original_title";
    private final static String JSON_POSTER = "poster_path";
    private final static String JSON_SYNOPSIS = "overview";
    private final static String JSON_RATE = "vote_average";
    private final static String JSON_RELEASE_DATE = "release_date";

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
        JSONObject movieJsonObject = new JSONObject(jsonString);
        if (movieJsonObject.has(OWM_MESSAGE_CODE) && movieJsonObject.getInt(OWM_MESSAGE_CODE) != HTTP_OK) {
            return null;
        }
        JSONArray moviesArray = movieJsonObject.getJSONArray(JSON_RESULTS);
        Movie[] result = new Movie[moviesArray.length()];
        for (int i=0; i < moviesArray.length(); i++) {
            JSONObject oneMovieFromMoviesArray = moviesArray.getJSONObject(i);

            String movieTitle = oneMovieFromMoviesArray.optString(JSON_MOVIE_TITLE);
            String moviePoster = oneMovieFromMoviesArray.optString(JSON_POSTER);
            String movieSynopsis = oneMovieFromMoviesArray.optString(JSON_SYNOPSIS);
            double movieRate = oneMovieFromMoviesArray.optDouble(JSON_RATE);
            String movieReleaseDate = oneMovieFromMoviesArray.optString(JSON_RELEASE_DATE);

            result[i] = new Movie(movieTitle, moviePoster, movieSynopsis, movieRate, movieReleaseDate);
        }
        return result;
    }

}

