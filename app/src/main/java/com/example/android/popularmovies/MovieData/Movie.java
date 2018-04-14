package com.example.android.popularmovies.MovieData;

import android.os.Parcel;
import android.os.Parcelable;

import static com.example.android.popularmovies.MovieUtils.NetworkUtils.buildImageUrl;

public class Movie implements Parcelable{
    final private String title;
    final private String poster;
    final private String synopsis;
    final private double average_rate;
    final private String release_date;

    public Movie (String title, String poster, String synopsis, double average_rate, String release_date) {
        this.title = title;
        this.poster = poster;
        this.synopsis = synopsis;
        this.average_rate = average_rate;
        this.release_date = release_date;
    }

    private Movie (Parcel in) {
    this.title = in.readString();
    this.poster = in.readString();
    this.synopsis = in.readString();
    this.average_rate = in.readDouble();
    this.release_date = in.readString();
    }

    public double getAverage_rate() {
        return average_rate;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getRelease_date() {
        return release_date;
    }

    public String getTitle() {
        return title;
    }

    public String getPoster(String size) {
        return buildImageUrl(size, poster).toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(poster);
        dest.writeString(synopsis);
        dest.writeDouble(average_rate);
        dest.writeString(release_date);
    }

    public final static Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }
    };
}
