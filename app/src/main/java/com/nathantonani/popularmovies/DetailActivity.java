package com.nathantonani.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

/**
 * Created by ntonani on 9/12/16.
 */
public class DetailActivity extends AppCompatActivity {

    MovieDetailsFragment movieDetailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);

        FragmentManager fm = getSupportFragmentManager();
        movieDetailsFragment = (MovieDetailsFragment)fm.findFragmentByTag("MovieDetailsFragment");
        if(movieDetailsFragment==null){
            Log.v("DetailsActivity", "onCreate -- Starting new fragment");
            movieDetailsFragment = new MovieDetailsFragment();
            fm.beginTransaction().add(R.id.container,movieDetailsFragment, "MovieDetailsFragment").commit();
        }else{
            Log.v("DetailsActivity","Fragment exists");
            movieDetailsFragment.getRetainInstance();
        }
    }

    /*
    @Override
    public Intent getParentActivityIntent() {
        Intent intent = super.getParentActivityIntent();
        if (intent != null) {
            return intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        return intent;
    }*/

}
