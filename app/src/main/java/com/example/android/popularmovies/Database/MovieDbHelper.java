package com.example.android.popularmovies.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import static com.example.android.popularmovies.Database.MovieContract.Favorites.*;

class MovieDbHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createMovieTable = "CREATE TABLE " + TABLE_NAME +
                                                "(" + _ID                   + " INTEGER PRIMARY KEY, "  +
                                                     COLUMN_TITLE           + " TEXT NOT NULL,"         +
                                                     COLUMN_POSTER          + " BLOB, "                 +
                                                     COLUMN_SYNOPSIS        + " TEXT, "                 +
                                                     COLUMN_RATING          + " REAL, "                 +
                                                     COLUMN_RELEASE_DATE    + " TEXT "                  +
                                                ");";
        db.execSQL(createMovieTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
