package com.example.android.popularmovies;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.Database.MovieContract;
import com.example.android.popularmovies.MovieData.Movie;
import com.example.android.popularmovies.MovieData.Review;
import com.example.android.popularmovies.MovieData.ReviewAdapter;
import com.example.android.popularmovies.MovieData.ReviewDataLoader;
import com.example.android.popularmovies.MovieData.Video;
import com.example.android.popularmovies.MovieData.VideoAdapter;
import com.example.android.popularmovies.MovieData.VideoDataLoader;
import com.example.android.popularmovies.MovieUtils.NetworkUtils;
import com.squareup.picasso.Picasso;
import com.bumptech.glide.Glide;

public class DetailActivity extends AppCompatActivity
                            implements  LoaderManager.LoaderCallbacks,
                                        VideoAdapter.VideoAdapterClickHandler{
    private int movieId;
    private String posterURL;
    private String titleStr;
    private String overviewStr;
    private Double averageRate;
    private String releaseDateStr;
    private boolean isFavorite;

    private ImageView mStar;
    private RecyclerView mReviewsRecycleView;
    private TextView mReviewsTitle;
    private RecyclerView mVideosRecycleView;
    private TextView mVideosTitle;

    private final int SINGLE_MOVIE_LOADER_ID = 1010;
    private final int ALL_MOVIES_LOADER_ID = 1011;
    private final int REVIEW_LOADER_ID = 1020;
    private final int VIDEO_LOADER_ID = 1030;
    private final String MOVIE_ID_KEY = "movie_id_key";

    private Review[] reviews;
    private Video[] videos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent parentIntent = this.getIntent();

        ImageView poster = findViewById(R.id.iv_thumbnail);
        TextView title = findViewById(R.id.tv_title);
        TextView release_date = findViewById(R.id.tv_release_date);
        TextView average_rate = findViewById(R.id.tv_average_rate);
        TextView synopsis = findViewById(R.id.tv_synopsis);
        mStar = findViewById(R.id.iv_star);

        mReviewsRecycleView = findViewById(R.id.reviews_rv);
        mVideosRecycleView = findViewById(R.id.trailers_rv);
        mReviewsTitle = findViewById(R.id.reviews_id);
        mVideosTitle = findViewById(R.id.related_videos_tv);



        if(parentIntent.hasExtra("movie")) {
            Movie detailMovie =  parentIntent.getParcelableExtra("movie");
            movieId = detailMovie.getID();
            posterURL = detailMovie.getPoster(NetworkUtils.IMAGE_SIZE_STANDART);
            titleStr = detailMovie.getTitle();
            overviewStr = detailMovie.getSynopsis();
            averageRate = detailMovie.getAverage_rate();
            releaseDateStr = detailMovie.getRelease_date();
            byte[] image = detailMovie.getImageFromDb();

            title.setText(titleStr);
            release_date.setText(releaseDateStr);
            average_rate.setText(String.valueOf(averageRate));
            synopsis.setText(overviewStr);

            if (image == null) {
                Picasso.with(this).load(posterURL).into(poster);
            } else {
                Glide.with(this).load(image).asBitmap().into(poster);
            }
        }

        Bundle bundle = new Bundle();
        bundle.putInt(MOVIE_ID_KEY, movieId);
        getSupportLoaderManager().initLoader(SINGLE_MOVIE_LOADER_ID, bundle, this);

        if(NetworkUtils.isConnected(this)) {
            bundle.putInt(ReviewDataLoader.REVIEW_LOADER_MOVIE_ID_TAG, movieId);
            bundle.putInt(VideoDataLoader.VIDEO_LOADER_MOVIE_ID_TAG, movieId);
            getSupportLoaderManager().initLoader(REVIEW_LOADER_ID, bundle, this);
            getSupportLoaderManager().initLoader(VIDEO_LOADER_ID, bundle, this);
            mReviewsTitle.setVisibility(View.INVISIBLE);
            mVideosTitle.setVisibility(View.INVISIBLE);
        }
    }

    public void starClick(View imageView) {
        if (getSupportLoaderManager().hasRunningLoaders()) {
            Log.v("LOADERMAN", "Started, return");
            return;
        } else {
            starClickProcess();
            Bundle bundle = new Bundle();
            bundle.putInt(MOVIE_ID_KEY, movieId);
            getSupportLoaderManager().initLoader(SINGLE_MOVIE_LOADER_ID, bundle, this);
        }
    }

    private void starClickProcess() {
        if (!isFavorite) {
            ContentValues cv = new ContentValues();
            cv.put(MovieContract.Favorites._ID, movieId);
            cv.put(MovieContract.Favorites.COLUMN_RATING, averageRate);
            cv.put(MovieContract.Favorites.COLUMN_RELEASE_DATE, releaseDateStr);
            cv.put(MovieContract.Favorites.COLUMN_SYNOPSIS, overviewStr);
            cv.put(MovieContract.Favorites.COLUMN_TITLE, titleStr);
            byte[] image = NetworkUtils.convertImageToByte(posterURL);
            cv.put(MovieContract.Favorites.COLUMN_POSTER, image);
            getContentResolver().insert(MovieContract.Favorites.CONTENT_URI, cv);
        } else {
            getContentResolver().delete(MovieContract.Favorites.buildWithId(String.valueOf(movieId)),
                    null,
                    null);
        }
    }

    private void initStar() {
        Log.v("STARPROBLEM", "star init call");
        if (isFavorite) {
            mStar.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            mStar.setImageResource(android.R.drawable.btn_star_big_off);
        }
    }

    private void initializeReviewAdapter() {
        ReviewAdapter reviewAdapter = new ReviewAdapter(this, reviews);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL,
                false);
        mReviewsRecycleView.setLayoutManager(layoutManager);
        mReviewsRecycleView.setAdapter(reviewAdapter);
        mReviewsRecycleView.setVisibility(View.VISIBLE);
        if (reviewAdapter.getItemCount() > 0 ) {
            mReviewsTitle.setVisibility(View.VISIBLE);
        } else {
            mReviewsTitle.setVisibility(View.INVISIBLE);
        }
    }

    private void initializeVideoAdapter() {
        VideoAdapter videoAdapter = new VideoAdapter(this, videos, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL,
                false);
        mVideosRecycleView.setLayoutManager(layoutManager);
        mVideosRecycleView.setAdapter(videoAdapter);
        mVideosRecycleView.setVisibility(View.VISIBLE);
        if (videoAdapter.getItemCount() > 0 ) {
            mVideosTitle.setVisibility(View.VISIBLE);
        } else {
            mVideosTitle.setVisibility(View.INVISIBLE);
        }
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle args) {
        switch (id) {
            case SINGLE_MOVIE_LOADER_ID:
                int movieId = args.getInt(MOVIE_ID_KEY);
                return new CursorLoader(this,
                        ContentUris.withAppendedId(MovieContract.Favorites.CONTENT_URI, movieId),
                        null,
                        null,
                        null,
                        null);
            case ALL_MOVIES_LOADER_ID:
                return new CursorLoader(this,
                        MovieContract.Favorites.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
            case VIDEO_LOADER_ID:
                return new VideoDataLoader(this, args);
            case REVIEW_LOADER_ID:
                return new ReviewDataLoader(this, args);
                default:
                    throw new UnsupportedOperationException ("Illegal operation with Loader " + String.valueOf(id));
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        switch (loader.getId()) {
            case SINGLE_MOVIE_LOADER_ID:
            case ALL_MOVIES_LOADER_ID:
                isFavorite = ((Cursor) data).getCount() > 0;
                initStar();
                break;
            case VIDEO_LOADER_ID:
                videos = (Video[]) data;
                if (videos == null) {
                    break;
                }
                initializeVideoAdapter();
                break;
            case REVIEW_LOADER_ID:
                reviews = (Review[]) data;
                if (reviews == null) {
                    break;
                }
                initializeReviewAdapter();
                break;
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }

    @Override
    public void onVideoClick(Uri videoUri) {
        Intent youtubeOpener = new Intent(Intent.ACTION_VIEW, videoUri);
        if (youtubeOpener.resolveActivity(getPackageManager()) != null) {
            startActivity(youtubeOpener);
        } else {
            Toast.makeText(this, "You have no application to open trailer", Toast.LENGTH_SHORT).show();
        }
    }
}
