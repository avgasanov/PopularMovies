package com.example.android.popularmovies.MovieData;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.R;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder>{
    private final Context context;
    private Review[] reviews;

    public ReviewAdapter(Context context, Review[] reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ViewHolder holder, int position) {
        String author = reviews[position].getAuthor();
        String opinion = reviews[position].getContent();
        Log.v("ONBNDREVIWS", "author is: " + author + "\n opinion is: " + opinion);
        holder.opinionTextView.setText(opinion);
        holder.opinionAuthorTextView.setText(author);
    }

    @Override
    public int getItemCount() {
        return reviews.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        final TextView opinionTextView;
        final TextView opinionAuthorTextView;
        ViewHolder(View itemView) {
            super(itemView);
            opinionTextView = itemView.findViewById(R.id.opinion_tv);
            opinionAuthorTextView = itemView.findViewById(R.id.author_tv);
        }
    }
}
