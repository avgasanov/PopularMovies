package com.example.android.popularmovies.Database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.example.android.popularmovies.Database.MovieContract.AUTHORITY;
import static com.example.android.popularmovies.Database.MovieContract.PATH_MOVIES;

public class MovieProvider extends ContentProvider {

    private static final int FAVORITE_MOVIES = 100;
    private static final int SINGLE_MOVIE = 101;

    private MovieDbHelper movieDbHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(AUTHORITY, PATH_MOVIES, FAVORITE_MOVIES);
        uriMatcher.addURI(AUTHORITY, PATH_MOVIES + "/#", SINGLE_MOVIE);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        movieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = movieDbHelper.getReadableDatabase();
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case FAVORITE_MOVIES:
                cursor = database.query(MovieContract.Favorites.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case SINGLE_MOVIE:
                cursor = database.query(MovieContract.Favorites.TABLE_NAME,
                        projection,
                        MovieContract.Favorites._ID + " = ?",
                        new String[] {uri.getPathSegments().get(1)},
                        null,
                        null,
                        sortOrder);
                break;
                default:
                    throw new UnsupportedOperationException("Invalid Uri: " + uri.toString());
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not implemented for this project");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase database = movieDbHelper.getWritableDatabase();
        Uri insertUri;

        if (values == null) {
            throw new NullPointerException("Please check content values");
        }

        switch (sUriMatcher.match(uri)) {
            case FAVORITE_MOVIES:
                long id = database.insert(MovieContract.Favorites.TABLE_NAME,
                                null,
                                 values);
                if (id > 0) {
                    insertUri = ContentUris.withAppendedId(MovieContract.Favorites.CONTENT_URI, id);
                } else {
                    throw new SQLiteException("Value is not inserted. Check Primary Constraint");
                }
                break;
                default: throw new UnsupportedOperationException("Invalid Uri");
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return insertUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = movieDbHelper.getWritableDatabase();
        int deletedRows;

        switch (sUriMatcher.match(uri)) {
            case FAVORITE_MOVIES:
                deletedRows = database.delete(MovieContract.Favorites.TABLE_NAME,
                                        null,
                                        null);
                break;
            case SINGLE_MOVIE:
                deletedRows =  database.delete(MovieContract.Favorites.TABLE_NAME,
                        MovieContract.Favorites._ID + " = ?",
                        new String[] {uri.getPathSegments().get(1)});
                break;
                default:
                    throw new UnsupportedOperationException("Invalid Uri");
        }

        if (deletedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deletedRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = movieDbHelper.getWritableDatabase();
        int update_count;

        if (values == null) {
            throw new NullPointerException("Please check content values");
        }

        switch (sUriMatcher.match(uri)) {
            case SINGLE_MOVIE:
                update_count = database.update(MovieContract.Favorites.TABLE_NAME,
                                        values,
                                        MovieContract.Favorites._ID + " = ?",
                                        new String[] {uri.getPathSegments().get(1)});
                break;
            case FAVORITE_MOVIES:
                update_count = database.update(MovieContract.Favorites.TABLE_NAME,
                                        values,
                                        selection,
                                        selectionArgs);
                break;
                default:
                    throw new UnsupportedOperationException("Invalid Uri");
        }

        if (update_count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return update_count;
    }
}
