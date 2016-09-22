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

import com.nathantonani.popularmovies.data.Movie;
import com.nathantonani.popularmovies.R;
import com.squareup.picasso.Picasso;

/**
 * Created by ntonani on 9/12/16.
 */
public class MovieDetailsFragment extends Fragment {

    private final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();

    private Movie mMovieObject;

    /*
     * Fragment lifecycle / callbacks
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        //Inflate fragment
        View rootView = inflater.inflate(R.layout.fragment_detail,container,false);

        //Check intent content
        Intent intent = getActivity().getIntent();
        if(intent!=null && intent.hasExtra("movie"))
            mMovieObject = intent.getExtras().getParcelable("movie");

        //Check bundle state
        if(mMovieObject==null && savedInstanceState!=null && savedInstanceState.getParcelable("detailsMovie")!=null)
            mMovieObject = savedInstanceState.getParcelable("detailsMovie");

        if(mMovieObject==null) return rootView;

        //Add image
        try {
            Picasso.with(getActivity()).setIndicatorsEnabled(true);
            Picasso.with(getActivity()).load(mMovieObject.getPosterPath()).into((ImageView) rootView.findViewById(R.id.movieDetail_thumbnail));
        }catch(Exception e){
            Log.e(LOG_TAG,"Error loading image: "+e.toString());
        }
        //Add title
        ((TextView)rootView.findViewById(R.id.movieDetail_title)).setText(mMovieObject.getTitle());

        //Add overview
        ((TextView)rootView.findViewById(R.id.movieDetail_overview)).setText(mMovieObject.getOverview());

        //Add release date
        ((TextView)rootView.findViewById(R.id.movieDetail_releaseDate)).setText(mMovieObject.getReleaseDateString());

        //Add rating
        ((TextView)rootView.findViewById(R.id.movieDetail_rating)).setText("User Rating: "+mMovieObject.getUserRating());

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState){

        //Store bundle data
        if(mMovieObject!=null)
            outState.putParcelable(getString(R.string.movie_details_parce),mMovieObject);
        super.onSaveInstanceState(outState);
    }

}
