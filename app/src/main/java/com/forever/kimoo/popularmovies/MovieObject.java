package com.forever.kimoo.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by KiMoo on 04/12/2015.
 */
public class MovieObject implements Parcelable {
    private String ID;
    private String movieName;
    private String posterURL;
    private String originalName;
    private String moviePostarThumbnail;
    private String overview;
    private String rating;
    private String releaseDate;


    public MovieObject(){

    }


    public MovieObject(String id,String name,String poster,String image,String overview,String rating,String date){
        this.setID(id);
        this.setMovieName(name);
        this.setPosterURL(poster);
        this.setMoviePostarThumbnail(image);
        this.setOverview(overview);
        this.setRating(rating);
        this.setReleaseDate(date);
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getPosterURL() {
        return posterURL;
    }

    public void setPosterURL(String posterURL) {
        this.posterURL = posterURL;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getMoviePostarThumbnail() {
        return moviePostarThumbnail;
    }

    public void setMoviePostarThumbnail(String moviePostarThumbnail) {
        this.moviePostarThumbnail = moviePostarThumbnail;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    protected MovieObject(Parcel in) {
        ID = in.readString();
        movieName = in.readString();
        posterURL = in.readString();
        originalName = in.readString();
        moviePostarThumbnail = in.readString();
        overview = in.readString();
        rating = in.readString();
        releaseDate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ID);
        dest.writeString(movieName);
        dest.writeString(posterURL);
        dest.writeString(originalName);
        dest.writeString(moviePostarThumbnail);
        dest.writeString(overview);
        dest.writeString(rating);
        dest.writeString(releaseDate);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MovieObject> CREATOR = new Parcelable.Creator<MovieObject>() {
        @Override
        public MovieObject createFromParcel(Parcel in) {
            return new MovieObject(in);
        }

        @Override
        public MovieObject[] newArray(int size) {
            return new MovieObject[size];
        }
    };
}
