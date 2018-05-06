package com.example.android.popularmovies.MovieData;

import android.os.Parcel;
import android.os.Parcelable;

import static com.example.android.popularmovies.MovieUtils.NetworkUtils.buildImageUrl;

public class Movie implements Parcelable{
    final private int movieId;
    final private String title;
    final private String poster;
    final private String synopsis;
    final private double average_rate;
    final private String release_date;
    final private byte[] image;

    public Movie (int movieId, String title, String poster, String synopsis, double average_rate, String release_date, byte[] image) {
        this.movieId = movieId;
        this.title = title;
        this.poster = poster;
        this.synopsis = synopsis;
        this.average_rate = average_rate;
        this.release_date = release_date;
        this.image = image;
    }

    private Movie (Parcel in) {
        this.movieId = in.readInt();
        this.title = in.readString();
        this.poster = in.readString();
        this.synopsis = in.readString();
        this.average_rate = in.readDouble();
        this.release_date = in.readString();
        int byteArrayLength = in.readInt();
        if (byteArrayLength > 0) {
            this.image = new byte[byteArrayLength];
            in.readByteArray(image);
        } else {
            image = null;
        }
    }

    public int getID() {return movieId;}
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
        if (poster == null) {
            return null;
        } else {
            return buildImageUrl(size, poster).toString();
        }
    }

    public byte[] getImageFromDb() {
        return image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(movieId);
        dest.writeString(title);
        dest.writeString(poster);
        dest.writeString(synopsis);
        dest.writeDouble(average_rate);
        dest.writeString(release_date);
        if (image != null) {
            dest.writeInt(image.length);
        } else {
            dest.writeInt(0);
        }
        dest.writeByteArray(image);
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
