package com.example.android.popularmovies.MovieUtils;

import android.database.Cursor;

import com.example.android.popularmovies.Database.MovieContract;
import com.example.android.popularmovies.MovieData.Movie;

import java.util.ArrayList;

public class MovieUtils {
    public static ArrayList<Movie> loadMovieFromDatabase(Cursor cursor) {
        int count = cursor.getCount();
        if (count == 0) {
            cursor.close();
            return null;
        }
        ArrayList<Movie> result = new ArrayList<>();
            int idColIdx = cursor.getColumnIndex(MovieContract.Favorites._ID);
            int titleColIdx = cursor.getColumnIndex(MovieContract.Favorites.COLUMN_TITLE);
            int posterColIdx = cursor.getColumnIndex(MovieContract.Favorites.COLUMN_POSTER);
            int synopsisColIdx = cursor.getColumnIndex(MovieContract.Favorites.COLUMN_SYNOPSIS);
            int averageColIdx = cursor.getColumnIndex(MovieContract.Favorites.COLUMN_RATING);
            int releaseColIdx = cursor.getColumnIndex(MovieContract.Favorites.COLUMN_RELEASE_DATE);

            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                int movieId = cursor.getInt(idColIdx);
                String title = cursor.getString(titleColIdx);
                String synopsis = cursor.getString(synopsisColIdx);
                double average_rate = cursor.getDouble(averageColIdx);
                String release_date = cursor.getString(releaseColIdx);
                byte[] image = cursor.getBlob(posterColIdx);
                result.add(new Movie(movieId, title, null,synopsis, average_rate, release_date, image));
                }
        return result;
    }
}
