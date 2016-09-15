package com.nathantonani.popularmovies;

import android.content.Intent;
import android.media.audiofx.BassBoost;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = "MainActivity";

    private MovieGridFragment movieGridFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v("MainActivity","onCreate - empty instance:"+(savedInstanceState==null));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        movieGridFragment = (MovieGridFragment)fm.findFragmentByTag("MovieGridFragment");
        if(movieGridFragment==null){
            Log.v("MainActivity", " OnCreate -- Starting new fragment");
            movieGridFragment = new MovieGridFragment();
            fm.beginTransaction().add(R.id.container,movieGridFragment, "MovieGridFragment").commit();
        }else{
            Log.v(LOG_TAG,"Fragment exists");
            movieGridFragment.getRetainInstance();
        }
    }
    //TODO: Remove onSave
    @Override
    public void onSaveInstanceState(Bundle saveInstanceState){
        Log.v("MainActivity","onSaveInstanceState -- "+(saveInstanceState==null));
        super.onSaveInstanceState(saveInstanceState);
    }

    @Override
    public void onDestroy(){
        Log.v(LOG_TAG,"onDestroy");
        super.onDestroy();
    }

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

}
