package com.nathantonani.popularmovies.sync.extras;

import android.util.Log;

import com.nathantonani.popularmovies.BuildConfig;
import com.nathantonani.popularmovies.model.Review;
import com.nathantonani.popularmovies.model.Reviews;
import com.nathantonani.popularmovies.model.Trailer;
import com.nathantonani.popularmovies.model.Trailers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by ntonani on 11/1/16.
 */
public class MovieExtrasProvider {
    private final String LOG_TAG = MovieExtrasProvider.class.getSimpleName();
    private static MovieExtrasProvider INSTANCE = null;

    private String BASE_URL = null;
    private final String API_KEY;

    private MovieExtrasServiceInterface mMovieExtrasService;
    private Retrofit mRetrofit;

    private Map<Integer,List<Trailer>> mTrailers;
    private Map<Integer,List<Review>> mReviews;

    private MovieExtrasProvider(){
        mTrailers = new HashMap<Integer,List<Trailer>>();
        mReviews = new HashMap<Integer,List<Review>>();
        API_KEY = BuildConfig.THE_MOVIE_DB_API_KEY;
    }

    public void getTrailersForMovie(int movieId, final MovieExtrasProviderCallbacks callback){
        if(mTrailers.containsKey(movieId))
            callback.onMovieTrailersLoaded(mTrailers.get(movieId));

        if(mMovieExtrasService == null) {
            callback.onMovieTrailersLoaded(null);
            return;
        }

        Call<Trailers> call = mMovieExtrasService.getTrailersForMovie(movieId,API_KEY);
        call.enqueue(new Callback<Trailers>(){
            @Override
            public void onResponse(Call<Trailers> call, Response<Trailers> response){
                int statusCode = response.code();
                if(statusCode > 202) {
                    callback.onMovieTrailersLoaded(null);
                    return;
                }

                Trailers curTrailers = response.body();
                callback.onMovieTrailersLoaded(curTrailers.getTrailers());
            }

            @Override
            public void onFailure(Call<Trailers> call, Throwable t){
                callback.onMovieTrailersLoaded(null);
            }
        });
    }

    public void getReviewsForMovie(int movieId, final MovieExtrasProviderCallbacks callback){
        if(mReviews.containsKey(movieId))
            callback.onMovieReviewsLoaded(mReviews.get(movieId));

        if(mMovieExtrasService == null) {
            callback.onMovieReviewsLoaded(null);
            return;
        }

        Call<Reviews> call = mMovieExtrasService.getReviewsForMovie(movieId,API_KEY);
        call.enqueue(new Callback<Reviews>(){

            @Override
            public void onResponse(Call<Reviews> call, Response<Reviews> response) {
                int statusCode = response.code();
                if(statusCode > 202){
                    callback.onMovieReviewsLoaded(null);
                    return;
                }

                Reviews curReviews = response.body();
                callback.onMovieReviewsLoaded(curReviews.getReviews());
            }

            @Override
            public void onFailure(Call<Reviews> call, Throwable t) {
                callback.onMovieReviewsLoaded(null);
            }
        });
    }

    public void setBaseUrl(String baseUrl){
        this.BASE_URL = baseUrl;
        mRetrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL).build();
        mMovieExtrasService = mRetrofit.create(MovieExtrasServiceInterface.class);
        Log.w(LOG_TAG, mRetrofit.baseUrl().toString());
    }

    public static MovieExtrasProvider getInstance(){
        if(INSTANCE == null) INSTANCE = new MovieExtrasProvider();
        return INSTANCE;
    }
}
