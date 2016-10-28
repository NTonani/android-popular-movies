package com.nathantonani.popularmovies.activity;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.nathantonani.popularmovies.adapter.sync.MoviesSyncAdapter;
import com.nathantonani.popularmovies.fragment.MovieGridFragment;
import com.nathantonani.popularmovies.R;

public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private MovieGridFragment movieGridFragment;

    /*
     * Activity lifecycle
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set view
        setContentView(R.layout.activity_main);

        //Check fragment existence
        FragmentManager fm = getSupportFragmentManager();
        movieGridFragment = (MovieGridFragment)fm.findFragmentByTag(MovieGridFragment.class.getSimpleName());
        if(movieGridFragment==null){
            movieGridFragment = new MovieGridFragment();
            fm.beginTransaction().add(R.id.container,movieGridFragment, MovieGridFragment.class.getSimpleName()).commit();
        }else
            movieGridFragment.getRetainInstance();

        MoviesSyncAdapter.initializeSyncAdapter(this);

    }
}
