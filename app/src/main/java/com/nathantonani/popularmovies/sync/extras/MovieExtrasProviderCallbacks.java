package com.nathantonani.popularmovies.sync.extras;

import com.nathantonani.popularmovies.model.Review;
import com.nathantonani.popularmovies.model.Trailer;

import java.util.List;

/**
 * Created by ntonani on 11/2/16.
 */
public interface MovieExtrasProviderCallbacks {

    public void onMovieTrailersLoaded(List<Trailer> trailers);
    public void onMovieReviewsLoaded(List<Review> reviews);
}
