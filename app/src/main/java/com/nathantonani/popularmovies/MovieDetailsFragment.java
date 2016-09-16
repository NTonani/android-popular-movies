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

/**
 * Created by ntonani on 9/12/16.
 */
public class MovieDetailsFragment extends Fragment {

    public MovieDetailsFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_detail,container,false);

        Intent intent = getActivity().getIntent();
        Movie movieObject = null;
        if(intent!=null && intent.hasExtra("movie")){
            movieObject = intent.getExtras().getParcelable("movie");
        }

        if(movieObject==null) return rootView;

        //Add image
        Picasso.with(getActivity()).load(movieObject.getPosterPath()).into((ImageView)rootView.findViewById(R.id.movieDetail_thumbnail));

        //Add title
        ((TextView)rootView.findViewById(R.id.movieDetail_title)).setText(movieObject.getTitle());

        //Add overview
        ((TextView)rootView.findViewById(R.id.movieDetail_overview)).setText(movieObject.getOverview());

        //Add release date
        ((TextView)rootView.findViewById(R.id.movieDetail_releaseDate)).setText(movieObject.getReleaseDateString());

        //Add rating
        ((TextView)rootView.findViewById(R.id.movieDetail_rating)).setText("User Rating: "+movieObject.getUserRating());

        return rootView;
    }
}
