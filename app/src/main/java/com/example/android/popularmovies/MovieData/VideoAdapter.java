package com.example.android.popularmovies.MovieData;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.MovieUtils.NetworkUtils;
import com.example.android.popularmovies.R;
import com.squareup.picasso.Picasso;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder>{
    private final Context context;
    private Video videos[];
    final private VideoAdapterClickHandler clickHandler;

    public VideoAdapter(Context context, Video[] videos, VideoAdapterClickHandler handler) {
        this.clickHandler = handler;
        this.context = context;
        this.videos = videos;
    }

    public interface VideoAdapterClickHandler {
        void onVideoClick(Uri videoUri);
    }

    @NonNull
    @Override
    public VideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoAdapter.ViewHolder holder, int position) {
            Uri imageUri = NetworkUtils.buildYoutubeImageUri(videos[position].getKey());
            Picasso.with(context).load(imageUri).into(holder.mTrailer);
    }

    @Override
    public int getItemCount() {
        return videos.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final ImageView mTrailer;

        ViewHolder(View view) {
            super(view);
            mTrailer = view.findViewById(R.id.trailer_iv);
            mTrailer.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Uri videoUri = NetworkUtils.buildYoutubeUri(videos[position].getKey());
            clickHandler.onVideoClick(videoUri);
        }
    }


}
