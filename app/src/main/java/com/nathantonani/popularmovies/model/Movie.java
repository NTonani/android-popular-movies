package com.nathantonani.popularmovies.model;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.nathantonani.popularmovies.data.MoviesContract.MovieEntry;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by ntonani on 9/11/16.
 */
public class Movie implements Parcelable{

    private final String urlBase = "http://image.tmdb.org/t/p/";
    private final String picWidthSmall = "w342";
    private final String picWidthLarge = "w500";

    private int movieId;
    private String posterPath;
    private String overview;
    private String releaseDateString;
    private String title;
    private Double userRating;
    private Double popularity;

    public Movie(JSONObject movieJson) throws JSONException{

        this.posterPath = urlBase+picWidthLarge+movieJson.getString("poster_path");
        this.overview = movieJson.getString("overview");
        this.releaseDateString = movieJson.getString("release_date");
        this.title = movieJson.getString("original_title");
        this.userRating = movieJson.getDouble("vote_average");
        this.popularity = movieJson.getDouble("popularity");
        this.movieId = movieJson.getInt("id");

    }

    public Movie(int movieId, String posterPath, String overview, String releaseDateString, String title, Double userRating, Double popularity){

        this.movieId = movieId;
        this.posterPath = urlBase+picWidthLarge+posterPath;
        this.overview = overview;
        this.releaseDateString = releaseDateString;
        this.title = title;
        this.userRating = userRating;
        this.popularity = popularity;

    }

    /*
     * Parcelable
     */

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Object createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new Movie[size];
        }
    };

    public Movie(Parcel in){
        this.posterPath = urlBase+picWidthLarge+in.readString();
        this.overview = in.readString();
        this.releaseDateString = in.readString();
        this.title = in.readString();
        this.userRating = in.readDouble();
        this.popularity = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.posterPath);
        dest.writeString(this.overview);
        dest.writeString(this.releaseDateString);
        dest.writeString(this.title);
        dest.writeDouble(this.userRating);
        dest.writeDouble(this.popularity);
    }

    /*
     * Getters
     */

    public String getPosterPath() {
        return posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDateString() {
        return releaseDateString;
    }

    public String getTitle() {
        return title;
    }

    public Double getUserRating() {
        return userRating;
    }

    public Double getPopularity() { return popularity; }

    public int getMovieId() {return movieId;}

    @Override
    public String toString(){
        return this.title+" -- "+this.overview;
    }

    public ContentValues getContentValues(){
        ContentValues cv = new ContentValues();
        cv.put(MovieEntry.COLUMN_MOVIE_ID,movieId);
        cv.put(MovieEntry.COLUMN_TITLE,title);
        cv.put(MovieEntry.COLUMN_OVERVIEW,overview);
        cv.put(MovieEntry.COLUMN_RELEASE_DATE,releaseDateString);
        cv.put(MovieEntry.COLUMN_POSTER_PATH,posterPath);
        cv.put(MovieEntry.COLUMN_POPULARITY,popularity);
        cv.put(MovieEntry.COLUMN_VOTE_AVERAGE,getUserRating());
        cv.put(MovieEntry.COLUMN_ADULT,false);
        return cv;
    }

}
