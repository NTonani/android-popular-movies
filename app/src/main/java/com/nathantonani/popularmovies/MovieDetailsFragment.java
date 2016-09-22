package com.nathantonani.popularmovies;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ntonani on 9/12/16.
 */
public class MovieDetailsFragment extends Fragment {

    private final String LOG_TAG = "MovieDetailsFragment";
    private Movie mMovieObject;
    public MovieDetailsFragment(){

    }

    /*
     * Lifecycle
     */

    @Override
    public void onPause(){
        Log.v(LOG_TAG,"onPause");
        super.onPause();
    }

    @Override
    public void onStop(){
        Log.v(LOG_TAG,"onStop");
        super.onStop();
    }

    @Override
    public void onResume(){
        Log.v(LOG_TAG,"onResume");
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        Log.v(LOG_TAG,"onSaveInstanceState --- outState == null : "+(outState==null));
        if(mMovieObject!=null)
            outState.putParcelable("detailsMovie",mMovieObject);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart(){
        Log.v(LOG_TAG,"onStart");
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_detail,container,false);

        Intent intent = getActivity().getIntent();

        if(intent!=null && intent.hasExtra("movie"))
            mMovieObject = intent.getExtras().getParcelable("movie");


        if(mMovieObject==null && savedInstanceState!=null && savedInstanceState.getParcelable("detailsMovie")!=null)
            mMovieObject = savedInstanceState.getParcelable("detailsMovie");

        if(mMovieObject==null) return rootView;

        //Add image
        //PicassoCache.getPicassoInstance(getActivity()).load(mMovieObject.getPosterPath()).into((ImageView)rootView.findViewById(R.id.movieDetail_thumbnail));
        //Picasso.with(getActivity()).setIndicatorsEnabled(true);
        Picasso.with(getActivity()).load(mMovieObject.getPosterPath()).into((ImageView)rootView.findViewById(R.id.movieDetail_thumbnail));

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
}
