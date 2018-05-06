package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.Database.MovieContract;
import com.example.android.popularmovies.MovieData.Movie;
import com.example.android.popularmovies.MovieData.MovieAdapter;
import com.example.android.popularmovies.MovieData.MovieDataLoader;
import com.example.android.popularmovies.MovieUtils.MovieUtils;
import com.example.android.popularmovies.MovieUtils.NetworkUtils;

import java.util.ArrayList;
import java.util.Arrays;

import static com.example.android.popularmovies.MovieUtils.NetworkUtils.MOST_POPULAR_ORDER;
import static com.example.android.popularmovies.MovieUtils.NetworkUtils.TOP_RATED_ORDER;
import static com.example.android.popularmovies.MovieUtils.NetworkUtils.isConnected;



public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,
        LoaderManager.LoaderCallbacks, SharedPreferences.OnSharedPreferenceChangeListener {

    private ArrayList<Movie> movieArrayList;
    private Context context;

    private GridView gridView;
    private TextView errorView;
    private ProgressBar progressBar;

    private Menu optionsMenu;


    final private static String INSTANCE_STATE_MOVIES = "movies";
    final private static String INSTANCE_STATE_SCROLL_INDEX = "scroll_index";
    final private static String INSTANCE_STATE_SCROLL_TOP = "scroll_top";

    final private int CATEGORY_POPULAR = 0;
    final private int CATEGORY_RATED = 1;
    final private int CATEGORY_FAVORITES = 2;

    final private static int MOVIE_LOADER_ID = 300;
    final private static int MOVIE_FROM_DATABASE_LOADER_ID = 400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        gridView = findViewById(R.id.gridview);
        errorView = findViewById(R.id.tv_error);
        progressBar = findViewById(R.id.pb_loading_indicator);

        gridView.setOnItemClickListener(this);

        //START CODE SOURCE https://stackoverflow.com/questions/6465680/how-to-determine-the-screen-width-in-terms-of-dp-or-dip-at-runtime-in-android?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dpWidth = displayMetrics.widthPixels;
        //END CODE FROM SOURCE

        gridView.setNumColumns(dpWidth/450);

        Log.v("INSTANCESTATE", "On create process");
        if(savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_STATE_MOVIES)) {
            movieArrayList = savedInstanceState.getParcelableArrayList(INSTANCE_STATE_MOVIES);
            initializeAdapter();

            int index = savedInstanceState.getInt(INSTANCE_STATE_SCROLL_INDEX);
            int top = savedInstanceState.getInt(INSTANCE_STATE_SCROLL_TOP);
            gridView.setSelectionFromTop(index, top);
            Log.v("INSTANCESTATE", "instance state restored, Movies count = " + String.valueOf(movieArrayList.size()));
        } else {
            initMovies();
        }

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int index = gridView.getFirstVisiblePosition();
        View v = gridView.getChildAt(0);
        int top = (v == null) ? 0 : (v.getTop() - gridView.getPaddingTop());

        outState.putParcelableArrayList(INSTANCE_STATE_MOVIES, movieArrayList);
        Log.v("INSTANCESTATE", "we put parcelable. Movies count: " + String.valueOf(movieArrayList.size()));
        outState.putInt(INSTANCE_STATE_SCROLL_INDEX, index);
        outState.putInt(INSTANCE_STATE_SCROLL_TOP, top);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    private void initMovies() {
        SharedPreferences defPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String orderPreferenceKey = getString(R.string.list_preference_sort_key);
        int order = Integer.parseInt(defPreferences.getString(orderPreferenceKey, "0"));

        if (order != CATEGORY_FAVORITES && !isConnected(this)) {
            Toast.makeText(this, getString(R.string.switching_to_favorites), Toast.LENGTH_SHORT).show();
            order = CATEGORY_FAVORITES;
        }

        showGrid();

        switch (order) {
            case CATEGORY_POPULAR:
                initLoader(MOST_POPULAR_ORDER);
                break;
            case CATEGORY_RATED:
                initLoader(TOP_RATED_ORDER);
                break;
            case CATEGORY_FAVORITES:
                initDatabaseLoader();
                break;
                default:
                    defPreferences.edit()
                            .putString(orderPreferenceKey, String.valueOf(CATEGORY_FAVORITES))
                            .apply();
                    initDatabaseLoader();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Movie detailMovie = movieArrayList.get(position);
        Class detailActivity = DetailActivity.class;
        Intent detailIntent = new Intent(MainActivity.this, detailActivity);
        detailIntent.putExtra("movie", detailMovie);
        startActivity(detailIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        optionsMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!NetworkUtils.isConnected(context) && item.getItemId() != R.id.action_favorite) {
            showError();
            return true;
        }

        int id = item.getItemId();
        switch (id) {
            case R.id.action_popular:
            {
                PreferenceManager.getDefaultSharedPreferences(this).edit()
                        .putString(getString(R.string.list_preference_sort_key), String.valueOf(CATEGORY_POPULAR))
                        .apply();
                return true;
            }
            case R.id.action_rated:
            {
                PreferenceManager.getDefaultSharedPreferences(this).edit()
                        .putString(getString(R.string.list_preference_sort_key), String.valueOf(CATEGORY_RATED))
                        .apply();
                return true;
            }
            case R.id.action_favorite:
            {
                PreferenceManager.getDefaultSharedPreferences(this).edit()
                        .putString(getString(R.string.list_preference_sort_key), String.valueOf(CATEGORY_FAVORITES))
                        .apply();
                return true;
            }
            case R.id.action_settings:
            {
                Intent activityIntent = new Intent(this, SettingsActivity.class);
                startActivity(activityIntent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeAdapter() {
        MovieAdapter movieAdapter = new MovieAdapter(this, movieArrayList);
        gridView.setAdapter(movieAdapter);
    }

    private void initLoader(String sortOrder) {

        Bundle bundle = new Bundle();
        bundle.putString(MovieDataLoader.MOVIE_DATA_LOADER_TAG, sortOrder);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<Movie[]> loader = loaderManager.getLoader(MOVIE_LOADER_ID);
        if (loader == null) {
            loaderManager.initLoader(MOVIE_LOADER_ID, bundle, this);
        } else {
            loaderManager.restartLoader(MOVIE_LOADER_ID, bundle, this);
        }

        showProgress();
    }

    private void initDatabaseLoader() {

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<Movie[]> loader = loaderManager.getLoader(MOVIE_LOADER_ID);
        if (loader == null) {
            loaderManager.initLoader(MOVIE_FROM_DATABASE_LOADER_ID, null, this);
        } else {
            loaderManager.restartLoader(MOVIE_FROM_DATABASE_LOADER_ID, null, this);
        }

        showProgress();
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id) {
            case MOVIE_LOADER_ID:
            return new MovieDataLoader(this, args);
            case MOVIE_FROM_DATABASE_LOADER_ID:
                return new CursorLoader(this,
                        MovieContract.Favorites.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
                default: throw new UnsupportedOperationException("loader id is illegal");
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        switch (loader.getId()) {
            case MOVIE_LOADER_ID:
                if (data == null) {
                    showError();
                } else {
                    showGrid();
                    movieArrayList = new ArrayList<>(Arrays.asList((Movie[]) data));
                    initializeAdapter();
                }
                break;
            case MOVIE_FROM_DATABASE_LOADER_ID:
                if (data == null) {
                    showError();
                } else {
                    showGrid();
                    movieArrayList = MovieUtils.loadMovieFromDatabase((Cursor) data);
                    if (movieArrayList == null) {
                        movieArrayList = new ArrayList<Movie>();
                        initializeAdapter();
                        Toast.makeText(this, getString(R.string.error_no_favorites), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }


    private void showError() {
        progressBar.setVisibility(View.INVISIBLE);
        gridView.setVisibility(View.INVISIBLE);
        errorView.setVisibility(View.VISIBLE);
    }

    private void showGrid() {
        progressBar.setVisibility(View.INVISIBLE);
        errorView.setVisibility(View.INVISIBLE);
        gridView.setVisibility(View.VISIBLE);
    }

    private void showProgress() {
        errorView.setVisibility(View.INVISIBLE);
        gridView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        initMovies();
    }
}
