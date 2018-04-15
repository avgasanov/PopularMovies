package com.example.android.popularmovies.MovieData;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.android.popularmovies.MovieUtils.NetworkUtils;
import com.example.android.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends ArrayAdapter<Movie> {


    public MovieAdapter(Activity context, List<Movie> movieArray) {
        super (context, 0,movieArray);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Movie movie = getItem(position);
        Context context = getContext();

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.grid_item, parent, false);
        }

        ImageView poster = convertView.findViewById(R.id.iv_poster);
        String posterURL = movie.getPoster(NetworkUtils.IMAGE_SIZE_STANDART);
        if (poster != null) {
            Picasso.with(context).load(posterURL).into(poster);
        }

        return convertView;
    }

}
