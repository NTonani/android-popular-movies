package com.nathantonani.popularmovies.sync.extras;

import com.nathantonani.popularmovies.model.Reviews;
import com.nathantonani.popularmovies.model.Trailers;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by ntonani on 11/1/16.
 */
public interface MovieExtrasServiceInterface {

    @GET("{movie_id}/trailers")
    Call<Trailers> getTrailersForMovie(@Path("movie_id") int movieId, @Query("api_key") String apiKey);

    @GET("{movie_id}/reviews")
    Call<Reviews> getReviewsForMovie(@Path("movie_id") int movieId, @Query("api_key") String apiKey);
}
