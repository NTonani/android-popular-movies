package com.nathantonani.popularmovies.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nathantonani.popularmovies.model.Movie;
import com.nathantonani.popularmovies.R;
import com.squareup.picasso.Picasso;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ntonani on 9/12/16.
 */
public class MovieDetailsFragment extends Fragment {

    private final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();
    private Movie mMovieObject;

    @BindView(R.id.movieDetail_title) TextView movieTitle_view;
    @BindView(R.id.movieDetail_overview) TextView movieOverview_view;
    @BindView(R.id.movieDetail_releaseDate) TextView movieRelease_view;
    @BindView(R.id.movieDetail_rating) TextView movieRating_view;
    @BindView(R.id.movieDetail_thumbnail) ImageView movieThumbnail_view;

    @BindString(R.string.movie_details_parcel) String movieDetails_parcel;

    /*
     * Fragment lifecycle / callbacks
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        //Inflate fragment
        View rootView = inflater.inflate(R.layout.fragment_detail,container,false);

        //Bind Butter Knife
        ButterKnife.bind(this,rootView);

        //Check intent content
        Intent intent = getActivity().getIntent();
        if(intent!=null && intent.hasExtra(movieDetails_parcel))
            mMovieObject = intent.getExtras().getParcelable(movieDetails_parcel);

        //Check bundle state
        if(mMovieObject==null && savedInstanceState!=null && savedInstanceState.getParcelable(movieDetails_parcel)!=null)
            mMovieObject = savedInstanceState.getParcelable(movieDetails_parcel);

        if(mMovieObject==null) return rootView;

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

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState){

        //Store bundle data
        if(mMovieObject!=null)
            outState.putParcelable(movieDetails_parcel,mMovieObject);
        super.onSaveInstanceState(outState);
    }

}
