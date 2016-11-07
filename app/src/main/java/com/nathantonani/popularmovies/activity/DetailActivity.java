package com.nathantonani.popularmovies.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.nathantonani.popularmovies.fragment.MovieDetailsFragment;
import com.nathantonani.popularmovies.R;

/**
 * Created by ntonani on 9/12/16.
 */
public class DetailActivity extends AppCompatActivity {

    private final String LOG_TAG = DetailActivity.class.getSimpleName();

    MovieDetailsFragment movieDetailsFragment;

    /*
     * Activity lifecycle
     */

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //Set view
        setContentView(R.layout.activity_detail);

        //Check fragment existence
        FragmentManager fm = getSupportFragmentManager();
        movieDetailsFragment = (MovieDetailsFragment)fm.findFragmentByTag(MovieDetailsFragment.class.getSimpleName());
        if(movieDetailsFragment==null){
            movieDetailsFragment = new MovieDetailsFragment();

            Bundle arguments = new Bundle();
            arguments.putParcelable(MovieDetailsFragment.MOVIE_URI,getIntent().getData());
            movieDetailsFragment.setArguments(arguments);

            fm.beginTransaction().add(R.id.container,movieDetailsFragment, MovieDetailsFragment.class.getSimpleName()).commit();
        }else
            movieDetailsFragment.getRetainInstance();

    }
}
