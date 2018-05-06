package com.example.android.popularmovies.Database;

import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {

    public final static String AUTHORITY = "com.example.android.popularmovies";
    private final static Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public final static String PATH_MOVIES = "movies";

    public static final class Favorites implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                                                    .appendPath(PATH_MOVIES).build();

        public final static String TABLE_NAME = "movies";
        public final static String COLUMN_TITLE = "title";
        public final static String COLUMN_POSTER = "poster";
        public final static String COLUMN_SYNOPSIS = "synopsis";
        public final static String COLUMN_RATING = "rating";
        public final static String COLUMN_RELEASE_DATE = "release_date";

       public static Uri buildWithId(String id) {
            return CONTENT_URI.buildUpon().appendPath(id).build();
       }
    }
}
