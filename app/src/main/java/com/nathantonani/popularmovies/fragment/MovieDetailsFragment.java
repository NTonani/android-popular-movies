package com.nathantonani.popularmovies.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nathantonani.popularmovies.R;
import com.nathantonani.popularmovies.data.MoviesContract.MovieEntry;
import com.nathantonani.popularmovies.model.Movie;
import com.nathantonani.popularmovies.model.Review;
import com.nathantonani.popularmovies.model.Trailer;
import com.nathantonani.popularmovies.sync.extras.MovieExtrasProvider;
import com.nathantonani.popularmovies.sync.extras.MovieExtrasProviderCallbacks;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ntonani on 9/12/16.
 */
public class MovieDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, MovieExtrasProviderCallbacks {

    private final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();
    private static final int MOVIE_LOADER = 0;

    private Movie mMovieObject;
    private MovieExtrasProvider mMovieExtras;

    private List<Trailer> mTrailers;
    private List<Review> mReviews;

    @BindView(R.id.movieDetail_title) TextView movieTitle_view;
    @BindView(R.id.movieDetail_overview) TextView movieOverview_view;
    @BindView(R.id.movieDetail_releaseDate) TextView movieRelease_view;
    @BindView(R.id.movieDetail_rating) TextView movieRating_view;
    @BindView(R.id.movieDetail_thumbnail) ImageView movieThumbnail_view;
    @BindView(R.id.detail_reviews_container) ViewGroup movieDetailReviews_view;
    @BindView(R.id.detail_trailers_container) ViewGroup movieDetailTrailers_view;
    @BindString(R.string.movie_details_parcel) String movieDetails_parcel;

    private static final String[] MOVIE_PROJECTION = {
            MovieEntry._ID,
            MovieEntry.COLUMN_MOVIE_ID,
            MovieEntry.COLUMN_TITLE,
            MovieEntry.COLUMN_OVERVIEW,
            MovieEntry.COLUMN_ADULT,
            MovieEntry.COLUMN_RELEASE_DATE,
            MovieEntry.COLUMN_POPULARITY,
            MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieEntry.COLUMN_POSTER_PATH
    };

    public static final int COL_MOVIE_ID = 0;
    public static final int COL_MOVIE_MOVIE_ID = 1;
    public static final int COL_MOVIE_TITLE = 2;
    public static final int COL_MOVIE_OVERVIEW = 3;
    public static final int COL_MOVIE_ADULT = 4;
    public static final int COL_MOVIE_RELEASE_DATE = 5;
    public static final int COL_MOVIE_POPULARITY = 6;
    public static final int COL_MOVIE_VOTE_AVERAGE = 7;
    public static final int COL_MOVIE_POSTER_PATH = 8;

    /*
     * Fragment lifecycle / callbacks
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        //Inflate fragment
        View rootView = inflater.inflate(R.layout.fragment_detail,container,false);

        //Bind Butter Knife
        ButterKnife.bind(this,rootView);

        mTrailers = new ArrayList<Trailer>();
        mReviews = new ArrayList<Review>();
    /*
        //Check bundle state
        if(mMovieObject==null && savedInstanceState!=null && savedInstanceState.getParcelable(movieDetails_parcel)!=null)
            mMovieObject = savedInstanceState.getParcelable(movieDetails_parcel);
    */
        mMovieExtras = MovieExtrasProvider.getInstance();
        mMovieExtras.setBaseUrl(getString(R.string.movies_path_base));
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        getLoaderManager().initLoader(MOVIE_LOADER,null,this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        /*
        //Store bundle data
        if(mMovieObject!=null)
            outState.putParcelable(movieDetails_parcel,mMovieObject);
        */

        super.onSaveInstanceState(outState);
    }

    /*
     * Loader Callbacks
     */

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();
        Uri movieUri = null;

        if(intent!=null)
            movieUri = intent.getData();

        Cursor movieCursor = getActivity().getContentResolver().query(movieUri,MOVIE_PROJECTION,null,null,null);

        return new CursorLoader(getActivity(),movieUri,MOVIE_PROJECTION,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(!data.moveToFirst())return;
        mMovieObject = new Movie(data.getInt(COL_MOVIE_MOVIE_ID), data.getString(COL_MOVIE_TITLE),
                data.getString(COL_MOVIE_OVERVIEW), data.getString(COL_MOVIE_RELEASE_DATE),
                Double.parseDouble(data.getString(COL_MOVIE_POPULARITY)), Double.parseDouble(data.getString(COL_MOVIE_VOTE_AVERAGE)),
                data.getString(COL_MOVIE_POSTER_PATH));

        mMovieExtras.getReviewsForMovie(mMovieObject.getMovieId(),this);
        mMovieExtras.getTrailersForMovie(mMovieObject.getMovieId(),this);

        //Add image
        try {
            Picasso.with(getActivity()).setIndicatorsEnabled(true);
            Picasso.with(getActivity()).load(mMovieObject.getPosterPath()).into(movieThumbnail_view);
        }catch(Exception e){
            Log.e(LOG_TAG,"Error loading image: "+e.toString());
        }
        //Add title
        movieTitle_view.setText(mMovieObject.getTitle());

        //Add overview
        movieOverview_view.setText(mMovieObject.getOverview());

        //Add release date
        movieRelease_view.setText(mMovieObject.getReleaseDateString());

        //Add rating
        movieRating_view.setText("User Rating: "+mMovieObject.getUserRating()); //TODO: Resource string with placeholder

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /*
     * MovieExtraProvider callback
     */

    @Override
    public void onMovieTrailersLoaded(List<Trailer> trailers) {
        if(trailers == null) {
            Log.w(LOG_TAG, "Null trailers");
            return;
        }
        mTrailers = trailers;
        final LayoutInflater inflater = LayoutInflater.from(getActivity());
        for(Trailer trailer : mTrailers){
            final View trailerItem = inflater.inflate(R.layout.detail_trailers_item,movieDetailTrailers_view,false);
            final TextView trailerView = ButterKnife.findById(trailerItem,R.id.trailer_text);

            trailerItem.setTag(trailer);
            trailerView.setText(trailer.getName());

            trailerItem.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.w(LOG_TAG,"onClick of view!");
                    Trailer curTrailer = (Trailer) v.getTag();
                    Intent youtubeApp = new Intent(Intent.ACTION_VIEW,Uri.parse("vnd.youtube:"+curTrailer.getSource()));
                    Intent webApp = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v="+curTrailer.getSource()));

                    try{
                        startActivity(youtubeApp);
                    }catch(ActivityNotFoundException e){
                        startActivity(webApp);
                    }
                }
            });

            movieDetailTrailers_view.addView(trailerItem);
        }
    }

    @Override
    public void onMovieReviewsLoaded(List<Review> reviews) {
        if(reviews == null){
            Log.w(LOG_TAG,"Null reviews");
            return;
        }
        mReviews = reviews;
        final LayoutInflater inflater = LayoutInflater.from(getActivity());
        for(Review review : mReviews) {
            final View reviewItem = inflater.inflate(R.layout.detail_reviews_item,movieDetailReviews_view,false);
            final TextView authorView = ButterKnife.findById(reviewItem,R.id.review_author);
            final TextView contentView = ButterKnife.findById(reviewItem,R.id.review_content);

            authorView.setText(review.getAuthor());
            contentView.setText(review.getContent());
            movieDetailReviews_view.addView(reviewItem);
        }
    }

}
